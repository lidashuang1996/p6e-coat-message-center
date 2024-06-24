package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.ShortMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.service.LogService;
import club.p6e.coat.message.center.service.ShortMessageLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认短消息发射服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class ShortMessageLauncherServiceImpl implements ShortMessageLauncherService {

    /**
     * 最大收件人长度
     */
    public static int MAX_RECIPIENT_LENGTH = 50;

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "SMS_DEFAULT";

    /**
     * 缓存类型
     */
    protected static final String CACHE_TYPE = "SMS_A_LI_YUN_CLIENT";

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortMessageLauncherServiceImpl.class);

    /**
     * 日志服务
     */
    private final LogService logService;

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法注入
     *
     * @param logService 日志服务对象
     * @param threadPool 消息中心线程池对象
     */
    public ShortMessageLauncherServiceImpl(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, ShortMessageConfigModel config) {
        final int size = recipients.size();
        final Map<String, List<String>> result = new HashMap<>(16);
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            final Map<String, List<String>> logData = logService.create(recipient, template);
            result.putAll(logData);
            threadPool.submit(() -> {
                try {
                    LOGGER.info("[ SMS MESSAGE ] >>> start send SMS.");
                    LOGGER.info("[ SMS MESSAGE ] >>> recipient: {}", recipient);
                    LOGGER.info("[ SMS MESSAGE ] >>> template title: {}", template.getMessageTitle());
                    LOGGER.info("[ SMS MESSAGE ] >>> template content: {}", template.getMessageContent());
                    execute(getClient(config), recipient, template);
                    LOGGER.info("[ MAIL MESSAGE ] >>> end send SMS.");
                } catch (Exception ignore) {
                    // ignore
                } finally {
                    logService.update(logData, "SUCCESS");
                }
            });
        }
        return result;
    }

    /**
     * 获取客户端
     *
     * @param config 配置对象
     * @return 短信客户端对象
     * @throws Exception 异常对象
     */
    protected com.aliyun.dysmsapi20170525.Client getClient(ShortMessageConfigModel config) throws Exception {
        com.aliyun.dysmsapi20170525.Client client = ExpiredCache.get(CACHE_TYPE, config.getApplicationName());
        if (client == null) {
            client = createClient(config);
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
    protected synchronized com.aliyun.dysmsapi20170525.Client createClient(ShortMessageConfigModel config) throws Exception {
        final String id = config.getApplicationId();
        final String name = config.getApplicationName();
        final String domain = config.getApplicationDomain();
        final String secret = config.getApplicationSecret();
        com.aliyun.dysmsapi20170525.Client client = ExpiredCache.get(CACHE_TYPE, name);
        if (client == null) {
            final com.aliyun.teaopenapi.models.Config aliyunConfig = new com.aliyun.teaopenapi.models.Config();
            aliyunConfig.setAccessKeyId(id);
            aliyunConfig.setAccessKeySecret(secret);
            aliyunConfig.setEndpoint(domain);
            client = new com.aliyun.dysmsapi20170525.Client(aliyunConfig);
            ExpiredCache.set(CACHE_TYPE, name, client);
        }
        return client;
    }

    protected void execute(com.aliyun.dysmsapi20170525.Client client, List<String> recipients, TemplateMessageModel template) throws Exception {
        try {
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
        } catch (Exception e) {
            LOGGER.error("[ SHORT MESSAGE SEND ERROR ]", e);
        }
    }

}
