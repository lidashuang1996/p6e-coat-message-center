//package club.p6e.coat.message.center.config;
//
//import club.p6e.coat.message.center.config.model.ConfigModel;
//import club.p6e.coat.message.center.config.model.MailMessageConfigModel;
//import club.p6e.coat.message.center.config.model.MobileMessageConfigModel;
//import club.p6e.coat.message.center.config.model.ShortMessageConfigModel;
//import club.p6e.coat.message.center.config.parser.ConfigParser;
//import club.p6e.coat.message.center.utils.JsonUtil;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 配置解析器（默认）
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = ConfigParser.class,
//        ignored = DefaultConfigParser.class
//)
//public abstract class DefaultConfigParser implements ConfigParser {
//
//    /**
//     * MMS 类型
//     */
//    private static final String MMS_TYPE = "MMS";
//
//    /**
//     * SMS 类型
//     */
//    private static final String SMS_TYPE = "SMS";
//
//    /**
//     * MAIL 类型
//     */
//    private static final String MAIL_TYPE = "MAIL";
//
//    /**
//     * 外部数据源配置解析器
//     */
//    private final ExternalSourceConfigParser externalSourceConfigParser = new ExternalSourceConfigParser();
//
//    @Override
//    public ConfigModel execute(ConfigSource config) {
//        return switch (config.type()) {
//            case MAIL_TYPE -> getMailConfigData(config);
//            case SMS_TYPE -> getShortMessageConfigData(config);
//            case MMS_TYPE -> getMobileMessageConfigData(config);
//            default -> externalSourceConfigParser.execute(config);
//        };
//    }
//
//    protected MobileMessageConfigModel getMobileMessageConfigData(ConfigSource config) {
//        final String content = config.content();
//        return new MobileMessageConfigModel() {
//            @Override
//            public String applicationName() {
//                return null;
//            }
//
//            @Override
//            public String applicationContent() {
//                return null;
//            }
//
//            @Override
//            public String platform() {
//                return null;
//            }
//
//            @Override
//            public Map<String, String> other() {
//                return null;
//            }
//
//            @Override
//            public Integer id() {
//                return config.id();
//            }
//
//            @Override
//            public String name() {
//                return config.name();
//            }
//
//            @Override
//            public String type() {
//                return config.type();
//            }
//
//            @Override
//            public String content() {
//                return config.content();
//            }
//        };
//    }
//
//    protected ShortMessageConfigModel getShortMessageConfigData(ConfigSource config) {
//        final String content = config.content();
//        return new ShortMessageConfigModel() {
//            @Override
//            public String applicationSign() {
//                return null;
//            }
//
//            @Override
//            public String applicationKeyId() {
//                return null;
//            }
//
//            @Override
//            public String applicationKeySecret() {
//                return null;
//            }
//
//            @Override
//            public String applicationDomain() {
//                return null;
//            }
//
//            @Override
//            public Map<String, String> other() {
//                return null;
//            }
//
//            @Override
//            public Integer id() {
//                return config.id();
//            }
//
//            @Override
//            public String name() {
//                return config.name();
//            }
//
//            @Override
//            public String type() {
//                return config.type();
//            }
//
//            @Override
//            public String content() {
//                return config.content();
//            }
//        };
//    }
//
//    protected MailMessageConfigModel getMailConfigData(ConfigSource config) {
//        String tls = null;
//        String port = null;
//        String host = null;
//        String auth = null;
//        String from = null;
//        String password = null;
//        final Map<String, String> other = new HashMap<>();
//        try {
//            final String content = config.content();
//            if (content != null) {
//                final Map<String, String> map =
//                        JsonUtil.fromJsonToMap(content, String.class, String.class);
//                if (map != null) {
//                    tls = map.get("tls");
//                    port = map.get("port");
//                    host = map.get("host");
//                    auth = map.get("auth");
//                    from = map.get("from");
//                    password = map.get("password");
//                    for (final String key : map.keySet()) {
//                        if (key.startsWith("@") && key.length() > 1) {
//                            other.put(key.substring(1), map.get(key));
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            // ...
//        }
//        final String finalTls = tls;
//        final String finalPort = port;
//        final String finalHost = host;
//        final String finalAuth = auth;
//        final String finalFrom = from;
//        final String finalPassword = password;
//        return new MailMessageConfigModel() {
//            @Override
//            public String port() {
//                return finalPort;
//            }
//
//            @Override
//            public String host() {
//                return finalHost;
//            }
//
//            @Override
//            public String auth() {
//                return finalAuth;
//            }
//
//            @Override
//            public String tls() {
//                return finalTls;
//            }
//
//            @Override
//            public String from() {
//                return finalFrom;
//            }
//
//            @Override
//            public String password() {
//                return finalPassword;
//            }
//
//            @Override
//            public Map<String, String> other() {
//                return other;
//            }
//
//            @Override
//            public Integer id() {
//                return config.id();
//            }
//
//            @Override
//            public String name() {
//                return config.name();
//            }
//
//            @Override
//            public String type() {
//                return config.type();
//            }
//
//            @Override
//            public String content() {
//                return config.content();
//            }
//        };
//    }
//
//}
