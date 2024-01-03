//package club.p6e.coat.message.center.service.impl;
//
//import club.p6e.coat.common.utils.HttpUtil;
//import club.p6e.coat.common.utils.JsonUtil;
//import club.p6e.coat.common.utils.TemplateParser;
//import club.p6e.coat.message.center.MessageCenterThreadPool;
//import club.p6e.coat.message.center.model.MobileMessageConfigModel;
//import club.p6e.coat.message.center.model.TemplateMessageModel;
//import club.p6e.coat.message.center.service.MobileMessageLauncherService;
//import club.p6e.coat.message.center.ExpiredCache;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author lidashuang
// * @version 1.0
// */
//@Component
//public class MobileMessageLauncherServiceHarmonyOsImpl implements MobileMessageLauncherService {
//
//    private static final String TYPE = "HARMONY_OS";
//    /**
//     * 最多的收件人长度
//     */
//    public static final int MAX_RECIPIENT_LENGTH = 300;
//
//    /**
//     * 注入日志对象
//     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(MobileMessageLauncherServiceHarmonyOsImpl.class);
//
//    /**
//     * 消息中心线程池对象
//     */
//    private final MessageCenterThreadPool threadPool;
//
//    /**
//     * 构造方法注入
//     *
//     * @param threadPool 消息中心线程池对象
//     */
//    public MobileMessageLauncherServiceHarmonyOsImpl(MessageCenterThreadPool threadPool) {
//        this.threadPool = threadPool;
//    }
//
//    @Override
//    public String name() {
//        return "HARMONY_OS";
//    }
//
//
//    @Override
//    public List<String> execute(List<String> recipients, TemplateMessageModel template, MobileMessageConfigModel config) {
//        final String name = config.getApplicationName();
//        Client client = ExpiredCache.get(TYPE, name);
//        if (client == null) {
//            client = new Client(
//                    config.getApplicationId(),
//                    config.getApplicationKey(),
//                    config.getApplicationSecret(),
//                    config.getOther()
//            );
//            ExpiredCache.set(TYPE, name, client);
//        }
//        final Client finalClient = client;
//        final int size = recipients.size();
//        final String content = template.getMessageContent();
//        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
//            final List<String> rs = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
//            final String finalContent = TemplateParser.execute(content, new HashMap<>(1) {{
//                put("recipients", JsonUtil.toJson(rs));
//            }});
//            threadPool.submit(() -> {
//                try {
//                    finalClient.pushMessage(finalContent);
//                } catch (Exception e) {
//                    LOGGER.error("HARMONY_OS MMS ERROR >>> " + e.getMessage());
//                }
//            });
//        }
//        return recipients;
//    }
//
//    @SuppressWarnings("ALL")
//    private static class Client {
//
//        @Data
//        @Accessors(chain = true)
//        private static class AuthResultModel implements Serializable {
//            @JsonProperty("token_type")
//            private String tokenType;
//            @JsonProperty("access_token")
//            private String accessToken;
//            @JsonProperty("expires_in")
//            private Integer expiresIn;
//        }
//
//        private long date;
//        private String token;
//
//        private final String id;
//        private final String key;
//        private final String secret;
//        private final String authUrl;
//        private final String messageUrl;
//        private final String host;
//
//        public Client(String id, String key, String secret, Map<String, String> data) {
//            this.id = id;
//            this.key = key;
//            this.secret = secret;
//            this.authUrl = data.get("authUrl");
//            this.messageUrl = data.get("messageUrl");
//            this.host = data.get("host");
//        }
//
//        private void initToken() {
//            final String res = HttpUtil.doGet(authUrl, new HashMap<>(), new HashMap<>() {{
//                put("client_id", key);
//                put("client_secret", secret);
//                put("grant_type", "client_credentials");
//            }});
//            if (res == null) {
//                throw new RuntimeException();
//            } else {
//                final AuthResultModel result = JsonUtil.fromJson(res, AuthResultModel.class);
//                if (result != null
//                        && result.getExpiresIn() != null
//                        && result.getAccessToken() != null) {
//                    long expire = result.getExpiresIn() - 15 * 60;
//                    expire = expire <= 0 ? 0 : expire * 1000;
//                    this.date = System.currentTimeMillis() + expire;
//                    this.token = result.getAccessToken();
//                } else {
//                    throw new RuntimeException();
//                }
//            }
//
//        }
//
//        public String getToken() {
//            final long current = System.currentTimeMillis();
//            if (token == null || date < current) {
//                initToken();
//            }
//            return token;
//        }
//
//        public Object pushMessage(String content) {
//            final String res = HttpUtil.doPost(messageUrl, new HashMap<>() {{
//                put("Host", host);
//                put("Authorization", "Bearer " + getToken());
//                put("Content-Type", "application/json; charset=utf-8");
//            }}, content);
//            if (res == null) {
//                throw new RuntimeException();
//            } else {
//                return JsonUtil.fromJsonToMap(res, String.class, Object.class);
//            }
//        }
//
//    }
//}
