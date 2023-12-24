package club.p6e.coat.message.center.launcher.impl;

import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.template.CommunicationTemplateModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherService<T extends ConfigModel> {

    List<String> execute(List<String> recipients, CommunicationTemplateModel template, T config);

}
