package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.WeChatMessageConfigModel;
import club.p6e.coat.message.center.service.WeChatMessageConfigParserService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * WeChatMessageConfigParserServiceImpl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WeChatMessageConfigParserServiceImpl implements WeChatMessageConfigParserService {

    /**
     * PARSER_NAME
     */
    private static final String PARSER_NAME = "WECHAT_CONFIG_DEFAULT_PARSER";

    @Override
    public String name() {
        return PARSER_NAME;
    }

    @Override
    public WeChatMessageConfigModel execute(ConfigModel cm) {
        final SimpleWeChatMessageConfigModel model = new SimpleWeChatMessageConfigModel(cm);
        if (cm.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(cm.content(), String.class, Object.class);
            if (data != null) {
                model.setApplicationId(TransformationUtil.objectToString(data.get("applicationId")));
                model.setApplicationSecret(TransformationUtil.objectToString(data.get("applicationSecret")));
                model.setAccessTokenUrl(TransformationUtil.objectToString(data.get("accessTokenUrl")));
                model.setAccessTokenCacheName(TransformationUtil.objectToString(data.get("accessTokenCacheName")));
                final Map<String, String> other = new HashMap<>();
                for (final String key : data.keySet()) {
                    other.put(key, TransformationUtil.objectToString(data.get(key)));
                }
                model.setOther(other);
            }
        }
        return model;
    }

    /**
     * SimpleWeChatMessageConfigModel
     */
    public static class SimpleWeChatMessageConfigModel implements WeChatMessageConfigModel, Serializable {

        /**
         * Application ID
         */
        private String applicationId;

        /**
         * Application Secret
         */
        private String applicationSecret;

        /**
         * Access Token Url
         */
        private String accessTokenUrl;

        /**
         * Access Token Cache Name
         */
        private String accessTokenCacheName;

        /**
         * Other Data
         */
        public Map<String, String> other = Collections.unmodifiableMap(new HashMap<>());

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Construct Initialization
         * Inject Source Config Model Object
         *
         * @param source Source Config Model
         */
        public SimpleWeChatMessageConfigModel(ConfigModel source) {
            this.source = source;
        }

        @Override
        public int id() {
            return this.source == null ? 0 : this.source.id();
        }

        @Override
        public String rule() {
            return this.source == null ? null : this.source.rule();
        }

        @Override
        public MessageType type() {
            return this.source == null ? null : this.source.type();
        }

        @Override
        public boolean enable() {
            return this.source != null && this.source.enable();
        }

        @Override
        public String name() {
            return this.source == null ? null : this.source.name();
        }

        @Override
        public String content() {
            return this.source == null ? null : this.source.content();
        }

        @Override
        public String description() {
            return this.source == null ? null : this.source.description();
        }

        @Override
        public String parser() {
            return this.source == null ? null : this.source.parser();
        }

        @Override
        public byte[] parserSource() {
            return this.source == null ? null : this.source.parserSource();
        }

        @Override
        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        @Override
        public String getApplicationId() {
            return this.applicationId;
        }

        @Override
        public void setApplicationSecret(String applicationSecret) {
            this.applicationSecret = applicationSecret;
        }

        @Override
        public String getApplicationSecret() {
            return this.applicationSecret;
        }

        @Override
        public void setAccessTokenUrl(String accessTokenUrl) {
            this.accessTokenUrl = accessTokenUrl;
        }

        @Override
        public String getAccessTokenUrl() {
            return this.accessTokenUrl;
        }

        @Override
        public void setAccessTokenCacheName(String accessTokenCacheName) {
            this.accessTokenCacheName = accessTokenCacheName;
        }

        @Override
        public String getAccessTokenCacheName() {
            return this.accessTokenCacheName;
        }

        @Override
        public void setOther(Map<String, String> other) {
            if (other != null) {
                this.other = Collections.unmodifiableMap(other);
            }
        }

        @Override
        public Map<String, String> getOther() {
            return other;
        }

    }

}
