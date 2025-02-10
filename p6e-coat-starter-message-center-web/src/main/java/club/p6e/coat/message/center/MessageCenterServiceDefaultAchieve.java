package club.p6e.coat.message.center;

import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.config.*;
import club.p6e.coat.message.center.config.mail.MailMessageConfigModel;
import club.p6e.coat.message.center.config.mobile.MobileMessageConfigModel;
import club.p6e.coat.message.center.config.sms.ShortMessageConfigModel;
import club.p6e.coat.message.center.config.telegram.TelegramMessageConfigModel;
import club.p6e.coat.message.center.config.wechat.WeChatMessageConfigModel;
import club.p6e.coat.message.center.error.*;
import club.p6e.coat.message.center.launcher.*;
import club.p6e.coat.message.center.launcher.mail.MailMessageLauncherService;
import club.p6e.coat.message.center.launcher.mobile.MobileMessageLauncherService;
import club.p6e.coat.message.center.launcher.sms.ShortMessageLauncherService;
import club.p6e.coat.message.center.launcher.telegram.TelegramMessageLauncherService;
import club.p6e.coat.message.center.launcher.wechat.WeChatMessageLauncherService;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.template.TemplateModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 发射机服务的实现类
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MessageCenterService.class,
        ignored = MessageCenterServiceDefaultAchieve.class
)
public class MessageCenterServiceDefaultAchieve implements MessageCenterService {

    /**
     * Default Language
     */
    public static String DEFAULT_LANGUAGE = "zh-cn";

    /**
     * Config Type
     */
    protected static final String CONFIG_TYPE = "CONFIG";

    /**
     * Template Type
     */
    protected static final String TEMPLATE_TYPE = "TEMPLATE";

    /**
     * Launcher Type
     */
    protected static final String LAUNCHER_TYPE = "LAUNCHER";

    /**
     * Data Source Repository
     */
    protected final DataSourceRepository repository;

    /**
     * Launcher Route Service Map
     */
    protected final Map<String, LauncherRouteService> launcherRouteServiceMap;

    /**
     * Config Parser Service Map
     */
    protected final Map<String, ConfigParserService<?>> configParserServiceMap;

    /**
     * Launcher Template Parser Service Map
     */
    protected final Map<String, LauncherTemplateParserService> launcherTemplateParserServiceMap;

    /**
     * Mail Message Launcher Service Map
     */
    protected final Map<String, MailMessageLauncherService> mailMessageLauncherServiceMap;

    /**
     * Short Message Launcher Service Map
     */
    protected final Map<String, ShortMessageLauncherService> shortMessageLauncherServiceMap;

    /**
     * Mobile Message Launcher Service Map
     */
    protected final Map<String, MobileMessageLauncherService> mobileMessageLauncherServiceMap;

    /**
     * WeChat Message Launcher Service Map
     */
    protected final Map<String, WeChatMessageLauncherService> wechatMessageLauncherServiceMap;

    /**
     * Telegram Message Launcher Service Map
     */
    protected final Map<String, TelegramMessageLauncherService> telegramMessageLauncherServiceMap;

