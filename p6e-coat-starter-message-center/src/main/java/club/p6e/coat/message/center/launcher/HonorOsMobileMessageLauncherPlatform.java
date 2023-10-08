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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * HonorOS
 * 移动消息发送平台
 * <a href="https://developer.hihonor.com/cn/kitdoc?category=%E5%9F%BA%E7%A1%80%E6%9C%8D%E5%8A%A1&kitId=11002&navigation=guides&docId=cloud-base-api.md&token=">...</a>
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = HonorOsMobileMessageLauncherPlatform.class,
        ignored = HonorOsMobileMessageLauncherPlatform.class
)
public class HonorOsMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {

    /**
     * 最多的收件人长度
     */
    private static final int MAX_RECIPIENT_LENGTH = 300;

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HarmonyOsMobileMessageLauncherPlatform.class);

    /**
     * 客户端缓存对象
     */
    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>();


    @Override
    public String name() {
        return "HONOR";
    }

    @Override
    public List<Function<Void, String>> execute(MobileMessageConfigData config, TemplateData template, List<String> recipients) {
        final Client client = getClient(config);
        final int size = recipients.size();
        final int stride = MAX_RECIPIENT_LENGTH;
        final List<Function<Void, String>> result = new ArrayList<>();
        for (int i = 0; i < size; i = i + stride) {
            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
            result.add((v) -> {
                final Client.PushResultModel model = client.pushMessage(template.content(), rs);
                return model == null ? null : String.valueOf(model.getCode());
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
            final ClientParamModel param = JsonUtil.fromJson(config.applicationContent(), ClientParamModel.class);
            if (param == null
                    || param.getAuthUrl() == null
                    || param.getAuthHost() == null
                    || param.getAppId() == null
                    || param.getPushUrl() == null
                    || param.getClientId() == null
                    || param.getClientSecret() == null) {
                throw new RuntimeException("application content honor value is null error");
            } else {
                client = new Client(param);
                CLIENT_CACHE.put(name, client);
            }
        }
        return client;
    }

    private static class Client {

        private static final int MAX_RETRY = 3;
        private static final RestTemplate REST_TEMPLATE = new RestTemplate();

        private long date;
        private String token;
        private final ClientParamModel param;

        public Client(ClientParamModel param) {
            this.param = param;
        }

        private Client.AuthResultModel getToken() {
            final String url = param.getAuthUrl();
            final HttpHeaders headers = new HttpHeaders();
            headers.set("Host", param.getAuthHost());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            return getToken(url, headers, "grant_type=client_credentials&client_id="
                    + param.getClientId() + "&client_secret=" + param.getClientSecret(), 0);
        }

        private Client.AuthResultModel getToken(String url, HttpHeaders headers, String body, int retry) {
            if (retry < MAX_RETRY) {
                if (retry != 0) {
                    LOGGER.info("[HONOR TOKEN] ( retry: " + retry + " )");
                }
                try {
                    LOGGER.info("[HONOR TOKEN] url ::: " + url + "?" + body);
                    LOGGER.info("[HONOR TOKEN] body ::: " + body);
                    LOGGER.info("[HONOR TOKEN] headers ::: " + headers);
                    final ResponseEntity<String> responseEntity =
                            REST_TEMPLATE.postForEntity(url + "?" + body, null, String.class);
                    if (responseEntity.getStatusCode().value() == 200) {
                        final String rBody = responseEntity.getBody();
                        if (rBody != null) {
                            final Client.AuthResultModel result = JsonUtil.fromJson(rBody, Client.AuthResultModel.class);
                            if (result != null && result.getAccessToken() != null) {
                                LOGGER.info("[HONOR TOKEN] result :: " + result);
                                return result;
                            }
                        }
                    }
                    return getToken(url, headers, body, (retry + 1));
                } catch (Exception e) {
                    LOGGER.error("[HONOR TOKEN]", e);
                }
            } else {
                LOGGER.info("[HONOR TOKEN] exceeded maximum retry count !!");
            }
            return null;
        }

        public synchronized String getAccessToken() {
            final long current = System.currentTimeMillis();
            if (token == null || date < current) {
                final Client.AuthResultModel authResultModel = getToken();
                if (authResultModel == null || authResultModel.getTokenType() == null) {
                    throw new RuntimeException("honor client get token error");
                }
                token = authResultModel.getAccessToken();
                date = current + ((authResultModel.getExpiresIn() - 900) * 1000L);
            }
            return token;
        }

        public Client.PushResultModel pushMessage(String content, List<String> recipients) {
            final String url = param.getPushUrl();
            final String token = getAccessToken();
            final HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            return pushMessage(url, headers, content, 0);
        }

        private Client.PushResultModel pushMessage(String url, HttpHeaders headers, String body, int retry) {
            if (retry < MAX_RETRY) {
                if (retry != 0) {
                    LOGGER.info("[HONOR PUSH] ( retry: " + retry + " )");
                }
                try {
                    LOGGER.info("[HONOR PUSH] url ::: " + url + "?" + body);
                    LOGGER.info("[HONOR PUSH] body ::: " + body);
                    LOGGER.info("[HONOR PUSH] headers ::: " + headers);
                    final HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                    final ResponseEntity<String> responseEntity =
                            REST_TEMPLATE.postForEntity(url, requestEntity, String.class);
                    if (responseEntity.getStatusCode().value() == 200) {
                        final String rBody = responseEntity.getBody();
                        if (rBody != null) {
                            final Client.PushResultModel result = JsonUtil.fromJson(rBody, Client.PushResultModel.class);
                            if (result != null && result.getCode() != null && result.getCode() == 200) {
                                LOGGER.info("[HONOR PUSH] result :: " + result);
                                return result;
                            }
                        }
                    }
                    return pushMessage(url, headers, body, (retry + 1));
                } catch (Exception e) {
                    LOGGER.error("[HONOR PUSH]", e);
                }
            } else {
                LOGGER.info("[HONOR PUSH] exceeded maximum retry count !!");
            }
            return null;
        }

        @Data
        @Accessors(chain = true)
        private static class AuthResultModel implements Serializable {
            @JsonProperty("token_type")
            private String tokenType;
            @JsonProperty("access_token")
            private String accessToken;
            @JsonProperty("expires_in")
            private Integer expiresIn;
        }

        @Data
        @Accessors(chain = true)
        private static class PushResultModel implements Serializable {
            private Integer code;
            private String message;
        }
    }

    @Data
    private static class ClientParamModel implements Serializable {
        private String authUrl;
        private String authHost;
        private String appId;
        private String clientId;
        private String clientSecret;
        private String pushUrl;
    }

}
