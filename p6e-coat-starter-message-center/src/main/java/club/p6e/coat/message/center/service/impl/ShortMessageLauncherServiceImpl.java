package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.ShortMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.service.ShortMessageLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class ShortMessageLauncherServiceImpl implements ShortMessageLauncherService {

    /**
     * 最大收件人长度
     */
    public static final int MAX_RECIPIENT_LENGTH = 100;

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortMessageLauncherServiceImpl.class);

    /**
     * 客户端缓存对象
     */
    private static final Map<String, com.aliyun.dysmsapi20170525.Client> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法注入
     *
     * @param threadPool 消息中心线程池对象
     */
    public ShortMessageLauncherServiceImpl(MessageCenterThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return "DEFAULT";
    }

    @Override
    public List<String> execute(List<String> recipients, TemplateMessageModel template, ShortMessageConfigModel config) {
        final int size = recipients.size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> rs = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            threadPool.submit(() -> {
                try {
                    execute(getClient(config), rs, template);
                } catch (Exception e) {
                    LOGGER.error("ALI_YUN SMS CONFIG ERROR >>> " + e.getMessage());
                }
            });
        }
        return recipients;
    }


    /**
     * 获取客户端
     *
     * @param config 配置对象
     * @return 短信客户端对象
     * @throws Exception 异常对象
     */
    private com.aliyun.dysmsapi20170525.Client getClient(ShortMessageConfigModel config) throws Exception {
        final String name = config.getApplicationName();
        com.aliyun.dysmsapi20170525.Client client = CLIENT_CACHE.get(name);
        if (client == null) {
            client = createClient(config);
            CLIENT_CACHE.put(name, client);
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
    private synchronized com.aliyun.dysmsapi20170525.Client createClient(ShortMessageConfigModel config) throws Exception {
        final String id = config.getApplicationId();
        final String name = config.getApplicationName();
        final String domain = config.getApplicationDomain();
        final String secret = config.getApplicationSecret();
        final com.aliyun.dysmsapi20170525.Client client = CLIENT_CACHE.get(name);
        if (client == null) {
            final com.aliyun.teaopenapi.models.Config aliyunConfig = new com.aliyun.teaopenapi.models.Config();
            aliyunConfig.setAccessKeyId(id);
            aliyunConfig.setAccessKeySecret(secret);
            aliyunConfig.setEndpoint(domain);
            return new com.aliyun.dysmsapi20170525.Client(aliyunConfig);
        }
        return client;
    }

    private void execute(com.aliyun.dysmsapi20170525.Client client, List<String> recipients, TemplateMessageModel template) throws Exception {
        final String title = template.getMessageTitle();
        final Map<String, String> params = template.getMessageParam();
        final com.aliyun.teautil.models.RuntimeOptions
                runtimeOptions = new com.aliyun.teautil.models.RuntimeOptions();
        final com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest
                sendBatchSmsRequest = new com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest();
        sendBatchSmsRequest.setTemplateCode(title);
        sendBatchSmsRequest.setTemplateParamJson(JsonUtil.toJson(params));
        sendBatchSmsRequest.setPhoneNumberJson(JsonUtil.toJson(recipients));
        client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtimeOptions);
    }

}
