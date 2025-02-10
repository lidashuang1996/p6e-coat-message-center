package club.p6e.coat.message.center.launcher.wechat;

import club.p6e.coat.common.utils.*;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.wechat.WeChatMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.log.LogService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
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

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatMessageDefaultLauncherService.class);

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
    public WeChatMessageDefaultLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, WeChatMessageConfigModel config) {
        // segmentation send mail task
        final int size = ltm.getRecipients().size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = ltm.getRecipients().subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            threadPool.submit(() -> {
                try {
                    LOGGER.info("[ WECHAT LAUNCHER ] >>> START SEND WECHAT.");
                    LOGGER.info("[ WECHAT LAUNCHER ] >>> WECHAT CLIENT: {}", JsonUtil.toJson(config));
                    LOGGER.info("[ WECHAT LAUNCHER ] >>> WECHAT TEMPLATE: {}", ltm.getMessageTitle());
                    LOGGER.info("[ WECHAT LAUNCHER ] >>> WECHAT TEMPLATE CONTENT: {}", ltm.getMessageContent());
                    // execute the operation of sending telegram
                    send(client(config), recipient, ltm);
                } finally {
                    LOGGER.info("[ TELEGRAM LAUNCHER ] >>> END SEND TELEGRAM.");
                }
            });
        }
        return () -> 0;
    }

    /**
     * Send We Chat Message
     *
     * @param client     Client
     * @param recipients Recipients
     * @param template   Template
     */
    public void send(Client client, List<String> recipients, LauncherTemplateModel template) {
        if (template != null && template.getType() != null) {
            if ("TEMPLATE".equalsIgnoreCase(template.getType())) {
                final Client.TemplateMessageModel tmm = new Client.TemplateMessageModel();
                tmm.setTid(template.getMessageTitle());
                tmm.setUrl(template.getMessageParam().get("url"));
                tmm.setData(new HashMap<>() {{
                    for (final String key : template.getMessageParam().keySet()) {
                        if (key.startsWith("$")) {
                            put(key.substring(1), template.getMessageParam().get(key));
                        }
                    }
                }});
                if (template.getMessageParam().get("smallProgramId") != null) {
                    Client.SmallProgramModel spm = tmm.getSmallProgram();
                    if (spm == null) {
                        spm = new Client.SmallProgramModel()
                                .setId(template.getMessageParam().get("smallProgramId"));
                    }
                    tmm.setSmallProgram(spm);
                }
                if (template.getMessageParam().get("smallProgramPath") != null) {
                    Client.SmallProgramModel spm = tmm.getSmallProgram();
                    if (spm == null) {
                        spm = new Client.SmallProgramModel()
                                .setPath(template.getMessageParam().get("smallProgramPath"));
                    }
                    tmm.setSmallProgram(spm);
                }
                for (final String recipient : recipients) {
                    client.sendTemplateMessage(tmm.setOid(recipient));
                }
            } else if ("SUBSCRIPTION".equalsIgnoreCase(template.getType())) {
                final Client.SubscriptionMessageModel smm = new Client.SubscriptionMessageModel();
                if (template.getMessageParam().get("smallProgramId") != null) {
                    Client.SmallProgramModel spm = smm.getSmallProgram();
                    if (spm == null) {
                        spm = new Client.SmallProgramModel()
                                .setId(template.getMessageParam().get("smallProgramId"));
                    }
                    smm.setSmallProgram(spm);
                }
                smm.setData(new HashMap<>() {{
                    for (final String key : template.getMessageParam().keySet()) {
                        if (key.startsWith("$")) {
                            put(key.substring(1), template.getMessageParam().get(key));
                        }
                    }
                }});
                if (template.getMessageParam().get("smallProgramPath") != null) {
                    Client.SmallProgramModel spm = smm.getSmallProgram();
                    if (spm == null) {
                        spm = new Client.SmallProgramModel()
                                .setPath(template.getMessageParam().get("smallProgramPath"));
                    }
                    smm.setSmallProgram(spm);
                }
                smm.setTid(template.getMessageTitle());
                smm.setOid(template.getMessageParam().get("oid"));
                smm.setPage(template.getMessageParam().get("page"));
                client.sendSubscriptionMessage(smm);
            }
        }
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
        Client client = ExpiredCache.get(CACHE_TYPE, name);
        if (client == null) {
            Function<Void, String> tf = null;
            if (config.getAccessTokenUrl() != null) {
                tf = Client.getCustomUrlAccessToken(config.getAccessTokenUrl(), config.getOther());
            } else if (config.getApplicationId() != null && config.getApplicationSecret() != null) {
                tf = Client.getWeChatUrlAccessToken(config.getApplicationId(), config.getApplicationSecret());
            }
            client = new Client(tf);
            ExpiredCache.set(CACHE_TYPE, name, client);
        }
        return client;
    }

    /**
     * Client
     */
    public static class Client {

        /**
         * Access Token Callback Function
         */
        private final Function<Void, String> tf;

        /**
         * Construct Initialization
         *
         * @param tf Access Token Callback Function
         */
        public Client(Function<Void, String> tf) {
            this.tf = tf;
        }

        /**
         * Send Template Message
         *
         * @param model Template Message Model
         * @return Send Template Message Result
         */
        @SuppressWarnings("ALL")
        public boolean sendTemplateMessage(TemplateMessageModel model) {
            final String accessToken = tf.apply(null);
            final String result = HttpUtil.doPost(TemplateParser.execute(
                    "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=@{ACCESS_TOKEN}",
                    new HashMap<>() {{
                        put("ACCESS_TOKEN", accessToken);
                    }}
            ), new HashMap<>(), JsonUtil.toJson(new HashMap<String, Object>() {{
                put("touser", model.getOid());
                put("template_id", model.getTid());
                put("data", new HashMap<>() {{
                    model.getData().keySet().forEach(key -> put(key, new HashMap<>() {{
                        put("value", model.getData().get(key));
                    }}));
                }});
                if (model.getUrl() != null) {
                    put("url", model.getUrl());
                }
                if (model.getSmallProgram() != null) {
                    put("miniprogram", new HashMap<>() {{
                        put("appid", model.getSmallProgram().getId());
                        put("pagepath", model.getSmallProgram().getPath());
                    }});
                }
            }}));
            if (result != null) {
                try {
                    final Map<String, String> content = JsonUtil.fromJsonToMap(result, String.class, String.class);
                    if (content != null && content.get("errcode") != null
                            && content.get("errcode").equalsIgnoreCase("0")) {
                        return true;
                    }
                } catch (Exception e) {
                    // ignore exception
                }
            }
            return false;
        }

        /**
         * Send Subscription Message
         *
         * @param model Subscription Message Model
         * @return Send Subscription Message Result
         */
        @SuppressWarnings("ALL")
        public boolean sendSubscriptionMessage(SubscriptionMessageModel model) {
            final String accessToken = tf.apply(null);
            final String result = HttpUtil.doPost(TemplateParser.execute(
                    "https://api.weixin.qq.com/cgi-bin/message/subscribe/bizsend?access_token=@{ACCESS_TOKEN}",
                    new HashMap<>() {{
                        put("ACCESS_TOKEN", accessToken);
                    }}
            ), new HashMap<>(), JsonUtil.toJson(new HashMap<String, Object>() {{
                put("data", new HashMap<>() {{
                    model.getData().keySet().forEach(key -> put(key, new HashMap<>() {{
                        put("value", model.getData().get(key));
                    }}));
                }});
                put("touser", model.getOid());
                put("template_id", model.getTid());
                if (model.getPage() != null) {
                    put("page", model.getPage());
                }
                if (model.getSmallProgram() != null) {
                    put("miniprogram", new HashMap<>() {{
                        put("appid", model.getSmallProgram().getId());
                        put("pagepath", model.getSmallProgram().getPath());
                    }});
                }
            }}));
            if (result != null) {
                try {
                    final Map<String, String> content = JsonUtil.fromJsonToMap(result, String.class, String.class);
                    if (content != null && content.get("errcode") != null
                            && content.get("errcode").equalsIgnoreCase("0")) {
                        return true;
                    }
                } catch (Exception e) {
                    // ignore exception
                }
            }
            return false;
        }

        @SuppressWarnings("ALL")
        @Data
        @Accessors(chain = true)
        public static class TemplateMessageModel implements Serializable {
            private String oid;
            private String tid;
            private String url;
            private Map<String, String> data;
            private SmallProgramModel smallProgram;
        }

        @SuppressWarnings("ALL")
        @Data
        @Accessors(chain = true)
        public static class SubscriptionMessageModel implements Serializable {
            private String oid;
            private String tid;
            private String page;
            private Map<String, String> data;
            private SmallProgramModel smallProgram;
        }

        @SuppressWarnings("ALL")
        @Data
        @Accessors(chain = true)
        public static class SmallProgramModel implements Serializable {
            private String id;
            private String path;
        }

        /**
         * Get We Chat Url Access Token
         *
         * @param id     We Chat ID
         * @param secret We Chat Secret
         * @return Access Token Function
         */
        public static Function<Void, String> getWeChatUrlAccessToken(String id, String secret) {
            return new Function<>() {
                private String token;
                private long timestamp;

                @SuppressWarnings("ALL")
                @Override
                public String apply(Void unused) {
                    if (timestamp + 7000 * 1000L >= System.currentTimeMillis()) {
                        return token;
                    }
                    final String result = HttpUtil.doGet(TemplateParser.execute(
                            "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=@{APP_ID}&secret=@{APP_SECRET}",
                            new HashMap<>() {{
                                put("APP_ID", id);
                                put("APP_SECRET", secret);
                            }}
                    ));
                    final Map<String, String> content = JsonUtil.fromJsonToMap(result, String.class, String.class);
                    if (content != null && content.get("access_token") != null) {
                        token = content.get("access_token");
                        timestamp = System.currentTimeMillis();
                    }
                    return token;
                }
            };
        }

        /**
         * Get Custom Url Access Token
         *
         * @param url   Custom Url
         * @param other Custom Other
         * @return Access Token Function
         */
        public static Function<Void, String> getCustomUrlAccessToken(String url, Map<String, String> other) {
            return unused -> {
                final String result = HttpUtil.doGet(TemplateParser.execute(url, other == null ? new HashMap<>() : other));
                final Map<String, Object> content = JsonUtil.fromJsonToMap(result, String.class, Object.class);
                if (content != null
                        && content.get("code") != null
                        && TransformationUtil.objectToInteger(content.get("code")) == 0) {
                    return TransformationUtil.objectToString(content.get("data"));
                }
                return null;
            };
        }

    }


}
