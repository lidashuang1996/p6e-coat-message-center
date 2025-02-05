package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.service.MobileMessageConfigParserService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MobileMessageConfigParserServiceImpl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MobileMessageConfigParserServiceImpl implements MobileMessageConfigParserService {

    /**
     * PARSER_NAME
     */
    private static final String PARSER_NAME = "MOBILE_CONFIG_DEFAULT_PARSER";

    @Override
    public String name() {
        return PARSER_NAME;
    }

    @Override
    public MobileMessageConfigModel execute(ConfigModel config) {
        final SimpleMobileMessageConfigModel model = new SimpleMobileMessageConfigModel(config);
        if (config.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(config.content(), String.class, Object.class);
            if (data != null) {
                model.setApplicationId(TransformationUtil.objectToString(data.get("applicationId")));
                model.setApplicationKey(TransformationUtil.objectToString(data.get("applicationKey")));
                model.setApplicationName(TransformationUtil.objectToString(data.get("applicationName")));
                model.setApplicationSecret(TransformationUtil.objectToString(data.get("applicationSecret")));
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
     * SimpleMobileMessageConfigModel
     */
    public static class SimpleMobileMessageConfigModel implements MobileMessageConfigModel, Serializable {

        /**
         * Application ID
         */
        private String applicationId;

        /**
         * Application Key
         */
        private String applicationKey;

        /**
         * Application Name
         */
        private String applicationName;

        /**
         * Application Secret
         */
        private String applicationSecret;

        /**
         * Other Data
         */
        private Map<String, String> other = new HashMap<>();

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Construct initialization
         * Inject Source Config Model Object
         *
         * @param source Source Config Model
         */
        public SimpleMobileMessageConfigModel(ConfigModel source) {
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
        public void setApplicationKey(String applicationKey) {
            this.applicationKey = applicationKey;
        }

        @Override
        public String getApplicationKey() {
            return this.applicationKey;
        }

        @Override
        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        @Override
        public String getApplicationName() {
            return this.applicationName;
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
