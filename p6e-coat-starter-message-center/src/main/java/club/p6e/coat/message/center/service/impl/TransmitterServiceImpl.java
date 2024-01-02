package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.*;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.service.*;
import club.p6e.coat.message.center.utils.ExpiredCacheUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发射机服务的实现类
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class TransmitterServiceImpl implements TransmitterService {

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
    private final ConfigParserService configParserService;

    /**
     * 模板解析器服务
     */
    private final TemplateParserService templateParserService;

    /**
     * 发射器路由服务
     */
    private final LauncherRouteService launcherRouteService;

    /**
     * MAIL 类型消息发射器
     */
    private final MailMessageLauncherService mailMessageLauncherService;

    /**
     * SMS 类型消息发射器
     */
    private final ShortMessageLauncherService shortMessageLauncherService;

    /**
     * MMS 类型消息发射器
     */
    private final MobileMessageLauncherService mobileMessageLauncherService;

    private static final String CONFIG_TYPE = "CONFIG";
    private static final String LAUNCHER_TYPE = "LAUNCHER";

    private static final String TEMPLATE_TYPE = "TEMPLATE";

    public TransmitterServiceImpl(
            DataSourceRepository repository,
            ConfigParserService configParserService,
            TemplateParserService templateParserService,
            LauncherRouteService launcherRouteService,
            MailMessageLauncherService mailMessageLauncherService,
            ShortMessageLauncherService shortMessageLauncherService,
            MobileMessageLauncherService mobileMessageLauncherService
    ) {
        this.repository = repository;
        this.configParserService = configParserService;
        this.templateParserService = templateParserService;
        this.launcherRouteService = launcherRouteService;
        this.mailMessageLauncherService = mailMessageLauncherService;
        this.shortMessageLauncherService = shortMessageLauncherService;
        this.mobileMessageLauncherService = mobileMessageLauncherService;
    }

    /**
     * 读取发射器数据对象
     *
     * @param id 发射器 ID
     * @return 发射器模型
     */
    private LauncherModel getLauncherData(Integer id) {
        LauncherModel model = ExpiredCacheUtil.get(LAUNCHER_TYPE, String.valueOf(id));
        if (model == null) {
            final LauncherModel nm = repository.getLauncherData(id);
            if (nm == null) {
                return null;
            } else {
                ExpiredCacheUtil.set(LAUNCHER_TYPE, String.valueOf(id), nm);
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
        ConfigModel model = ExpiredCacheUtil.get(CONFIG_TYPE, String.valueOf(id));
        if (model == null) {
            final ConfigModel cm = repository.getConfigData(id);
            if (cm == null) {
                return null;
            } else {
                ExpiredCacheUtil.set(CONFIG_TYPE, String.valueOf(id), cm);
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
        TemplateModel model = ExpiredCacheUtil.get(TEMPLATE_TYPE, key + "_" + language);
        if (model == null) {
            final TemplateModel tm = repository.getTemplateData(key, language);
            if (tm == null) {
                return null;
            } else {
                ExpiredCacheUtil.set(TEMPLATE_TYPE, key + "_" + language, tm);
                model = tm;
            }
        }
        return model;
    }

    @Override
    public List<String> push(Integer id, String language, List<String> recipients, Map<String, String> data, List<File> attachments) {
        LauncherModel launcherModel = getLauncherData(id);
        if (launcherModel == null) {
            throw new NullPointerException("[" + id + "] launcher model is null.");
        }
        if (!launcherModel.enable()) {
            throw new RuntimeException("[" + id + "] launcher model is disable.");
        }
        final List<ConfigModel> configs = new ArrayList<>();
        final List<LauncherModel.ConfigMapperModel> launcherConfigs = launcherModel.configs();
        if (launcherConfigs == null || launcherConfigs.isEmpty()) {
            throw new NullPointerException("[" + id + "] launcher model mapper config result is null.");
        } else {
            for (final LauncherModel.ConfigMapperModel config : launcherConfigs) {
                final ConfigModel cm = getConfigData(config.id());
                if (cm != null) {
                    configs.add(cm);
                }
            }
        }
        final ConfigModel routeConfigModel = launcherRouteService.execute(launcherModel, configs);
        if (routeConfigModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper route config result is null.");
        }
        final ConfigModel configModel = configParserService.execute(routeConfigModel);
        if (configModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper route config result is null.");
        }
        final TemplateModel templateModel = getTemplateData(launcherModel.template(), language);
        if (templateModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper template result is null.");
        }
        final TemplateMessageModel templateMessageModel = templateParserService.execute(templateModel, data, attachments);
        if (templateMessageModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper template result is null.");
        }
        switch (launcherModel.type()) {
            case SMS:
                if (configModel instanceof ShortMessageConfigModel) {
                    return shortMessageLauncherService.execute(
                            recipients, templateMessageModel, (ShortMessageConfigModel) configModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model SMS " +
                            "[ route-config/" + configModel.getClass() + " ] type is error.");
                }
            case MAIL:
                if (configModel instanceof MailMessageConfigModel) {
                    return mailMessageLauncherService.execute(
                            recipients, templateMessageModel, (MailMessageConfigModel) configModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model MAIL " +
                            "[ route-config/" + configModel.getClass() + " ] type is error.");
                }
            case MOBILE:
                if (configModel instanceof MobileMessageConfigModel) {
                    return mobileMessageLauncherService.execute(
                            recipients, templateMessageModel, (MobileMessageConfigModel) configModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model MOBILE " +
                            "[ route-config/" + configModel.getClass() + " ] type is error.");
                }
            default:
                throw new RuntimeException("[" + id + "] launcher model type is error.");
        }
    }

}
