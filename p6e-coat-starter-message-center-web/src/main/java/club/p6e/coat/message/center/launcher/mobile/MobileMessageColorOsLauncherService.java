//package club.p6e.coat.message.center.launcher.mobile;
//
//import club.p6e.coat.common.utils.HttpUtil;
//import club.p6e.coat.common.utils.JsonUtil;
//import club.p6e.coat.common.utils.Sha256Util;
//import club.p6e.coat.message.center.MessageCenterThreadPool;
//import club.p6e.coat.message.center.config.mobile.MobileMessageConfigModel;
//import club.p6e.coat.message.center.launcher.LauncherResultModel;
//import club.p6e.coat.message.center.launcher.LauncherStartingModel;
//import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
//import club.p6e.coat.message.center.log.LogService;
//import club.p6e.coat.message.center.ExpiredCache;
//import club.p6e.coat.message.center.template.TemplateModel;
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
// * ColorOs 移动消息发射服务
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//public class MobileMessageColorOsLauncherService implements MobileMessageLauncherService {
//
//    /**
//     * 最多的收件人长度
//     */
//    public static int MAX_RECIPIENT_LENGTH = 300;
//
//    /**
//     * 默认的模板解析器名称
//     */
//    private static final String COLOR_OS_PARSER = "MOBILE_COLOR_OS";
//
//    /**
//     * 缓存类型
//     */
//    private static final String CACHE_TYPE = "MOBILE_COLOR_OS_CLIENT";
//
//    /**
//     * 注入日志对象
//     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(MobileMessageColorOsLauncherService.class);
//
//    /**
//     * 日志服务
//     */
//    private final LogService logService;
//
//    /**
//     * 消息中心线程池对象
//     */
//    private final MessageCenterThreadPool threadPool;
//
//    /**
//     * 构造方法初始化
//     *
//     * @param threadPool 消息中心线程池对象
//     */
//    public MobileMessageColorOsLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
//        this.logService = logService;
//        this.threadPool = threadPool;
//    }
//
//    @Override
//    public String name() {
//        return COLOR_OS_PARSER;
//    }
//
//    @Override
//    public LauncherResultModel execute(LauncherStartingModel starting, TemplateModel template, MobileMessageConfigModel config) {
//        return null;
//    }
//
//    @Override
//    public Map<String, List<String>> execute(List<String> recipients, LauncherTemplateModel template, MobileMessageConfigModel config) {
//        final int size = recipients.size();
//        final String content = template.getMessageContent();
//        final Map<String, List<String>> result = new HashMap<>(16);
//        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
//            final List<String> recipient = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
//            final Map<String, List<String>> ls = logService.create(recipient, template);
//            result.putAll(ls);
//            threadPool.submit(() -> {
//                try {
//                    LOGGER.info("[ MOBILE COLOR OS MESSAGE ] >>> start send mobile color os.");
//                    LOGGER.info("[ MOBILE COLOR OS MESSAGE ] >>> recipient: {}", recipient);
//                    LOGGER.info("[ MOBILE COLOR OS MESSAGE ] >>> template title: {}", template.getMessageTitle());
//                    LOGGER.info("[ MOBILE COLOR OS MESSAGE ] >>> template content: {}", template.getMessageContent());
//                    getClient(config).pushMessage(recipient, content);
//                    LOGGER.info("[ MOBILE COLOR OS MESSAGE ] >>> end send mobile color os.");
//                } catch (Exception e) {
//                    LOGGER.error("[ MOBILE COLOR OS MESSAGE ERROR ] >>> {}", e.getMessage());
//                } finally {
//                    logService.update(ls, "SUCCESS");
//                }
//            });
//        }
//        return result;
//    }
//
//    private Client getClient(MobileMessageConfigModel config) {
//        final String name = config.getApplicationName();
//        Client client = ExpiredCache.get(CACHE_TYPE, name);
//        if (client == null) {
//            client = createClient(config);
//        }
//        return client;
//    }
//
//    /**
//     * 创建 FirebaseApp 对象
//     *
//     * @param config 配置对象
//     */
//    private synchronized Client createClient(MobileMessageConfigModel config) {
//        final String name = config.getApplicationName();
//        Client client = ExpiredCache.get(CACHE_TYPE, name);
//        if (client == null) {
//            client = new Client(
//                    config.getApplicationId(),
//                    config.getApplicationKey(),
//                    config.getApplicationSecret(),
//                    config.getOther()
//            );
//        }
//        return client;
//    }
//
//
//    @SuppressWarnings("ALL")
//    private static class Client {
//
//        private static final long TOKEN_DATE = 2 * 60 * 6000 - 15 * 6000;
//
//        private long date;
//        private String token;
//        private final String id;
//        private final String key;
//        private final String secret;
//        private final Map<String, String> data;
//
//        private final String authUrl;
//        private final String messageUrl;
//
//        public Client(String id, String key, String secret, Map<String, String> data) {
//            this.id = id;
//            this.key = key;
//            this.secret = secret;
//            this.data = data;
//            authUrl = data.get("auth-url");
//            messageUrl = data.get("auth-url");
//        }
//
//        private void initToken() {
//            final long now = System.currentTimeMillis();
//            final String res = HttpUtil.doPost(authUrl, new HashMap<>() {{
//                put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//            }}, new HashMap<>() {{
//                put("app_key", key);
//                put("timestamp", String.valueOf(now));
//                put("sign", Sha256Util.execute((key + now + secret)));
//            }});
//            if (res == null) {
//                throw new RuntimeException();
//            } else {
//                final AuthResultModel result = JsonUtil.fromJson(res, AuthResultModel.class);
//                if (result != null
//                        && result.getCode() == 0
//                        && result.getData() != null
//                        && result.getData().getAuthToken() != null) {
//                    this.date = System.currentTimeMillis() + TOKEN_DATE;
//                    this.token = result.getData().getAuthToken();
//                } else {
//                    throw new RuntimeException();
//                }
//            }
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
//
//        public MessageResultModel pushMessage(List<String> recipients, String content) {
//            final String res = HttpUtil.doPost(messageUrl, new HashMap<>() {{
//                put("authToken", getToken());
//                put("Content-Type", "application/json; charset=utf-8");
//            }}, content);
//            if (res == null) {
//                throw new RuntimeException();
//            } else {
//                return JsonUtil.fromJson(res, MessageResultModel.class);
//            }
//        }
//
//        @Data
//        @Accessors(chain = true)
//        private static class AuthResultModel implements Serializable {
//            private Integer code;
//            private String message;
//            private AuthDataResultModel data;
//        }
//
//        @Data
//        @Accessors(chain = true)
//        private static class AuthDataResultModel implements Serializable {
//            @JsonProperty("auth_token")
//            private String authToken;
//        }
//
//        @Data
//        @Accessors(chain = true)
//        private static class MessageResultModel implements Serializable {
//            private Integer result;
//            private String desc;
//            private String taskId;
//        }
//    }
//
//}
