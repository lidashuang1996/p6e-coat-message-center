package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ShortMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageLauncherService extends LauncherService<ShortMessageConfigModel> {

    Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, ShortMessageConfigModel config);


}
