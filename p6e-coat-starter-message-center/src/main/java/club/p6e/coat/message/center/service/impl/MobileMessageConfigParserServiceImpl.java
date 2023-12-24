package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.service.MobileMessageConfigParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MobileMessageConfigParserService.class,
        ignored = MobileMessageConfigParserServiceImpl.class
)
public abstract class MobileMessageConfigParserServiceImpl implements MobileMessageConfigParserService {

    @Override
    public MobileMessageConfigModel execute(ConfigModel config) {
        return new SimpleMobileMessageConfigModel(config) {{
            if (config.content() != null) {
                final Map<String, String> data = JsonUtil.fromJsonToMap(config.content(), String.class, String.class);
                if (data != null) {
                    setApplicationId(data.get("applicationId"));
                    setApplicationName(data.get("applicationName"));
                    setApplicationSecret(data.get("applicationSecret"));
                    setApplicationPlatform(data.get("applicationPlatform"));
                    setOther(data);
                }
            }
        }};
    }

    private static class SimpleMobileMessageConfigModel implements MobileMessageConfigModel, Serializable {
        private String applicationName;
        private String applicationId;
        private String applicationSecret;
        private String applicationPlatform;
        private Map<String, String> other = new HashMap<>();

        private final ConfigModel model;

        public SimpleMobileMessageConfigModel(ConfigModel model) {
            this.model = model;
        }

        @Override
        public int id() {
            return model == null ? 0 : model.id();
        }

        @Override
        public boolean enable() {
            return model != null && model.enable();
        }

        @Override
        public String name() {
            return model == null ? null : model.name();
        }

        @Override
        public MessageType type() {
            return model == null ? null : model.type();
        }

        @Override
        public String content() {
            return model == null ? null : model.content();
        }

        @Override
        public String description() {
            return model == null ? null : model.description();
        }

        @Override
        public String parser() {
            return model == null ? null : model.parser();
        }

        @Override
        public byte[] parserSource() {
            return model == null ? null : model.parserSource();
        }

        @Override
        public String rule() {
            return model == null ? null : model.rule();
        }


        @Override
        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        @Override
        public String getApplicationName() {
            return applicationName;
        }

        @Override
        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        @Override
        public String getApplicationId() {
            return applicationId;
        }

        @Override
        public void setApplicationSecret(String applicationSecret) {
            this.applicationSecret = applicationSecret;
        }

        @Override
        public String getApplicationSecret() {
            return applicationSecret;
        }

        @Override
        public void setApplicationPlatform(String applicationPlatform) {
            this.applicationPlatform = applicationPlatform;
        }

        @Override
        public String getApplicationPlatform() {
            return applicationPlatform;
        }

        @Override
        public void setOther(Map<String, String> other) {
            this.other = other;
        }

        @Override
        public Map<String, String> getOther() {
            return other;
        }
    }

}
