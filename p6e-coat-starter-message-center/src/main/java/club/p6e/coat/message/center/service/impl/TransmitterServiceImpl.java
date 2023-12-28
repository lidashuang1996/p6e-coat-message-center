package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.*;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.service.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class TransmitterServiceImpl implements TransmitterService {

    private DataSourceRepository repository;
    private ConfigParserService configParserService;
    private TemplateParserService templateParserService;
    private MailMessageLauncherService mailMessageLauncherService;
    private ShortMessageLauncherService shortMessageLauncherService;
    private MobileMessageLauncherService mobileMessageLauncherService;
    private final LauncherRouteService launcherRouteService;

    private final Map<String, ConfigModel> configs = new HashMap<>();
    private final Map<String, LauncherModel> launchers = new HashMap<>();
    private final Map<String, TemplateModel> templates = new HashMap<>();

    public TransmitterServiceImpl(
            DataSourceRepository repository,
            ConfigParserService configParserService,
            TemplateParserService templateParserService,
            LauncherRouteService launcherRouteService
    ) {
        this.repository = repository;
        this.configParserService = configParserService;
        this.templateParserService = templateParserService;
        this.launcherRouteService = launcherRouteService;
    }

    private LauncherModel getLauncherData(Integer id) {
        LauncherModel model = launchers.get(String.valueOf(id));
        if (model == null) {
            final LauncherModel nm = repository.getLauncherData(id);
            if (nm == null) {
                return null;
            } else {
                repository.getLauncherMapperSourceList(id);
                launchers.put(String.valueOf(id), model);
                model = nm;
            }
        }
        return model;
    }

    private ConfigModel getConfigData(Integer id) {
        ConfigModel model = configs.get(String.valueOf(id));
        if (model == null) {
            final ConfigModel nm = repository.getConfigData(id);
            if (nm == null) {
                return null;
            } else {
                repository.getLauncherMapperSourceList(id);
                model = nm;
            }
        }
        return model;
    }

    private TemplateModel getTemplateData(Integer id) {
        TemplateModel model = templates.get(String.valueOf(id));
        if (model == null) {
            final TemplateModel tm = repository.getTemplateData(id);
            if (tm == null) {
                throw new NullPointerException("[" + id + "] launcher model is null.");
            } else {
                repository.getLauncherMapperSourceList(id);
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
        final List<ConfigModel> list = new ArrayList<>();
        final List<LauncherModel.ConfigMapperModel> configs = launcherModel.configs();
        if (configs == null || configs.isEmpty()) {
            throw new NullPointerException("[" + id + "] launcher model mapper config result is null.");
        } else {
            for (final LauncherModel.ConfigMapperModel config : configs) {
                final ConfigModel cm = getConfigData(config.id());
                if (cm != null) {
                    list.add(cm);
                }
            }
        }
        final ConfigModel routeConfigModel = launcherRouteService.execute(launcherModel, list);
        final TemplateMessageModel templateModel = templateParserService.execute(getTemplateData(), data);
        if (routeConfigModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper route config result is null.");
        }
        if (templateModel == null) {
            throw new NullPointerException("[" + id + "] launcher model mapper template result is null.");
        }
        switch (launcherModel.type()) {
            case SMS:
                if (routeConfigModel instanceof ShortMessageConfigModel) {
                    return shortMessageLauncherService.execute(
                            recipients, templateModel, (ShortMessageConfigModel) routeConfigModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model SMS type is error.");
                }
                break;
            case MAIL:
                if (routeConfigModel instanceof MailMessageConfigModel) {
                    return mailMessageLauncherService.execute(
                            recipients, templateModel, (MailMessageConfigModel) routeConfigModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model MAIL type is error.");
                }
                break;
            case MOBILE:
                if (routeConfigModel instanceof MobileMessageConfigModel) {
                    return mobileMessageLauncherService.execute(
                            recipients, templateModel, (MobileMessageConfigModel) routeConfigModel);
                } else {
                    throw new RuntimeException("[" + id + "] launcher model MOBILE type is error.");
                }
                break;
            default:
                throw new RuntimeException("[" + id + "] launcher model type is error.");
        }
    }

}
