package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.MobileMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;
import club.p6e.coat.message.center.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * HarmonyOS
 * 移动消息发送平台
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = HarmonyOsMobileMessageLauncherPlatform.class,
        ignored = HarmonyOsMobileMessageLauncherPlatform.class
)
public class HarmonyOsMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {

    /**
     * 最多的收件人长度
     */
    private static final int MAX_RECIPIENT_LENGTH = 300;

    /**
     * 客户端缓存对象
     */
    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HarmonyOsMobileMessageLauncherPlatform.class);

    private static String push(String url, String body, HttpHeaders headers, int retry) {
        if (retry < 3) {
            if (retry != 0) {
                LOGGER.info("[HW_MPP >> MESSAGE] ( retry: " + retry + " )");
            }
            try {
                final HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                LOGGER.info("[HW_MPP >> MESSAGE] url ::: " + url);
                LOGGER.info("[HW_MPP >> MESSAGE] body ::: " + body);
                LOGGER.info("[HW_MPP >> MESSAGE] headers ::: " + headers);
                final RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
                final ResponseEntity<String> responseEntity =
                        restTemplate.postForEntity(url, requestEntity, String.class);
                if (responseEntity.getStatusCode().value() == 200) {
                    final String rBody = responseEntity.getBody();
                    if (rBody != null) {
                        final MessageResultModel result = JsonUtil.fromJson(rBody, MessageResultModel.class);
                        if (result != null && result.getCode() != null && "80000000".equals(result.getCode())) {
                            LOGGER.info("[HW_MPP >> MESSAGE] result :: " + result);
                            return result.getRequestId();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("[HW_MPP >> MESSAGE]", e);
            }
            return push(url, body, headers, (retry + 1));
        } else {
            LOGGER.info("[HW_MPP >> MESSAGE] Exceeded maximum retry count !!");
            return null;
        }
    }

    @Override
    public String name() {
        return "HARMONY";
    }

    @Override
    public List<Function<Void, String>> execute(MobileMessageConfigData config, TemplateData template, List<String> recipients) {
        final Client client = getClient(config);
        final String clientAccessToken = client.getAccessToken();


        final Map<String, String> utp = new HashMap<>();
        utp.put("project_client_id", huawei.get("project-client-id"));
        final String url = TemplateParser.execute(MESSAGES_URL, utp);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "push-api.cloud.huawei.com");
        headers.set("Authorization", "Bearer " + ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        final Map<String, Object> params = new HashMap<>();
        final Map<String, Object> paramMessage = new HashMap<>();
        final Map<String, Object> paramAndroid = new HashMap<>();
        final Map<String, Object> paramNotification = new HashMap<>();
        final Map<String, Object> paramAndroidData = new HashMap<>();
        final Map<String, Object> paramAndroidNotification = new HashMap<>();
        final Map<String, Object> paramAndroidNotificationClickAction = new HashMap<>();
        if (data != null && !data.isEmpty() && data.get("formDataId") != null) {
            paramAndroidData.put("smart_site_fid", data.get("formDataId"));
        }
        if (data != null && !data.isEmpty() && data.get("type") != null) {
            paramAndroidData.put("smart_site_type", data.get("type"));
        }
        params.put("validate_only", false);
        params.put("message", paramMessage);
        paramMessage.put("token", tokens);
        paramMessage.put("android", paramAndroid);
        paramMessage.put("notification", paramNotification);
        paramNotification.put("title", title);
        paramNotification.put("body", content);
        paramAndroid.put("category", "WORK");
        paramAndroid.put("urgency", "NORMAL");
        paramAndroid.put("ttl", "86400s");
        paramAndroid.put("collapse_key", -1);
        paramAndroid.put("notification", paramAndroidNotification);
        paramAndroid.put("data", JsonUtil.toJson(paramAndroidData));
        paramAndroidNotification.put("title", title);
        paramAndroidNotification.put("body", content);
        paramAndroidNotification.put("click_action", paramAndroidNotificationClickAction);
        paramAndroidNotificationClickAction.put("type", 3);

        final int size = recipients.size();
        final int stride = MAX_RECIPIENT_LENGTH;
        final List<Function<Void, String>> result = new ArrayList<>();
        for (int i = 0; i < size; i = i + stride) {
            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
            result.add((v) -> {
                paramMessage.put("token", rs);
                return push(url, JsonUtil.toJson(params), headers, 0);
            });
        }
        return result;
    }


    private Client getClient(MobileMessageConfigData config) {
        final String name = config.applicationName();
        Client client = CLIENT_CACHE.get(name);
        if (client == null) {
            client = createClient(config);
            CLIENT_CACHE.put(name, client);
        }
        return client;
    }

    private synchronized Client createClient(MobileMessageConfigData config) {
        final String name = config.applicationName();
        Client client = CLIENT_CACHE.get(name);
        if (client == null) {
            final Config cg = JsonUtil.fromJson(config.applicationContent(), Config.class);
            if (cg == null
                    || cg.getId() == null
                    || cg.getUrl() == null
                    || cg.getSecret() == null) {
                throw new RuntimeException("application content harmony is config error");
            } else {
                client = new Client(cg.getUrl(), cg.getId(), cg.getSecret());
                CLIENT_CACHE.put(name, client);
            }
        }
        return client;
    }

    @Data
    private static class Config implements Serializable {
        private String url;
        private String id;
        private String secret;
    }

    @Data
    @Accessors(chain = true)
    private static class TokenResultModel implements Serializable {
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Integer expiresIn;
    }

    private static class Client {

        private static final RestTemplate REST_TEMPLATE = new RestTemplate();

        private long date;
        private String token;
        private final String id;
        private final String url;
        private final String secret;

        private static String getToken(String url, String id, String secret) {
            final HttpHeaders headers = new HttpHeaders();
            headers.set("Host", "oauth-login.cloud.huawei.com");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            return getToken(url, "grant_type=client_credentials" +
                    "&client_id=" + id + "&client_secret=" + secret, headers, 0);
        }

        private static String getToken(String url, String body, HttpHeaders headers, int retry) {
            if (retry < 3) {
                if (retry != 0) {
                    LOGGER.info("[HW TOKEN] ( retry: " + retry + " )");
                }
                try {
                    LOGGER.info("[HW TOKEN] url ::: " + url + "?" + body);
                    LOGGER.info("[HW TOKEN] body ::: " + body);
                    LOGGER.info("[HW TOKEN] headers ::: " + headers);
                    final ResponseEntity<String> responseEntity =
                            REST_TEMPLATE.postForEntity(url + "?" + body, null, String.class);
                    if (responseEntity.getStatusCode().value() == 200) {
                        final String rBody = responseEntity.getBody();
                        if (rBody != null) {
                            final TokenResultModel result = JsonUtil.fromJson(rBody, TokenResultModel.class);
                            if (result != null && result.getAccessToken() != null) {
                                LOGGER.info("[HW TOKEN] ACCESS_TOKEN :: " + result.getAccessToken());
                                return result.getAccessToken();
                            }
                        }
                    }
                    return getToken(url, body, headers, (retry + 1));
                } catch (Exception e) {
                    LOGGER.error("[HW TOKEN]", e);
                }
            } else {
                LOGGER.info("[HW TOKEN] exceeded maximum retry count !!");
            }
            return null;
        }

        public Client(String url, String id, String secret) {
            this.id = id;
            this.url = url;
            this.secret = secret;
        }

        public synchronized String getAccessToken() {
            final long current = System.currentTimeMillis();
            if (token == null || date < current) {
                token = getToken(url, id, secret);
                if (token == null) {
                    throw new RuntimeException("harmony client get token error");
                }
                date = current + (3000 * 1000);
            }
            return token;
        }
    }

}
