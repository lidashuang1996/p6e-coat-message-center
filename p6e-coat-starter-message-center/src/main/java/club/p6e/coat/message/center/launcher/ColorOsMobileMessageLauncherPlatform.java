//package club.p6e.coat.message.center.launcher;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.Serializable;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Function;
//
///**
// * ColorOS
// * 移动消息发送平台
// * <a href="https://open.oppomobile.com/new/developmentDoc/info?id=11234">...</a>
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = ColorOsMobileMessageLauncherPlatform.class,
//        ignored = ColorOsMobileMessageLauncherPlatform.class
//)
//public class ColorOsMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {
//
//    /**
//     * 最多的收件人长度
//     */
//    private static final int MAX_RECIPIENT_LENGTH = 300;
//
//    /**
//     * 注入日志对象
//     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(HarmonyOsMobileMessageLauncherPlatform.class);
//
//    /**
//     * 客户端缓存对象
//     */
//    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>();
//
//    private static String sha256(String data) {
//        try {
//            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            final byte[] encoded = digest.digest(data.getBytes(StandardCharsets.UTF_8));
//            final StringBuilder hexString = new StringBuilder(2 * encoded.length);
//            for (final byte b : encoded) {
//                final String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            return null;
//        }
//    }
//
//    @Override
//    public String name() {
//        return "COLOR";
//    }
//
//    @Override
//    public List<Function<Void, String>> execute(MobileMessageConfigModel config, TemplateModel template, List<String> recipients) {
//        final Client client = getClient(config);
//        final int size = recipients.size();
//        final int stride = MAX_RECIPIENT_LENGTH;
//        final List<Function<Void, String>> result = new ArrayList<>();
//        for (int i = 0; i < size; i = i + stride) {
//            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
//            result.add((v) -> {
//                final Client.PushResultModel model = client.pushMessage(template.content(), rs);
//                return model == null ? null : model.getTaskId();
//            });
//        }
//        return result;
//    }
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
//            final ClientParamModel param = JsonUtil.fromJson(config.applicationContent(), ClientParamModel.class);
//            if (param == null
//                    || param.getAuthUrl() == null
//                    || param.getPushUrl() == null
//                    || param.getAppId() == null
//                    || param.getAppKey() == null
//                    || param.getAppSecret() == null) {
//                throw new RuntimeException("application content origin value is null error");
//            } else {
//                client = new Client(param);
//                CLIENT_CACHE.put(name, client);
//            }
//        }
//        return client;
//    }
//
//    private static class Client {
//
//        private static final int MAX_RETRY = 3;
//        private static final RestTemplate REST_TEMPLATE = new RestTemplate();
//
//        private long date;
//        private String token;
//        private final ClientParamModel param;
//
//        public Client(ClientParamModel param) {
//            this.param = param;
//        }
//
//        private Client.AuthResultModel getToken() {
//            final String url = param.getAuthUrl();
//            final long now = System.currentTimeMillis();
//            final HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            final Map<String, String> map = new HashMap<>();
//            map.put("app_key", param.getAppKey());
//            map.put("timestamp", String.valueOf(now));
//            map.put("sign", sha256(param.getAppKey() + now + param.getMasterSecret()));
//            return getToken(url, headers, JsonUtil.toJson(map), 0);
//        }
//
//        private Client.AuthResultModel getToken(String url, HttpHeaders headers, String body, int retry) {
//            if (retry < MAX_RETRY) {
//                if (retry != 0) {
//                    LOGGER.info("[COLOR TOKEN] ( retry: " + retry + " )");
//                }
//                try {
//                    LOGGER.info("[COLOR TOKEN] url ::: " + url + "?" + body);
//                    LOGGER.info("[COLOR TOKEN] body ::: " + body);
//                    LOGGER.info("[COLOR TOKEN] headers ::: " + headers);
//                    final HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
//                    final ResponseEntity<String> responseEntity =
//                            REST_TEMPLATE.postForEntity(url, requestEntity, String.class);
//                    if (responseEntity.getStatusCode().value() == 200) {
//                        final String rBody = responseEntity.getBody();
//                        if (rBody != null) {
//                            final Client.AuthResultModel result = JsonUtil.fromJson(rBody, Client.AuthResultModel.class);
//                            if (result != null && result.getCode() != null && result.getCode() == 0
//                                    && result.getData() != null && result.getData().getAuthToken() != null) {
//                                LOGGER.info("[COLOR TOKEN] result :: " + result);
//                                return result;
//                            }
//                        }
//                    }
//                    return getToken(url, headers, body, (retry + 1));
//                } catch (Exception e) {
//                    LOGGER.error("[COLOR TOKEN]", e);
//                }
//            } else {
//                LOGGER.info("[COLOR TOKEN] exceeded maximum retry count !!");
//            }
//            return null;
//        }
//
//        public synchronized String getAccessToken() {
//            final long current = System.currentTimeMillis();
//            if (token == null || date < current) {
//                final Client.AuthResultModel authResultModel = getToken();
//                if (authResultModel == null
//                        || authResultModel.getData() == null
//                        || authResultModel.getData().getAuthToken() == null) {
//                    throw new RuntimeException("color client get token error");
//                }
//                token = authResultModel.getData().getAuthToken();
//                date = current + (7200 * 1000);
//            }
//            return token;
//        }
//
//        public Client.PushResultModel pushMessage(String content, List<String> recipients) {
//            final String url = param.getPushUrl();
//            final String token = getAccessToken();
//            final HttpHeaders headers = new HttpHeaders();
//            headers.set("authToken", token);
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            return pushMessage(url, headers, content, 0);
//        }
//
//        private Client.PushResultModel pushMessage(String url, HttpHeaders headers, String body, int retry) {
//            if (retry < MAX_RETRY) {
//                if (retry != 0) {
//                    LOGGER.info("[COLOR PUSH] ( retry: " + retry + " )");
//                }
//                try {
//                    LOGGER.info("[COLOR PUSH] url ::: " + url);
//                    LOGGER.info("[COLOR PUSH] body ::: " + body);
//                    LOGGER.info("[COLOR PUSH] headers ::: " + headers);
//                    final HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
//                    final ResponseEntity<String> responseEntity =
//                            REST_TEMPLATE.postForEntity(url, requestEntity, String.class);
//                    if (responseEntity.getStatusCode().value() == 200) {
//                        final String rBody = responseEntity.getBody();
//                        if (rBody != null) {
//                            final Client.PushResultModel result = JsonUtil.fromJson(rBody, Client.PushResultModel.class);
//                            if (result != null && result.getResult() != null && result.getResult() == 0) {
//                                LOGGER.info("[COLOR PUSH] result :: " + result);
//                                return result;
//                            }
//                        }
//                    }
//                    return pushMessage(url, headers, body, (retry + 1));
//                } catch (Exception e) {
//                    LOGGER.error("[COLOR PUSH]", e);
//                }
//            } else {
//                LOGGER.info("[COLOR PUSH] exceeded maximum retry count !!");
//            }
//            return null;
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
//        private static class PushResultModel implements Serializable {
//            private Integer result;
//            private String desc;
//            private String taskId;
//        }
//    }
//
//    @Data
//    private static class ClientParamModel implements Serializable {
//        private String authUrl;
//        private String appId;
//        private String appKey;
//        private String appSecret;
//        private String masterSecret;
//        private String pushUrl;
//    }
//}
