package club.p6e.coat.message.center.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 配置解析器（默认）
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ConfigParser.class,
        ignored = DefaultConfigParser.class
)
public class DefaultConfigParser implements ConfigParser {

    /**
     * MMS 类型
     */
    private static final String MMS_TYPE = "MMS";

    /**
     * SMS 类型
     */
    private static final String SMS_TYPE = "SMS";

    /**
     * MAIL 类型
     */
    private static final String MAIL_TYPE = "MAIL";

    /**
     * 外部数据源配置解析器
     */
    private final ExternalSourceConfigParser externalSourceConfigParser = new ExternalSourceConfigParser();

    @Override
    public ConfigData execute(ConfigSource config) {
        return switch (config.type()) {
            case MAIL_TYPE -> getMailConfigData(config);
            case SMS_TYPE -> getShortMessageConfigData(config);
            case MMS_TYPE -> getMobileMessageConfigData(config);
            default -> externalSourceConfigParser.execute(config);
        };
    }

    private MobileMessageConfigData getMobileMessageConfigData(ConfigSource config) {
        final String content = config.content();
        return new MobileMessageConfigData() {
            @Override
            public String applicationId() {
                return content;
            }

            @Override
            public String applicationKey() {
                return content;
            }

            @Override
            public String applicationSecret() {
                return content;
            }

            @Override
            public String applicationUrl() {
                return content;
            }

            @Override
            public String platform() {
                return null;
            }

            @Override
            public Integer id() {
                return config.id();
            }

            @Override
            public String name() {
                return config.name();
            }

            @Override
            public String type() {
                return config.type();
            }

            @Override
            public String content() {
                return config.content();
            }
        };
    }

    private ShortMessageConfigData getShortMessageConfigData(ConfigSource config) {
        final String content = config.content();
        return new ShortMessageConfigData() {
            @Override
            public String applicationId() {
                return content;
            }

            @Override
            public String applicationKey() {
                return content;
            }

            @Override
            public String applicationSecret() {
                return content;
            }

            @Override
            public String applicationUrl() {
                return content;
            }

            @Override
            public Integer id() {
                return config.id();
            }

            @Override
            public String name() {
                return config.name();
            }

            @Override
            public String type() {
                return config.type();
            }

            @Override
            public String content() {
                return config.content();
            }
        };
    }

    private MailConfigData getMailConfigData(ConfigSource config) {
        final String content = config.content();
        return new MailConfigData() {
            @Override
            public String port() {
                return content;
            }

            @Override
            public String host() {
                return content;
            }

            @Override
            public String auth() {
                return null;
            }

            @Override
            public String tls() {
                return null;
            }

            @Override
            public String from() {
                return null;
            }

            @Override
            public String password() {
                return null;
            }

            @Override
            public Map<String, String> other() {
                return null;
            }

            @Override
            public Integer id() {
                return config.id();
            }

            @Override
            public String name() {
                return config.name();
            }

            @Override
            public String type() {
                return config.type();
            }

            @Override
            public String content() {
                return config.content();
            }
        };
    }

}