    /**
     * Construct Initialization
     *
     * @param repository                         Data Source Repository
     * @param launcherRouteServiceList           Launcher Route Service List
     * @param configParserServiceList            Config Parser Service List
     * @param launcherTemplateParserServiceList  Launcher Template Parser Service List
     * @param mailMessageLauncherServiceList     Mail Message Launcher Service List
     * @param shortMessageLauncherServiceList    Short Message Launcher Service List
     * @param mobileMessageLauncherServiceList   Mobile Message Launcher Service List
     * @param weChatMessageLauncherServiceList   WeChat Message Launcher Service List
     * @param telegramMessageLauncherServiceList Telegram Message Launcher Service List
     */
    public MessageCenterServiceDefaultAchieve(
            DataSourceRepository repository,
            List<LauncherRouteService> launcherRouteServiceList,
            List<ConfigParserService<?>> configParserServiceList,
            List<LauncherTemplateParserService> launcherTemplateParserServiceList,
            List<MailMessageLauncherService> mailMessageLauncherServiceList,
            List<ShortMessageLauncherService> shortMessageLauncherServiceList,
            List<MobileMessageLauncherService> mobileMessageLauncherServiceList,
            List<WeChatMessageLauncherService> weChatMessageLauncherServiceList,
            List<TelegramMessageLauncherService> telegramMessageLauncherServiceList
    ) {
        final Map<String, LauncherRouteService> launcherRouteServiceMap = new HashMap<>();
        if (launcherRouteServiceList != null
                && !launcherRouteServiceList.isEmpty()) {
            for (final LauncherRouteService service : launcherRouteServiceList) {
                launcherRouteServiceMap.put(service.name(), service);
            }
        }

        final Map<String, ConfigParserService<?>> configParserServiceMap = new HashMap<>();
        if (configParserServiceList != null
                && !configParserServiceList.isEmpty()) {
            for (final ConfigParserService<?> service : configParserServiceList) {
                configParserServiceMap.put(service.name(), service);
            }
        }

        final Map<String, LauncherTemplateParserService> launcherTemplateParserServiceMap = new HashMap<>();
        if (launcherTemplateParserServiceList != null
                && !launcherTemplateParserServiceList.isEmpty()) {
            for (final LauncherTemplateParserService service : launcherTemplateParserServiceList) {
                launcherTemplateParserServiceMap.put(service.name(), service);
            }
        }

        final Map<String, MailMessageLauncherService> mailMessageLauncherServiceMap = new HashMap<>();
        if (mailMessageLauncherServiceList != null
                && !mailMessageLauncherServiceList.isEmpty()) {
            for (final MailMessageLauncherService service : mailMessageLauncherServiceList) {
                mailMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, ShortMessageLauncherService> shortMessageLauncherServiceMap = new HashMap<>();
        if (shortMessageLauncherServiceList != null
                && !shortMessageLauncherServiceList.isEmpty()) {
            for (final ShortMessageLauncherService service : shortMessageLauncherServiceList) {
                shortMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, MobileMessageLauncherService> mobileMessageLauncherServiceMap = new HashMap<>();
        if (mobileMessageLauncherServiceList != null
                && !mobileMessageLauncherServiceList.isEmpty()) {
            for (final MobileMessageLauncherService service : mobileMessageLauncherServiceList) {
                mobileMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, WeChatMessageLauncherService> wechatMessageLauncherServiceMap = new HashMap<>();
        if (mobileMessageLauncherServiceList != null
                && !mobileMessageLauncherServiceList.isEmpty()) {
            for (final WeChatMessageLauncherService service : weChatMessageLauncherServiceList) {
                wechatMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        final Map<String, TelegramMessageLauncherService> telegramMessageLauncherServiceMap = new HashMap<>();
        if (telegramMessageLauncherServiceList != null
                && !telegramMessageLauncherServiceList.isEmpty()) {
            for (final TelegramMessageLauncherService service : telegramMessageLauncherServiceList) {
                telegramMessageLauncherServiceMap.put(service.name(), service);
            }
        }

        this.repository = repository;
        this.configParserServiceMap = configParserServiceMap;
        this.launcherRouteServiceMap = launcherRouteServiceMap;
        this.launcherTemplateParserServiceMap = launcherTemplateParserServiceMap;
        this.mailMessageLauncherServiceMap = mailMessageLauncherServiceMap;
        this.shortMessageLauncherServiceMap = shortMessageLauncherServiceMap;
        this.mobileMessageLauncherServiceMap = mobileMessageLauncherServiceMap;
        this.wechatMessageLauncherServiceMap = wechatMessageLauncherServiceMap;
        this.telegramMessageLauncherServiceMap = telegramMessageLauncherServiceMap;
    }

    /**
     * Get Launcher Data
     *
     * @param id Launcher ID
     * @return Launcher Model
     */
    protected LauncherModel getLauncherData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final LauncherModel model = ExpiredCache.get(LAUNCHER_TYPE, name);
        if (model == null) {
            return setLauncherData(id);
        } else {
            return model;
        }
    }

    /**
     * Set Launcher Data
     *
     * @param id Launcher ID
     * @return Launcher Model
     */
    protected synchronized LauncherModel setLauncherData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final LauncherModel model = ExpiredCache.get(LAUNCHER_TYPE, name);
        if (model == null) {
            final LauncherModel lm = repository.getLauncherData(id);
            if (lm == null) {
                return null;
            } else {
                ExpiredCache.set(LAUNCHER_TYPE, name, lm);
                return lm;
            }
        } else {
            return model;
        }
    }

    /**
     * Get Config Data
     *
     * @param id Config ID
     * @return Config Model
     */
    protected ConfigModel getConfigData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final ConfigModel model = ExpiredCache.get(CONFIG_TYPE, name);
        if (model == null) {
            return setConfigData(id);
        } else {
            return model;
        }
    }

    /**
     * Set Config Data
     *
     * @param id Config ID
     * @return Config Model
     */
    protected ConfigModel setConfigData(Integer id) {
        final String name = Md5Util.execute(Md5Util.execute(String.valueOf(id)));
        final ConfigModel model = ExpiredCache.get(CONFIG_TYPE, name);
        if (model == null) {
            final ConfigModel cm = repository.getConfigData(id);
            if (cm == null) {
                return null;
            } else {
                ExpiredCache.set(CONFIG_TYPE, name, cm);
                return cm;
            }
        } else {
            return model;
        }
    }

    /**
     * Get Template Data
     *
     * @param key      Template Key
     * @param language Template Language
     * @return Template Model
     */
    protected TemplateModel getTemplateData(String key, String language) {
        language = (language == null || language.isEmpty()) ? DEFAULT_LANGUAGE : language;
        final String name = Md5Util.execute(Md5Util.execute(key + "@" + language));
        final TemplateModel model = ExpiredCache.get(TEMPLATE_TYPE, name);
        if (model == null) {
            return setTemplateData(key, language);
        } else {
            return model;
        }
    }

    /**
     * Set Template Data
     *
     * @param key      Template Key
     * @param language Template Language
     * @return Template Model
     */
    protected TemplateModel setTemplateData(String key, String language) {
        language = language == null ? DEFAULT_LANGUAGE : language;
        final String name = Md5Util.execute(Md5Util.execute(key + "@" + language));
        final TemplateModel model = ExpiredCache.get(TEMPLATE_TYPE, name);
        if (model == null) {
            final TemplateModel tm = repository.getTemplateData(key, language);
            if (tm == null) {
                return null;
            } else {
                ExpiredCache.set(TEMPLATE_TYPE, name, tm);
                return tm;
            }
        } else {
            return model;
        }
    }

    /**
     * Get Launcher Route Service
     *
     * @param launcher Launcher Model
     * @return Launcher Route Service
     */
    protected LauncherRouteService getLauncherRouteService(LauncherModel launcher) {
        final String route = launcher.route();
        if (route.startsWith("classname:")) {
            final byte[] classBytes = launcher.routeSource();
            final String className = route.substring(10);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, LauncherRouteService.class);
        } else {
            return launcherRouteServiceMap.get(route);
        }
    }

    /**
     * Get Config Parser Service
     *
     * @param config Config Model
     * @return Config Parser Service
     */
    protected ConfigParserService<?> getConfigParserService(ConfigModel config) {
        final String parser = config.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = config.parserSource();
            final String className = parser.substring(10);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, ConfigParserService.class);
        } else {
            return configParserServiceMap.get(parser);
        }
    }

    /**
     * Get Launcher Template Parser Service
     *
     * @param template Template Model
     * @return Launcher Template Parser Service
     */
    protected LauncherTemplateParserService getLauncherTemplateParserService(TemplateModel template) {
        final String parser = template.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = template.parserSource();
            final String className = parser.substring(10);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, LauncherTemplateParserService.class);
        } else {
            return launcherTemplateParserServiceMap.get(parser);
        }
    }

