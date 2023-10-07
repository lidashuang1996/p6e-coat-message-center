package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.ShortMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;
import club.p6e.coat.message.center.utils.GeneratorUtil;
import club.p6e.coat.message.center.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ShortMessageLauncherParser.class,
        ignored = DefaultShortMessageLauncherParser.class
)
public class DefaultShortMessageLauncherParser implements ShortMessageLauncherParser {

    /**
     * 最多的收件人长度
     */
    private static final int MAX_RECIPIENT_LENGTH = 100;

    /**
     * 客户端缓存对象
     */
    private static final Map<String, com.aliyun.dysmsapi20170525.Client> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultShortMessageLauncherParser.class);

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法初始化
     *
     * @param threadPool 消息中心线程池对象
     */
    public DefaultShortMessageLauncherParser(MessageCenterThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public LauncherData execute(ShortMessageConfigData config, TemplateData template, List<String> recipients) {
        if (config == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found config data is null");
        }
        if (template == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found template data is null");
        }
        if (recipients == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found recipients is null");
        }
        if (template.title() == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found that the title in template data is null");
        }
        final com.aliyun.dysmsapi20170525.Client client;
        try {
            client = getAliYunClient(config);
        } catch (Exception e) {
            throw new RuntimeException("ALIYUN CLIENT ERROR", e);
        }
        // submit send mail task
        final int size = recipients.size();
        final int stride = MAX_RECIPIENT_LENGTH;
        for (int i = 0; i < size; i = i + stride) {
            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
            threadPool.submit(() -> send(client, config, template, rs));
        }
        return new LauncherData() {
            @Override
            public String id() {
                return GeneratorUtil.uuid();
            }

            @Override
            public String type() {
                return config.type();
            }
        };
    }

    /**
     * 获取客户端
     *
     * @param config 配置对象
     * @return 短信客户端对象
     * @throws Exception 异常对象
     */
    private com.aliyun.dysmsapi20170525.Client getAliYunClient(ShortMessageConfigData config) throws Exception {
        final String id = config.applicationKeyId();
        final String domain = config.applicationDomain();
        com.aliyun.dysmsapi20170525.Client client = CLIENT_CACHE.get(id + "@" + domain);
        if (client == null) {
            client = createAliYunClient(config);
            CLIENT_CACHE.put(id + "@" + domain, client);
        }
        return client;
    }

    /**
     * 创建客户端
     *
     * @param config 配置对象
     * @return 短信客户端对象
     * @throws Exception 异常对象
     */
    private synchronized com.aliyun.dysmsapi20170525.Client createAliYunClient(ShortMessageConfigData config) throws Exception {
        final String id = config.applicationKeyId();
        final String domain = config.applicationDomain();
        final String secret = config.applicationKeySecret();
        final com.aliyun.dysmsapi20170525.Client client = CLIENT_CACHE.get(id + "@" + domain);
        if (client == null) {
            final com.aliyun.teaopenapi.models.Config aliyunConfig = new com.aliyun.teaopenapi.models.Config();
            aliyunConfig.setAccessKeyId(id);
            aliyunConfig.setAccessKeySecret(secret);
            aliyunConfig.setEndpoint(domain);
            return new com.aliyun.dysmsapi20170525.Client(aliyunConfig);
        }
        return client;
    }

    /**
     * 发送短信
     *
     * @param client     客户端对象
     * @param config     配置对象
     * @param template   模板对象
     * @param recipients 收件人
     */
    private void send(
            com.aliyun.dysmsapi20170525.Client client,
            ShortMessageConfigData config,
            TemplateData template,
            List<String> recipients
    ) {
        // SMS CODE
        final String title = template.title();
        // SMS PARAMS
        final Map<String, String> params = new HashMap<>();
        if (template.variable() != null) {
            final Map<String, String> variable = template.variable();
            for (final String key : variable.keySet()) {
                params.put(key, template.convert(variable.get(key)));
            }
        }
        final com.aliyun.teautil.models.RuntimeOptions
                runtimeOptions = new com.aliyun.teautil.models.RuntimeOptions();
        final com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest
                sendBatchSmsRequest = new com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest();
        sendBatchSmsRequest.setSignNameJson(config.applicationSign());
        sendBatchSmsRequest.setPhoneNumberJson(JsonUtil.toJson(recipients));
        sendBatchSmsRequest.setTemplateCode(title);
        sendBatchSmsRequest.setTemplateParamJson(JsonUtil.toJson(params));
        try {
            client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtimeOptions);
        } catch (Exception e) {
            LOGGER.error("ALIYUN SMS SEND ERROR >>> " + e.getMessage());
        }
    }

}
