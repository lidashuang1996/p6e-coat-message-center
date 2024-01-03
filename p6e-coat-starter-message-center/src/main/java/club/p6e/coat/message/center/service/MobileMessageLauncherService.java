package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherService extends LauncherService<MobileMessageConfigModel> {

    Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, MobileMessageConfigModel config);


}
