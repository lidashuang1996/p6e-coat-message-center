package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherService<T extends ConfigModel> {

    List<String> execute(List<String> recipients, TemplateMessageModel template, T config);

}
