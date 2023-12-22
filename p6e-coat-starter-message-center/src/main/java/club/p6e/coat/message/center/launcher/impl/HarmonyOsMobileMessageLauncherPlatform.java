//package club.p6e.coat.message.center.launcher;
//
//import club.p6e.coat.message.center.config.MobileMessageConfigModel;
//import club.p6e.coat.message.center.template.TemplateModel;
//import club.p6e.coat.message.center.utils.JsonUtil;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Function;
//
///**
// * HarmonyOS
// * 移动消息发送平台
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = HarmonyOsMobileMessageLauncherPlatform.class,
//        ignored = HarmonyOsMobileMessageLauncherPlatform.class
//)
//public class HarmonyOsMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {
//
//    private static final int MAX_RETRY = 3;
//    /**
//     * 最多的收件人长度
//     */
//    private static final int MAX_RECIPIENT_LENGTH = 300;
//
//    /**
//     * 客户端缓存对象
//     */
//    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>();
//
//    /**
//     * 注入日志对象
//     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(HarmonyOsMobileMessageLauncherPlatform.class);
//
//    private final RestTemplate restTemplate;
//
//    public HarmonyOsMobileMessageLauncherPlatform(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @Override
//    public String name() {
//        return "HARMONY";
//    }
//
//    @Override
//    public List<Function<Void, String>> execute(MobileMessageConfigModel config, TemplateModel template, List<String> recipients) {
//        final Client client = getClient(config);
//        final String clientAccessToken = client.getAccessToken();
//        final Config applicationConfig = JsonUtil.fromJson(config.applicationContent(), Config.class);
//        if (applicationConfig == null) {
//            throw new RuntimeException("[HW MESSAGE PUSH ERROR] >>> application config is null");
//        }
//        final String url = applicationConfig.getPushUrl();
//        final HttpHeaders headers = new HttpHeaders();
//        headers.set("Host", applicationConfig.getPushHost());
//        headers.set("Authorization", "Bearer " + clientAccessToken);
//        headers.set("Content-Type", "application/json;charset=UTF-8");
//
//        final Map<String, Object> params = new HashMap<>();
//        final Map<String, Object> paramMessage = new HashMap<>();
//        final Map<String, Object> paramAndroid = new HashMap<>();
//        final Map<String, Object> paramNotification = new HashMap<>();
//        final Map<String, Object> paramAndroidData = new HashMap<>();
//        final Map<String, Object> paramAndroidNotification = new HashMap<>();
//        final Map<String, Object> paramAndroidNotificationClickAction = new HashMap<>();
//        params.put("validate_only", false);
//        params.put("message", paramMessage);
//        paramMessage.put("android", paramAndroid);
//        paramMessage.put("notification", paramNotification);
//        paramNotification.put("title", template.title());
//        paramNotification.put("body", template.content());
//        paramAndroid.put("category", "WORK");
//        paramAndroid.put("urgency", "NORMAL");
//        paramAndroid.put("ttl", "86400s");
//        paramAndroid.put("collapse_key", -1);
//        paramAndroid.put("notification", paramAndroidNotification);
//        paramAndroid.put("data", JsonUtil.toJson(paramAndroidData));
//        paramAndroidNotification.put("title", template.title());
//        paramAndroidNotification.put("body", template.content());
//        paramAndroidNotification.put("click_action", paramAndroidNotificationClickAction);
//        paramAndroidNotificationClickAction.put("type", 3);
//
//        final int size = recipients.size();
//        final int stride = MAX_RECIPIENT_LENGTH;
//        final List<Function<Void, String>> result = new ArrayList<>();
//        for (int i = 0; i < size; i = i + stride) {
//            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
//            result.add((v) -> {
//                paramMessage.put("token", rs);
//                final PushResultModel model = push(
//                        url, headers, JsonUtil.toJson(params), 0);
//                return model == null ? null : model.getRequestId();
//            });
//        }
//        return result;
//    }
//
//
//    private Client getClient(MobileMessageConfigModel config) {
//        final String name = config.applicationName();
//        Client client = CLIENT_CACHE.get(name);
//        if (client == null) {
//            client = createClient(config);
//            CLIENT_CACHE.put(name, client);
//        }
//        return client;
//    }
//
//    private synchronized Client createClient(MobileMessageConfigModel config) {
//        final String name = config.applicationName();
//        Client client = CLIENT_CACHE.get(name);
//        if (client == null) {
//            final Config cg = JsonUtil.fromJson(config.applicationContent(), Config.class);
//            if (cg == null
//                    || cg.getAuthUrl() == null
//                    || cg.getAuthHost() == null
//                    || cg.getAuthClientId() == null
//                    || cg.getAuthClientSecret() == null
//                    || cg.getAuthContentType() == null) {
//                throw new RuntimeException("application content harmony is config error");
//            } else {
//                final Map<String, String> headers = new HashMap<>(2);
//                headers.put("Host", cg.getAuthHost());
//                headers.put("Content-Type", cg.getAuthContentType());
//                client = new Client(cg.getAuthUrl(), headers,
//                        "grant_type=client_credentials&client_id="
//                                + cg.getAuthClientId() + "&client_secret=" + cg.getAuthClientSecret());
//                CLIENT_CACHE.put(name, client);
//            }
//        }
//        return client;
//    }
//
//    private PushResultModel push(String url, HttpHeaders headers, String body, int retry) {
//        if (retry < MAX_RETRY) {
//            if (retry != 0) {
//                LOGGER.info("[HW MESSAGE] ( retry: " + retry + " )");
//            }
//            try {
//                final HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
//                LOGGER.info("[HW MESSAGE] url ::: " + url);
//                LOGGER.info("[HW MESSAGE] body ::: " + body);
//                LOGGER.info("[HW MESSAGE] headers ::: " + headers);
//                final ResponseEntity<String> responseEntity =
//                        restTemplate.postForEntity(url, requestEntity, String.class);
//                if (responseEntity.getStatusCode().value() == 200) {
//                    final String rBody = responseEntity.getBody();
//                    if (rBody != null) {
//                        final PushResultModel result = JsonUtil.fromJson(rBody, PushResultModel.class);
//                        if (result != null && result.getCode() != null && "80000000".equals(result.getCode())) {
//                            LOGGER.info("[HW MESSAGE] result :: " + result);
//                            return result;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                LOGGER.error("[HW MESSAGE]", e);
//            }
//            return push(url, headers, body, (retry + 1));
//        } else {
//            LOGGER.info("[HW MESSAGE] exceeded maximum retry count !!");
//            return null;
//        }
//    }
//
//    @Data
//    private static class Config implements Serializable {
//        private String authUrl;
//        private String authHost;
//        private String authClientId;
//        private String authClientSecret;
//        private String authContentType;
//        private String pushUrl;
//        private String pushHost;
//    }
//
//    @Data
//    private static class PushResultModel implements Serializable {
//        private String code;
//        private String msg;
//        private String requestId;
//    }
//
//    private static class Client {
//
//        @Data
//        @Accessors(chain = true)
//        private static class ResultModel implements Serializable {
//            @JsonProperty("token_type")
//            private String tokenType;
//            @JsonProperty("access_token")
//            private String accessToken;
//            @JsonProperty("expires_in")
//            private Integer expiresIn;
//        }
//
//        private static final RestTemplate REST_TEMPLATE = new RestTemplate();
//
//        private long date;
//        private String token;
//        private final String url;
//        private final String content;
//        private final Map<String, String> headers;
//
//        private static ResultModel getToken(String url, Map<String, String> headers, String body) {
//            final HttpHeaders httpHeaders = new HttpHeaders();
//            if (headers != null && !headers.isEmpty()) {
//                for (final String key : headers.keySet()) {
//                    httpHeaders.set(key, headers.get(key));
//                }
//            }
//            return getToken(url, httpHeaders, body, 0);
//        }
//
//        private static ResultModel getToken(String url, HttpHeaders headers, String body, int retry) {
//            if (retry < MAX_RETRY) {
//                if (retry != 0) {
//                    LOGGER.info("[HW TOKEN] ( retry: " + retry + " )");
//                }
//                try {
//                    LOGGER.info("[HW TOKEN] url ::: " + url + "?" + body);
//                    LOGGER.info("[HW TOKEN] body ::: " + body);
//                    LOGGER.info("[HW TOKEN] headers ::: " + headers);
//                    final ResponseEntity<String> responseEntity =
//                            REST_TEMPLATE.postForEntity(url + "?" + body, null, String.class);
//                    if (responseEntity.getStatusCode().value() == 200) {
//                        final String rBody = responseEntity.getBody();
//                        if (rBody != null) {
//                            final ResultModel result = JsonUtil.fromJson(rBody, ResultModel.class);
//                            if (result != null && result.getAccessToken() != null) {
//                                LOGGER.info("[HW TOKEN] result :: " + result);
//                                return result;
//                            }
//                        }
//                    }
//                    return getToken(url, headers, body, (retry + 1));
//                } catch (Exception e) {
//                    LOGGER.error("[HW TOKEN]", e);
//                }
//            } else {
//                LOGGER.info("[HW TOKEN] exceeded maximum retry count !!");
//            }
//            return null;
//        }
//
//        public Client(String url, Map<String, String> headers, String content) {
//            this.url = url;
//            this.headers = headers;
//            this.content = content;
//        }
//
//        public synchronized String getAccessToken() {
//            final long current = System.currentTimeMillis();
//            if (token == null || date < current) {
//                final ResultModel resultModel = getToken(url, headers, content);
//                if (resultModel.getAccessToken() == null) {
//                    throw new RuntimeException("harmony client get token error");
//                }
//                token = resultModel.getAccessToken();
//                date = current + (resultModel.getExpiresIn() * 1000);
//            }
//            return token;
//        }
//    }
//
//}
