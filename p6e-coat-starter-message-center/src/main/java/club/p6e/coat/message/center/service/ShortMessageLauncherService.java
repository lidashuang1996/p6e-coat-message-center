package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ShortMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageLauncherService extends LauncherService<ShortMessageConfigModel> {

    List<String> execute(List<String> recipients, TemplateMessageModel template, ShortMessageConfigModel config);


}
