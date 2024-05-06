package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.ExternalSourceClassLoader;
import club.p6e.coat.message.center.error.*;
import club.p6e.coat.message.center.model.*;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.service.*;
import club.p6e.coat.message.center.ExpiredCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 发射机服务的实现类
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = TransmitterService.class,
        ignored = TransmitterServiceImpl.class
)
public class TransmitterServiceImpl implements TransmitterService {

    /**
     * 默认的语言
     */
    public static String DEFAULT_LANGUAGE = "zh-cmn-Hans-CN";

    /**
     * 配置类型
     */
    protected static final String CONFIG_TYPE = "CONFIG";

    /**
     * 模板类型
     */
    protected static final String TEMPLATE_TYPE = "TEMPLATE";

    /**
     * 发射器类型
     */
    protected static final String LAUNCHER_TYPE = "LAUNCHER";

    /**
     * 数据源仓库对象
     */
    protected final DataSourceRepository repository;

    /**
     * 配置解析器服务
     */
    protected final Map<String, ConfigParserService> configParserServiceMap;

    /**
     * 模板解析器服务
     */
    protected final Map<String, TemplateParserService> templateParserServiceMap;

    /**
     * 发射器路由服务
     */
    protected final Map<String, LauncherRouteService> launcherRouteServiceMap;

    /**
     * MAIL 类型消息发射器
     */
    protected final Map<String, MailMessageLauncherService> mailMessageLauncherServiceMap;

    /**
     * SMS 类型消息发射器
     */
    protected final Map<String, ShortMessageLauncherService> shortMessageLauncherServiceMap;

    /**
     * MMS 类型消息发射器
     */
    protected final Map<String, MobileMessageLauncherService> mobileMessageLauncherServiceMap;


