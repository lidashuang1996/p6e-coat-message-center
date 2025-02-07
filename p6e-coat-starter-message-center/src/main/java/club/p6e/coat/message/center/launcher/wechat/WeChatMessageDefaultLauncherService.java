package club.p6e.coat.message.center.launcher.wechat;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.config.sms.ShortMessageConfigModel;
import club.p6e.coat.message.center.config.wechat.WeChatMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WeChatMessageDefaultLauncherService implements WeChatMessageLauncherService {

    /**
     * Maximum recipient length
     */
    public static int MAX_RECIPIENT_LENGTH = 50;

    /**
     * Cache Type
     */
    protected static final String CACHE_TYPE = "WECHAT_CLIENT";

    /**
     * Launcher Name
     */
    private static final String DEFAULT_LAUNCHER_NAME = "WECHAT_DEFAULT_LAUNCHER";

    @Override
    public String name() {
        return DEFAULT_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, WeChatMessageConfigModel config) {
        return null;
    }

    /**
     * Get Client
     *
     * @param config Client Config
     * @return Client
     */
    protected Client client(WeChatMessageConfigModel config) {
        final String name = Md5Util.execute(Md5Util.execute(config.getApplicationId()));
        Client client = ExpiredCache.get(CACHE_TYPE, name);
        if (client == null) {
            client = client(name, config);
        }
        return client;
    }

    /**
     * Create Client
     *
     * @param name   Client Name
     * @param config Client Config
     * @return Client
     */
    protected synchronized Client client(String name, WeChatMessageConfigModel config) {
        final String id = config.getApplicationId();
        final String secret = config.getApplicationSecret();
        Client client = ExpiredCache.get(CACHE_TYPE, name);
        if (client == null) {
            ExpiredCache.set(CACHE_TYPE, name, client);
        }
        return client;
    }

    protected void execute(Client client, List<String> recipients, LauncherTemplateModel ltm) {
        try {
            final String title = ltm.getMessageTitle();
            final Map<String, String> params = ltm.getMessageParam();
            final com.aliyun.teautil.models.RuntimeOptions
                    runtimeOptions = new com.aliyun.teautil.models.RuntimeOptions();
            final models.SendBatchSmsRequest
                    sendBatchSmsRequest = new models.SendBatchSmsRequest();
            sendBatchSmsRequest.setTemplateCode(title);
            sendBatchSmsRequest.setTemplateParamJson(JsonUtil.toJson(params));
            sendBatchSmsRequest.setPhoneNumberJson(JsonUtil.toJson(recipients));
            client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtimeOptions);
        } catch (Exception e) {
            LOGGER.error("[ SMS LAUNCHER ] SEND ERROR >>> {}", e.getMessage());
        }
    }

    public static class Client {

        private final Function<Void, String> tf;

        public Client(Function<Void, String> tf) {
            this.tf = tf;
        }

        public String getAccessToken() {
            return tf.apply(null);
        }

        

    }


}
