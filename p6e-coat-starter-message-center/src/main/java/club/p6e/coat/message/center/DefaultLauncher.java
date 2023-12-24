package club.p6e.coat.message.center;

import club.p6e.coat.message.center.config.*;
import club.p6e.coat.message.center.launcher.LauncherFactory;
import club.p6e.coat.message.center.launcher.LauncherModel;
import club.p6e.coat.message.center.launcher.LauncherRouteMatcher;
import club.p6e.coat.message.center.template.CommunicationTemplateModel;
import club.p6e.coat.message.center.template.TemplateFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class DefaultLauncher implements Transmitter {

    private final ConfigFactory configFactory;
    private final TemplateFactory templateFactory;
    private final LauncherFactory launcherFactory;
    private final LauncherRouteMatcher launcherRouteMatcher;

    public DefaultLauncher(
            ConfigFactory configFactory,
            TemplateFactory templateFactory,
            LauncherFactory launcherFactory,
            LauncherRouteMatcher launcherRouteMatcher
    ) {
        this.configFactory = configFactory;
        this.templateFactory = templateFactory;
        this.launcherFactory = launcherFactory;
        this.launcherRouteMatcher = launcherRouteMatcher;
    }


    @Override
    public List<String> push(Integer id, String language, List<String> recipients, Map<String, String> data, List<File> attachments) {
        final LauncherModel launcherModel = launcherFactory.getModel(id);
        if (launcherModel == null) {
            throw new NullPointerException("[" + id + "] launcher model is null.");
        } else {
            if (launcherModel.enable() != null && launcherModel.enable() == 1) {
                final List<ConfigModel> list = new ArrayList<>();
                final List<LauncherModel.ConfigMapperModel> configs = launcherModel.configs();
                if (configs == null || configs.isEmpty()) {
                    throw new NullPointerException("[" + id + "] launcher model mapper config result is null.");
                } else {
                    for (final LauncherModel.ConfigMapperModel config : configs) {
                        final ConfigModel cm = configFactory.getConfigModel(config.id());
                        if (cm != null) {
                            list.add(cm);
                        }
                    }
                }
                final ConfigModel routeConfigModel = launcherRouteMatcher.execute(launcherModel, list);
                if (routeConfigModel == null) {
                    throw new NullPointerException("[" + id + "] launcher model mapper route config result is null.");
                } else {
                    switch (launcherModel.type()) {
                        case SMS:
                            if (!(routeConfigModel instanceof ShortMessageConfigModel)) {
                                throw new RuntimeException("[" + id + "] launcher model SMS type is error.");
                            }
                            break;
                        case MAIL:
                            if (!(routeConfigModel instanceof MailMessageConfigModel)) {
                                throw new RuntimeException("[" + id + "] launcher model MAIL type is error.");
                            }
                            break;
                        case MOBILE:
                            if (!(routeConfigModel instanceof MobileMessageConfigModel)) {
                                throw new RuntimeException("[" + id + "] launcher model MOBILE type is error.");
                            }
                            break;
                        default:
                            throw new RuntimeException("[" + id + "] launcher model type is error.");
                    }
                    final CommunicationTemplateModel communicationTemplateModel
                            = templateFactory.getModel(launcherModel.template(), language, data, attachments);
                    if (communicationTemplateModel == null) {
                        throw new NullPointerException("[" + id + "] launcher model mapper template result is null.");
                    } else {
                        return launcherFactory.push(recipients, communicationTemplateModel, routeConfigModel);
                    }
                }
            } else {
                throw new RuntimeException("[" + id + "] launcher model is disable.");
            }
        }
    }

}
