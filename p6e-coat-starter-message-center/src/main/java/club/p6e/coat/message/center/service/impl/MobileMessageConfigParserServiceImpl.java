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
        value = MobileMessageConfigParserServiceImpl.class,
        ignored = MobileMessageConfigParserServiceImpl.class
)
public class MobileMessageConfigParserServiceImpl implements MobileMessageConfigParserService {

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "DEFAULT";

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public MobileMessageConfigModel execute(ConfigModel config) {
        return new SimpleMobileMessageConfigModel(config) {{
            if (config.content() != null) {
                final Map<String, String> data = JsonUtil.fromJsonToMap(config.content(), String.class, String.class);
                if (data != null) {
                    setApplicationId(data.get("applicationId"));
                    setApplicationKey(data.get("applicationKey"));
                    setApplicationName(data.get("applicationName"));
                    setApplicationSecret(data.get("applicationSecret"));
                    setOther(data);
                }
            }
        }};
    }

    /**
     * 简单邮件消息配置模型
     */
    private static class SimpleMobileMessageConfigModel implements MobileMessageConfigModel, Serializable {

        /**
         * 应用名称
         */
        private String applicationName;

        /**
         * 应用编号
         */
        private String applicationId;

        /**
         * 应用 KEY
         */
        private String applicationKey;

        /**
         * 应用密钥
         */
        private String applicationSecret;

        /**
         * 其他配置
         */
        private Map<String, String> other = new HashMap<>();

        /**
         * 源配置对象
         */
        private final ConfigModel model;

        /**
         * 构造方法注入源配置对象
         *
         * @param model 配置对象
         */
        public SimpleMobileMessageConfigModel(ConfigModel model) {
            this.model = model;
        }


        @Override
        public int id() {
            return model == null ? 0 : model.id();
        }

        @Override
        public String rule() {
            return model == null ? null : model.rule();
        }

        @Override
        public MessageType type() {
            return model == null ? null : model.type();
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
        public void setApplicationName(String name) {
            this.applicationName = name;
        }

        @Override
        public String getApplicationName() {
            return applicationName;
        }

        @Override
        public void setApplicationId(String id) {
            this.applicationId = id;
        }

        @Override
        public String getApplicationId() {
            return applicationId;
        }

        @Override
        public void setApplicationKey(String key) {
            this.applicationKey = key;
        }

        @Override
        public String getApplicationKey() {
            return applicationKey;
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
        public void setOther(Map<String, String> other) {
            this.other = other;
        }

        @Override
        public Map<String, String> getOther() {
            return other;
        }

    }

}
