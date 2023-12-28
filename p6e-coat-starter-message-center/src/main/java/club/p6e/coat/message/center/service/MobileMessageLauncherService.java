package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherService extends LauncherService<MobileMessageConfigModel> {

    List<String> execute(List<String> recipients, TemplateMessageModel template, MobileMessageConfigModel config);


}
