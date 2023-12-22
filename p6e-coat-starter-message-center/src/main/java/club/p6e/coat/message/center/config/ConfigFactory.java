package club.p6e.coat.message.center.config;

import club.p6e.coat.message.center.DataSourceFactory;
import club.p6e.coat.message.center.infrastructure.repository.ConfigRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class ConfigFactory {

    private static final String MAIL_TYPE = "MAIL";
    private static final String SMS_TYPE = "SMS";
    private static final String MOBILE_TYPE = "MOBILE";

    private final Map<String, ConfigModel> CACHE = new ConcurrentHashMap<>();
    private final DataSourceFactory dataSourceFactory;
    private final MailMessageConfigParser mailMessageConfigParser;
    private final MobileMessageConfigParser mobileMessageConfigParser;
    private final ShortMessageConfigParser shortMessageConfigParser;

    public ConfigFactory(
            DataSourceFactory dataSourceFactory,
            MailMessageConfigParser mailMessageConfigParser,
            MobileMessageConfigParser mobileMessageConfigParser,
            ShortMessageConfigParser shortMessageConfigParser
    ) {
        this.dataSourceFactory = dataSourceFactory;
        this.mailMessageConfigParser = mailMessageConfigParser;
        this.mobileMessageConfigParser = mobileMessageConfigParser;
        this.shortMessageConfigParser = shortMessageConfigParser;
    }

    public ConfigModel executeReadData(String id) {
        ConfigModel result = executeReadCache(id);
        if (result == null) {
            result = executeReadDatabase(id);
            if (result != null) {
                CACHE.put(id, result);
            }
        }
        return result;
    }

    public ConfigModel executeReadCache(String id) {
        return CACHE.get(id);
    }

    public ConfigModel executeReadDatabase(String id) {
        return null;
    }

    public ConfigModel getConfigModel(Integer id) {
        return executeReadData(id);
    }

    public MailMessageConfigModel getMailMessageConfigModel(String id) {
        final ConfigModel model = executeReadData(id);
        if (model instanceof MailMessageConfigModel) {
            return (MailMessageConfigModel) model;
        } else {
            return null;
        }
    }

    public MobileMessageConfigModel getMobileMessageConfigModel(String id) {
        final ConfigModel model = executeReadData(id);
        if (model instanceof MobileMessageConfigModel) {
            return (MobileMessageConfigModel) model;
        } else {
            return null;
        }
    }

    public ShortMessageConfigModel getShortMessageConfigModel(String id) {
        final ConfigModel model = executeReadData(id);
        if (model instanceof ShortMessageConfigModel) {
            return (ShortMessageConfigModel) model;
        } else {
            return null;
        }
    }

}