    /**
     * Get Launcher Service
     *
     * @param launcher Launcher Model
     * @param data     Launcher Service Data
     * @return Launcher Service
     */
    protected LauncherService<?> getLauncherService(LauncherModel launcher, Map<String, LauncherService<?>> data) {
        final String parser = launcher.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = launcher.parserSource();
            final String className = parser.substring(10);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, LauncherService.class);
        } else {
            return data.get(parser);
        }
    }

    /**
     * Get Mail Message Launcher Service
     *
     * @param launcher Launcher Model
     * @return Mail Message Launcher Service
     */
    protected MailMessageLauncherService getMailMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(mailMessageLauncherServiceMap));
        if (launcherService instanceof final MailMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(
                this.getClass(),
                "fun getMailMessageLauncherService(LauncherModel launcher).",
                "Launcher MAIL<" + launcher.parser() + "> service does not exist."
        );
    }

    /**
     * Get Short Message Launcher Service
     *
     * @param launcher Launcher Model
     * @return Short Message Launcher Service
     */
    protected ShortMessageLauncherService getShortMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(shortMessageLauncherServiceMap));
        if (launcherService instanceof final ShortMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(
                this.getClass(),
                "fun getShortMessageLauncherService(LauncherModel launcher).",
                "Launcher SMS<" + launcher.parser() + "> service does not exist."
        );
    }

    /**
     * Get Mobile Message Launcher Service
     *
     * @param launcher Launcher Model
     * @return Mobile Message Launcher Service
     */
    protected MobileMessageLauncherService getMobileMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(mobileMessageLauncherServiceMap));
        if (launcherService instanceof final MobileMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(
                this.getClass(),
                "fun getMobileMessageLauncherService(LauncherModel launcher).",
                "Launcher MOBILE<" + launcher.parser() + "> service does not exist."
        );
    }

    /**
     * Get WeChat Message Launcher Service
     *
     * @param launcher WeChat Message Launcher Service
     */
    protected WeChatMessageLauncherService getWeChatMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(wechatMessageLauncherServiceMap));
        if (launcherService instanceof final WeChatMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(
                this.getClass(),
                "fun getWeChatMessageLauncherService(LauncherModel launcher).",
                "Launcher WECHAT<" + launcher.parser() + "> service does not exist."
        );
    }

    /**
     * Get Telegram Message Launcher Service
     *
     * @param launcher Telegram Message Launcher Service
     */
    protected TelegramMessageLauncherService getTelegramMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(telegramMessageLauncherServiceMap));
        if (launcherService instanceof final TelegramMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(
                this.getClass(),
                "fun getTelegramMessageLauncherService(LauncherModel launcher).",
                "Launcher TELEGRAM<" + launcher.parser() + "> service does not exist."
        );
    }

    @Override
    public LauncherResultModel execute(LauncherStartingModel starting) {
        final LauncherModel launcherModel = getLauncherData(starting.id());
        if (launcherModel == null) {
            throw new LauncherNotExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] does not exist."
            );
        }
        if (!launcherModel.enable()) {
            throw new LauncherNotEnableException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] service not enabled."
            );
        }
        final List<ConfigModel> configs = new ArrayList<>();
        final List<LauncherModel.ConfigMapperModel> launcherConfigs = launcherModel.configs();
        if (launcherConfigs == null || launcherConfigs.isEmpty()) {
            throw new LauncherMapperConfigNotExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] mapper config result list [CONFIG NOT VERIFIED] value is empty or null."
            );
        } else {
            for (final LauncherModel.ConfigMapperModel config : launcherConfigs) {
                final ConfigModel cm = getConfigData(config.id());
                if (cm != null) {
                    configs.add(cm);
                }
            }
        }
        if (configs.isEmpty()) {
            throw new LauncherMapperConfigNotExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] mapper config result list [CONFIG VERIFIED] value is empty or null."
            );
        }
        final LauncherRouteService launcherRouteService = getLauncherRouteService(launcherModel);
        if (launcherRouteService == null) {
            throw new LauncherRouteConfigException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] route service does not exist."
            );
        }
        final ConfigModel selectConfigModel = launcherRouteService.execute(launcherModel, configs);
        if (selectConfigModel == null) {
            throw new LauncherRouteConfigException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] mapper route config result value is empty or null."
            );
        }
        final ConfigParserService<?> configParserService = getConfigParserService(selectConfigModel);
        if (configParserService == null) {
            throw new LauncherRouteConfigConvertException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] config parser service does not exist.");
        }
        final ConfigModel finalConfigModel = configParserService.execute(selectConfigModel);
        if (finalConfigModel == null) {
            throw new LauncherRouteConfigConvertException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] config parser service execute config value is empty or null."
            );
        }
        final TemplateModel finalTemplateModel = getTemplateData(launcherModel.template(), starting.language());
        if (finalTemplateModel == null) {
            throw new LauncherTemplateNotExistException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] mapper template("
                            + launcherModel.template() + "/" + starting.language() + ") value is empty or null.");
        }
        final LauncherTemplateParserService launcherTemplateParserService =
                getLauncherTemplateParserService(finalTemplateModel);
        if (launcherTemplateParserService == null) {
            throw new LauncherTemplateConvertException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] launcher template parser service does not exist.");
        }
        final LauncherTemplateModel finalLauncherTemplateModel =
                launcherTemplateParserService.execute(starting, finalTemplateModel);
        if (finalLauncherTemplateModel == null) {
            throw new LauncherTemplateConvertException(
                    this.getClass(),
                    "fun execute(LauncherStartingModel starting).",
                    "Launcher [" + starting.id() + "] mapper launcher template value is empty or null.");
        }
        switch (launcherModel.type()) {
            case SMS:
                if (finalConfigModel instanceof final ShortMessageConfigModel shortMessageConfigModel) {
                    return getShortMessageLauncherService(launcherModel)
                            .execute(finalLauncherTemplateModel, shortMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting).",
                            "Launcher [" + starting.id() + "]"
                                    + " SMS >>> " + ShortMessageConfigModel.class
                                    + " ::: " + finalConfigModel.getClass() + " type mismatch exception.");
                }
            case MAIL:
                if (finalConfigModel instanceof final MailMessageConfigModel mailMessageConfigModel) {
                    return getMailMessageLauncherService(launcherModel)
                            .execute(finalLauncherTemplateModel, mailMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting).",
                            "Launcher [" + starting.id() + "]"
                                    + " MAIL >>> " + MailMessageConfigModel.class
                                    + " ::: " + finalConfigModel.getClass() + " type mismatch exception."
                    );
                }
            case MOBILE:
                if (finalConfigModel instanceof final MobileMessageConfigModel mobileMessageConfigModel) {
                    return getMobileMessageLauncherService(launcherModel)
                            .execute(finalLauncherTemplateModel, mobileMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting).",
                            "Launcher [" + starting.id() + "]"
                                    + " MOBILE >>> " + MobileMessageConfigModel.class
                                    + " ::: " + finalConfigModel.getClass() + " type mismatch exception."
                    );
                }
            case WECHAT:
                if (finalConfigModel instanceof final WeChatMessageConfigModel weChatMessageConfigModel) {
                    return getWeChatMessageLauncherService(launcherModel)
                            .execute(finalLauncherTemplateModel, weChatMessageConfigModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting).",
                            "Launcher [" + starting.id() + "]"
                                    + " WECHAT >>> " + WeChatMessageConfigModel.class
                                    + " ::: " + finalConfigModel.getClass() + " type mismatch exception."
                    );
                }
            case TELEGRAM:
                if (finalConfigModel instanceof final TelegramMessageConfigModel templateMessageModel) {
                    return getTelegramMessageLauncherService(launcherModel)
                            .execute(finalLauncherTemplateModel, templateMessageModel);
                } else {
                    throw new LauncherConfigTypeMismatchException(
                            this.getClass(),
                            "fun execute(LauncherStartingModel starting).",
                            "Launcher [" + starting.id() + "]"
                                    + " TELEGRAM >>> " + TelegramMessageConfigModel.class
                                    + " ::: " + finalConfigModel.getClass() + " type mismatch exception."
                    );
                }
            default:
                throw new LauncherConfigTypeMismatchException(
                        this.getClass(),
                        "fun execute(LauncherStartingModel starting).",
                        "Launcher [" + starting.id() + "]"
                                + " TYPE >>> " + launcherModel.type().name()
                                + " ::: SMS/MAIL/MOBILE type mismatch exception."
                );
        }
    }

}