    /**
     * 构造方法初始化
     *
     * @param repository                       数据源仓库对象
     * @param configParserServiceList          配置解析器服务
     * @param templateParserServiceList        配置解析器服务
     * @param launcherRouteServiceList         发射器路由服务
     * @param mailMessageLauncherServiceList   MAIL 类型消息发射器
     * @param shortMessageLauncherServiceList  SMS 类型消息发射器
     * @param mobileMessageLauncherServiceList MMS 类型消息发射器
     */
    public TransmitterServiceImpl(
            DataSourceRepository repository,
            List<ConfigParserService> configParserServiceList,
            List<TemplateParserService> templateParserServiceList,
            List<LauncherRouteService> launcherRouteServiceList,
            List<MailMessageLauncherService> mailMessageLauncherServiceList,
            List<ShortMessageLauncherService> shortMessageLauncherServiceList,
            List<MobileMessageLauncherService> mobileMessageLauncherServiceList
    ) {
        final Map<String, ConfigParserService> configParserServiceMap = new HashMap<>();
        if (configParserServiceList != null
                && !configParserServiceList.isEmpty()) {
            for (final ConfigParserService service : configParserServiceList) {
                configParserServiceMap.put(service.name(), service);
            }
        }

        final Map<String, LauncherRouteService> launcherRouteServiceMap = new HashMap<>();
        if (launcherRouteServiceList != null
                && !launcherRouteServiceList.isEmpty()) {
            for (final LauncherRouteService service : launcherRouteServiceList) {
                launcherRouteServiceMap.put(service.name(), service);
            }
        }

        final Map<String, TemplateParserService> templateParserServiceMap = new HashMap<>();
        if (templateParserServiceList != null
                && !templateParserServiceList.isEmpty()) {
            for (final TemplateParserService service : templateParserServiceList) {
                templateParserServiceMap.put(service.name(), service);
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

        this.repository = repository;
        this.configParserServiceMap = configParserServiceMap;
        this.launcherRouteServiceMap = launcherRouteServiceMap;
        this.templateParserServiceMap = templateParserServiceMap;
        this.mailMessageLauncherServiceMap = mailMessageLauncherServiceMap;
        this.shortMessageLauncherServiceMap = shortMessageLauncherServiceMap;
        this.mobileMessageLauncherServiceMap = mobileMessageLauncherServiceMap;
    }

    /**
     * 读取发射器数据对象
     *
     * @param id 发射器 ID
     * @return 发射器模型
     */
    protected LauncherModel getLauncherData(Integer id) {
        final LauncherModel model = ExpiredCache.get(LAUNCHER_TYPE, String.valueOf(id));
        if (model == null) {
            return setLauncherData(id);
        } else {
            return model;
        }
    }

    /**
     * 写入发射器数据对象
     *
     * @param id 发射器 ID
     * @return 发射器模型
     */
    protected synchronized LauncherModel setLauncherData(Integer id) {
        final LauncherModel model = ExpiredCache.get(LAUNCHER_TYPE, String.valueOf(id));
        if (model == null) {
            final LauncherModel nm = repository.getLauncherData(id);
            if (nm == null) {
                return null;
            } else {
                ExpiredCache.set(LAUNCHER_TYPE, String.valueOf(id), nm);
                return nm;
            }
        } else {
            return model;
        }
    }

    /**
     * 读取配置数据对象
     *
     * @param id 配置 ID
     * @return 配置数据对象
     */
    protected ConfigModel getConfigData(Integer id) {
        final ConfigModel model = ExpiredCache.get(CONFIG_TYPE, String.valueOf(id));
        if (model == null) {
            return setConfigData(id);
        } else {
            return model;
        }
    }

    /**
     * 写入配置数据对象
     *
     * @param id 配置 ID
     * @return 配置数据对象
     */
    protected ConfigModel setConfigData(Integer id) {
        final ConfigModel model = ExpiredCache.get(CONFIG_TYPE, String.valueOf(id));
        if (model == null) {
            final ConfigModel cm = repository.getConfigData(id);
            if (cm == null) {
                return null;
            } else {
                ExpiredCache.set(CONFIG_TYPE, String.valueOf(id), cm);
                return cm;
            }
        } else {
            return model;
        }
    }

    /**
     * 读取模板数据对象
     *
     * @param key      模板键
     * @param language 模板语言
     * @return 模板数据对象
     */
    protected TemplateModel getTemplateData(String key, String language) {
        language = language == null ? DEFAULT_LANGUAGE : language;
        final TemplateModel model = ExpiredCache.get(TEMPLATE_TYPE, key + "@" + language);
        if (model == null) {
            return setTemplateData(key, language);
        } else {
            return model;
        }
    }

    /**
     * 写入模板数据对象
     *
     * @param key      模板键
     * @param language 模板语言
     * @return 模板数据对象
     */
    protected TemplateModel setTemplateData(String key, String language) {
        language = language == null ? DEFAULT_LANGUAGE : language;
        final TemplateModel model = ExpiredCache.get(TEMPLATE_TYPE, key + "@" + language);
        if (model == null) {
            final TemplateModel tm = repository.getTemplateData(key, language);
            if (tm == null) {
                return null;
            } else {
                ExpiredCache.set(TEMPLATE_TYPE, key + "@" + language, tm);
                return tm;
            }
        } else {
            return model;
        }
    }

    /**
     * 获取发射器路由服务
     *
     * @param launcher 发射器数据对象
     * @return 发射器路由服务
     */
    protected LauncherRouteService getLauncherRouteService(LauncherModel launcher) {
        final String route = launcher.route();
        if (route.startsWith("classname:")) {
            final byte[] classBytes = launcher.routeSource();
            final String className = route.substring(8);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, LauncherRouteService.class);
        } else {
            return launcherRouteServiceMap.get(route);
        }
    }

    /**
     * 获取配置解析器服务
     *
     * @param config 配置数据对象
     * @return 配置解析器服务
     */
    protected ConfigParserService getConfigParserService(ConfigModel config) {
        final String parser = config.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = config.parserSource();
            final String className = parser.substring(8);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, ConfigParserService.class);
        } else {
            return configParserServiceMap.get(parser);
        }
    }

    /**
     * 获取模板解析器服务
     *
     * @param template 模板数据对象
     * @return 模板解析器服务
     */
    protected TemplateParserService getTemplateParserService(TemplateModel template) {
        final String parser = template.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = template.parserSource();
            final String className = parser.substring(8);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, TemplateParserService.class);
        } else {
            return templateParserServiceMap.get(parser);
        }
    }

    /**
     * 获取发射器服务
     */
    protected LauncherService<?> getLauncherService(LauncherModel launcher, Map<String, LauncherService<?>> data) {
        final String parser = launcher.parser();
        if (parser.startsWith("classname:")) {
            final byte[] classBytes = launcher.parserSource();
            final String className = parser.substring(8);
            return ExternalSourceClassLoader.getInstance()
                    .newPackageClassInstance(className, classBytes, LauncherService.class);
        } else {
            return data.get(parser);
        }
    }

    /**
     * 获取邮件消息发射器服务
     *
     * @param launcher 发射器模型
     */
    protected MailMessageLauncherService getMailMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(mailMessageLauncherServiceMap));
        if (launcherService instanceof final MailMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(this.getClass(),
                "fun getMailMessageLauncherService(LauncherModel launcher).",
                "Unable to obtain corresponding mail message launcher service <" + MailMessageLauncherService.class + ">."
        );
    }

    /**
     * 获取短消息发射器服务
     *
     * @param launcher 发射器模型
     */
    protected ShortMessageLauncherService getShortMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(shortMessageLauncherServiceMap));
        if (launcherService instanceof final ShortMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(this.getClass(),
                "fun getShortMessageLauncherService(LauncherModel launcher).",
                "Unable to obtain corresponding short message launcher service <" + ShortMessageLauncherService.class + ">."
        );
    }

    /**
     * 获取移动消息发射器服务
     *
     * @param launcher 发射器模型
     */
    protected MobileMessageLauncherService getMobileMessageLauncherService(LauncherModel launcher) {
        final LauncherService<?> launcherService =
                getLauncherService(launcher, new HashMap<>(mobileMessageLauncherServiceMap));
        if (launcherService instanceof final MobileMessageLauncherService service) {
            return service;
        }
        throw new LauncherServiceNotExistException(this.getClass(),
                "fun getMobileMessageLauncherService(LauncherModel launcher).",
                "Unable to obtain corresponding mobile message launcher service <" + MobileMessageLauncherService.class + ">."
        );
    }

    @Override
    public Map<String, List<String>> push(Integer id, String language, List<String> recipients, Map<String, String> data, List<File> attachments) {
        final LauncherModel launcherModel = getLauncherData(id);

        if (launcherModel == null) {
            throw new LauncherNotExistException(this.getClass(), "fun push(...).", "Launcher is not exist.");
        }

        if (!launcherModel.enable()) {
            throw new LauncherNotEnableException(this.getClass(), "fun push(...).", "Launcher is not enabled.");
        }

        final List<ConfigModel> configs = new ArrayList<>();
        final List<LauncherModel.ConfigMapperModel> launcherConfigs = launcherModel.configs();

        if (launcherConfigs == null || launcherConfigs.isEmpty()) {
            throw new LauncherMapperConfigNoExistException(this.getClass(),
                    "fun push(...).", "Launcher mapper config result is not exist.");
        } else {
            for (final LauncherModel.ConfigMapperModel config : launcherConfigs) {
                final ConfigModel cm = getConfigData(config.id());
                if (cm != null) {
                    configs.add(cm);
                }
            }
        }

        if (configs.isEmpty()) {
            throw new LauncherMapperConfigNoExistException(this.getClass(),
                    "fun push(...).", "Launcher mapper config result is not exist.");
        }

        final ConfigModel routeConfigModel = getLauncherRouteService(launcherModel).execute(launcherModel, configs);
        if (routeConfigModel == null) {
            throw new LauncherRouteConfigException(this.getClass(), "fun push(...).",
                    "Launcher(" + launcherModel.id() + ") model mapper route config result is null.");
        }

        final ConfigModel configModel = getConfigParserService(routeConfigModel).execute(routeConfigModel);
        if (configModel == null) {
            throw new LauncherRouteConfigConvertException(this.getClass(), "fun push(...).",
                    "Launcher(" + launcherModel.id() + ") model mapper route config, analysis config convert exception.");
        }

        final TemplateModel templateModel = getTemplateData(launcherModel.template(), language);
        if (templateModel == null) {
            throw new LauncherTemplateNotExistException(this.getClass(), "fun push(...).",
                    "Launcher(" + launcherModel.id() + ") model mapper template("
                            + launcherModel.template() + "/" + language + ") result is null.");
        }

        final TemplateMessageModel templateMessageModel = getTemplateParserService(templateModel).execute(templateModel, data, attachments);
        if (templateMessageModel == null) {
            throw new LauncherTemplateConvertException(this.getClass(), "fun push(...).",
                    "Launcher(" + launcherModel.id() + ") model mapper template("
                            + launcherModel.template() + "/" + language + "), analysis template convert exception.");
        }

        templateMessageModel.putLogData("config", String.valueOf(configModel.id()));
        templateMessageModel.putLogData("template", String.valueOf(templateModel.id()));
        templateMessageModel.putLogData("launcher", String.valueOf(launcherModel.id()));

        if (attachments != null) {
            templateMessageModel.putLogData("attachment", JsonUtil.toJson(attachments.stream().map(File::getName).toList()));
        }

        switch (launcherModel.type()) {
            case SMS:
                if (configModel instanceof final ShortMessageConfigModel cm) {
                    return getShortMessageLauncherService(launcherModel).execute(recipients, templateMessageModel, cm);
                } else {
                    throw new LauncherTypeMismatchException(this.getClass(), "fun push(...).",
                            "Launcher(" + launcherModel.id() + ") model SMS >>> "
                                    + ShortMessageConfigModel.class + "(" + configModel.getClass() + ") type mismatch exception.");
                }
            case MAIL:
                if (configModel instanceof final MailMessageConfigModel cm) {
                    return getMailMessageLauncherService(launcherModel).execute(recipients, templateMessageModel, cm);
                } else {
                    throw new LauncherTypeMismatchException(this.getClass(), "fun push(...).",
                            "Launcher(" + launcherModel.id() + ") model MAIL >>> "
                                    + MailMessageConfigModel.class + "(" + configModel.getClass() + ") type mismatch exception.");
                }
            case MOBILE:
                if (configModel instanceof final MobileMessageConfigModel cm) {
                    return getMobileMessageLauncherService(launcherModel).execute(recipients, templateMessageModel, cm);
                } else {
                    throw new LauncherTypeMismatchException(this.getClass(), "fun push(...).",
                            "Launcher(" + launcherModel.id() + ") model MOBILE >>> "
                                    + MobileMessageConfigModel.class + "(" + configModel.getClass() + ") type mismatch exception.");
                }
            default:
                throw new LauncherTypeMismatchException(this.getClass(), "fun push(...).",
                        "Launcher(" + launcherModel.id() + ") model type(SMS/MAIL/MOBILE) mismatch exception.");
        }
    }

}
