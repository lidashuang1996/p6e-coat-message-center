package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.DataSourceFactory;
import club.p6e.coat.message.center.config.*;
import club.p6e.coat.message.center.template.CommunicationTemplateModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherFactory {

    private static final String MAIL_TYPE = "MAIL";
    private static final String SMS_TYPE = "SMS";
    private static final String MOBILE_TYPE = "MOBILE";

    private final Map<String, LauncherModel> CACHE = new ConcurrentHashMap<>();
    private final DataSourceFactory dataSourceFactory;
    private final MailMessageLauncherParser mailMessageLauncherParser;
    private final MobileMessageLauncherParser mobileMessageLauncherParser;
    private final ShortMessageLauncherParser shortMessageLauncherParser;

    private final LauncherRouteMatcher launcherRouteMatcher;

    public LauncherFactory(
            DataSourceFactory dataSourceFactory,
            MailMessageLauncherParser mailMessageLauncherParser,
            MobileMessageLauncherParser mobileMessageLauncherParser,
            ShortMessageLauncherParser shortMessageLauncherParser
    ) {
        this.dataSourceFactory = dataSourceFactory;
        this.mailMessageLauncherParser = mailMessageLauncherParser;
        this.mobileMessageLauncherParser = mobileMessageLauncherParser;
        this.shortMessageLauncherParser = shortMessageLauncherParser;
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

    public LauncherModel getModel(Integer id) {
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

    public ConfigModel performRouteMatching(LauncherModel launcher, List<ConfigModel> list) {
        return launcherRouteMatcher.execute(launcher, list);
    }

    public List<String> push(List<String> recipients, CommunicationTemplateModel communicationTemplateModel, ConfigModel routeConfigModel) {
    }
}
