package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.*;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.service.*;
import club.p6e.coat.message.center.ExpiredCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 配置类型
     */
    private static final String CONFIG_TYPE = "CONFIG";

    /**
     * 模板类型
     */
    private static final String TEMPLATE_TYPE = "TEMPLATE";

    /**
     * 发射器类型
     */
    private static final String LAUNCHER_TYPE = "LAUNCHER";

    /**
     * 默认的语言
     */
    public static final String DEFAULT_LANGUAGE = "zh-cmn-Hans-CN";

    /**
     * 数据源仓库对象
     */
    private final DataSourceRepository repository;

    /**
     * 配置解析器服务
     */
    private final Map<String, ConfigParserService> configParserServiceMap;

    /**
     * 模板解析器服务
     */
    private final Map<String, TemplateParserService> templateParserServiceMap;

    /**
     * 发射器路由服务
     */
    private final Map<String, LauncherRouteService> launcherRouteServiceMap;

    /**
     * MAIL 类型消息发射器
     */
    private final Map<String, MailMessageLauncherService> mailMessageLauncherServiceMap;

    /**
     * SMS 类型消息发射器
     */
    private final Map<String, ShortMessageLauncherService> shortMessageLauncherServiceMap;

    /**
     * MMS 类型消息发射器
     */
    private final Map<String, MobileMessageLauncherService> mobileMessageLauncherServiceMap;


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
        final Map<String, LauncherRouteService> launcherRouteServiceMap = new HashMap<>();
        if (launcherRouteServiceList != null
                && !launcherRouteServiceList.isEmpty()) {
            for (final LauncherRouteService service : launcherRouteServiceList) {
                launcherRouteServiceMap.put(service.name(), service);
            }
        }

        final Map<String, ConfigParserService> configParserServiceMap = new HashMap<>();
        if (configParserServiceList != null
                && !configParserServiceList.isEmpty()) {
            for (final ConfigParserService service : configParserServiceList) {
                configParserServiceMap.put(service.name(), service);
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
        this.launcherRouteServiceMap = launcherRouteServiceMap;
        this.configParserServiceMap = configParserServiceMap;
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
    private LauncherModel getLauncherData(Integer id) {
        LauncherModel model = ExpiredCache.get(LAUNCHER_TYPE, String.valueOf(id));
        if (model == null) {
            final LauncherModel nm = repository.getLauncherData(id);
            if (nm == null) {
                return null;
            } else {
                ExpiredCache.set(LAUNCHER_TYPE, String.valueOf(id), nm);
                model = nm;
            }
        }
        return model;
    }

    /**
     * 读取配置数据对象
     *
     * @param id 配置 ID
     * @return 配置数据对象
     */
    private ConfigModel getConfigData(Integer id) {
        ConfigModel model = ExpiredCache.get(CONFIG_TYPE, String.valueOf(id));
        if (model == null) {
            final ConfigModel cm = repository.getConfigData(id);
            if (cm == null) {
                return null;
            } else {
                ExpiredCache.set(CONFIG_TYPE, String.valueOf(id), cm);
                model = cm;
            }
        }
        return model;
    }

    /**
     * 读取模板数据对象
     *
     * @param key      模板键
     * @param language 模板语言
     * @return 模板数据对象
     */
    private TemplateModel getTemplateData(String key, String language) {
        language = language == null ? DEFAULT_LANGUAGE : language;
        TemplateModel model = ExpiredCache.get(TEMPLATE_TYPE, key + "_" + language);
        if (model == null) {
            final TemplateModel tm = repository.getTemplateData(key, language);
            if (tm == null) {
                return null;
            } else {
                ExpiredCache.set(TEMPLATE_TYPE, key + "_" + language, tm);
                model = tm;
            }
        }
        return model;
    }

    /**
     * 获取发射器服务
     */
    private LauncherService<?> getLauncherService(ConfigModel config, Map<String, LauncherService<?>> data) {
        final String parser = config.parser();
        if (parser.startsWith("package:")) {
            final String[] ps = parser.split(":");
            if (ps.length == 2) {
                final String className = ps[1];
                try {
                    final Class<?> clazz = Class.forName(className);
                    final Object obj = clazz.newInstance();
                    if (obj instanceof LauncherService) {
                        final LauncherService<?> service = (LauncherService<?>) obj;
                        data.put(parser, service);
                        return service;
                    } else {
                        throw new RuntimeException("[" + parser + "] class is not implements LauncherService.");
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException("[" + parser + "] class not found.");
                }
            } else {
                throw new RuntimeException("[" + parser + "] class name is null.");
            }

        } else {
            return data.get(parser);
        }
    }

    /**
     * 获取邮件消息发射器服务
     */
    private MailMessageLauncherService getMailMessageLauncherService(ConfigModel config) {
        final LauncherService<?> launcherService =
                getLauncherService(config, new HashMap<>(mailMessageLauncherServiceMap));
        if (launcherService instanceof final MailMessageLauncherService service) {
            return service;
        }
        throw new RuntimeException();
    }

    private ShortMessageLauncherService getShortMessageLauncherService(ConfigModel config) {
        final LauncherService<?> launcherService =
                getLauncherService(config, new HashMap<>(shortMessageLauncherServiceMap));
        if (launcherService instanceof final ShortMessageLauncherService service) {
            return service;
        }
        throw new RuntimeException();
    }

    private MobileMessageLauncherService getMobileMessageLauncherService(ConfigModel config) {
        final LauncherService<?> launcherService =
                getLauncherService(config, new HashMap<>(mobileMessageLauncherServiceMap));
        if (launcherService instanceof final MobileMessageLauncherService service) {
            return service;
        }
        throw new RuntimeException();
    }

    private LauncherRouteService getLauncherRouteService(LauncherModel launcher) {
        final String route = launcher.route();
        if (route.startsWith("package:")) {
            final String[] ps = route.split(":");
            if (ps.length == 2) {
                return null;
            } else {
                throw new RuntimeException("[" + route + "] class name is null.");
            }
        } else {
            return launcherRouteServiceMap.get(route);
        }
    }

    private ConfigParserService getConfigParserService(ConfigModel config) {
        final String parser = config.parser();
        if (parser.startsWith("package:")) {
            final String[] ps = parser.split(":");
            if (ps.length == 2) {
                return null;
            } else {
                throw new RuntimeException("[" + parser + "] class name is null.");
            }
        } else {
            return configParserServiceMap.get(parser);
        }
    }

    private TemplateParserService getTemplateParserService(TemplateModel template) {
        final String parser = template.parser();
        if (parser.startsWith("package:")) {
            final String[] ps = parser.split(":");
            if (ps.length == 2) {
                return null;
            } else {
                throw new RuntimeException("[" + parser + "] class name is null.");
            }
        } else {
            return templateParserServiceMap.get(parser);
        }
    }

    @Override
    public List<String> push(Integer id, String language, List<String> recipients, Map<String, String> data, List<File> attachments) {
        // 读取发射器数据对象
        LauncherModel launcherModel = getLauncherData(id);
        if (launcherModel == null) {
            // 如果发射器数据对象为空，则抛出空指针异常
            throw new NullPointerException("[" + id + "] launcher model is null.");
        }
        if (!launcherModel.enable()) {
            // 如果发射器数据对象的状态为禁用，则抛出运行时异常
            throw new RuntimeException("[" + id + "] launcher model is disable.");
        }
        // 读取配置数据对象
        final List<ConfigModel> configs = new ArrayList<>();
        final List<LauncherModel.ConfigMapperModel> launcherConfigs = launcherModel.configs();
        if (launcherConfigs == null || launcherConfigs.isEmpty()) {
            throw new NullPointerException("[" + id + "] launcher model mapper config result is null.");
        } else {
            for (final LauncherModel.ConfigMapperModel config : launcherConfigs) {
                // 查询映射的配置模型对象
                final ConfigModel cm = getConfigData(config.id());
                if (cm != null) {
                    configs.add(cm);
                }
            }
        }
        // 发射器路由服务执行
        final ConfigModel routeConfigModel = getLauncherRouteService(launcherModel).execute(launcherModel, configs);
        if (routeConfigModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper route config result is null.");
        }
        // 将配置模型对象转换为配置数据模型对象
        final ConfigModel configModel = getConfigParserService(routeConfigModel).execute(routeConfigModel);
        if (configModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper route config, analysis config convert exception.");
        }
        // 读取模板数据对象
        final TemplateModel templateModel = getTemplateData(launcherModel.template(), language);
        if (templateModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper template result is null.");
        }
        // 将模板数据对象转换为模板消息数据对象
        final TemplateMessageModel templateMessageModel = getTemplateParserService(templateModel).execute(templateModel, data, attachments);
        if (templateMessageModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper template, analysis template convert exception.");
        }
        // 类型判断分发
        switch (launcherModel.type()) {
            case SMS:
                if (configModel instanceof final ShortMessageConfigModel cm) {
                    return getShortMessageLauncherService(cm).execute(
                            recipients, templateMessageModel, (ShortMessageConfigModel) configModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model SMS " +
                            "[ route-config/" + configModel.getClass() + " ] type exception.");
                }
            case MAIL:
                if (configModel instanceof final MailMessageConfigModel cm) {
                    return getMailMessageLauncherService(cm).execute(
                            recipients, templateMessageModel, cm);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model MAIL " +
                            "[ route-config/" + configModel.getClass() + " ] type exception.");
                }
            case MOBILE:
                if (configModel instanceof final MobileMessageConfigModel cm) {
                    return getMobileMessageLauncherService(cm).execute(
                            recipients, templateMessageModel, cm);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model MOBILE " +
                            "[ route-config/" + configModel.getClass() + " ] type exception.");
                }
            default:
                throw new RuntimeException("[" + id + "] launcher model type exception.");
        }
    }

}
