package club.p6e.coat.message.center.launcher.sms;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.sms.ShortMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ShortMessageDefaultLauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class ShortMessageDefaultLauncherService implements ShortMessageLauncherService {

    /**
     * Maximum recipient length
     */
    public static int MAX_RECIPIENT_LENGTH = 50;

    /**
     * Cache Type
     */
    protected static final String CACHE_TYPE = "SMS_A_LI_YUN_CLIENT";

    /**
     * Launcher Name
     */
    private static final String DEFAULT_LAUNCHER_NAME = "SMS_DEFAULT_LAUNCHER";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortMessageDefaultLauncherService.class);

    /**
     * Log Service
     */
    protected final LogService logService;

    /**
     * Message Center Thread Pool Object
     */
    protected final MessageCenterThreadPool threadPool;

    /**
     * Construct Initialization
     *
     * @param logService Log Service
     * @param threadPool Thread Pool Object
     */
    public ShortMessageDefaultLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, ShortMessageConfigModel config) {
        // segmentation send mail task
        final int size = ltm.getRecipients().size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = ltm.getRecipients().subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            // submit sms sending tasks in the thread pool
            threadPool.submit(() -> {
                try {
                    LOGGER.info("[ SMS LAUNCHER ] >>> START SEND SMS.");
                    LOGGER.info("[ SMS LAUNCHER ] >>> SMS RECIPIENTS: {}", recipient);
                    LOGGER.info("[ SMS LAUNCHER ] >>> SMS CLIENT: {}", JsonUtil.toJson(config));
                    LOGGER.info("[ SMS LAUNCHER ] >>> SMS TEMPLATE TITLE: {}", ltm.getMessageTitle());
                    LOGGER.info("[ SMS LAUNCHER ] >>> SMS TEMPLATE PARAM: {}", ltm.getMessageParam());
                    execute(client(config), recipient, ltm);
                } finally {
                    LOGGER.info("[ SMS LAUNCHER ] >>> END SEND SMS.");
                }
            });
        }
        return () -> 0;
    }

    /**
     * Get Client
     *
     * @param config Client Config
     * @return Client
     */
    protected com.aliyun.dysmsapi20170525.Client client(ShortMessageConfigModel config) {
        try {
            final String name = Md5Util.execute(Md5Util.execute(config.getApplicationId()));
            com.aliyun.dysmsapi20170525.Client client = ExpiredCache.get(CACHE_TYPE, name);
            if (client == null) {
                client = client(name, config);
            }
            return client;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create Client
     *
     * @param name   Client Name
     * @param config Client Config
     * @return Client
     */
    protected synchronized com.aliyun.dysmsapi20170525.Client client(String name, ShortMessageConfigModel config) throws Exception {
        final String id = config.getApplicationId();
        final String domain = config.getApplicationDomain();
        final String secret = config.getApplicationSecret();
        com.aliyun.dysmsapi20170525.Client client = ExpiredCache.get(CACHE_TYPE, name);
        if (client == null) {
            final com.aliyun.teaopenapi.models.Config aliyunConfig = new com.aliyun.teaopenapi.models.Config();
            aliyunConfig.setAccessKeyId(id);
            aliyunConfig.setEndpoint(domain);
            aliyunConfig.setAccessKeySecret(secret);
            client = new com.aliyun.dysmsapi20170525.Client(aliyunConfig);
            ExpiredCache.set(CACHE_TYPE, name, client);
        }
        return client;
    }

    protected void execute(com.aliyun.dysmsapi20170525.Client client, List<String> recipients, LauncherTemplateModel ltm) {
        try {
            final String title = ltm.getMessageTitle();
            final Map<String, String> params = ltm.getMessageParam();
            final com.aliyun.teautil.models.RuntimeOptions
                    runtimeOptions = new com.aliyun.teautil.models.RuntimeOptions();
            final com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest
                    sendBatchSmsRequest = new com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest();
            sendBatchSmsRequest.setTemplateCode(title);
            sendBatchSmsRequest.setTemplateParamJson(JsonUtil.toJson(params));
            sendBatchSmsRequest.setPhoneNumberJson(JsonUtil.toJson(recipients));
            client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtimeOptions);
        } catch (Exception e) {
            LOGGER.error("[ SMS LAUNCHER ] SEND ERROR >>> {}", e.getMessage());
        }
    }

}
