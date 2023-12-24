package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageLauncherService extends LauncherService<MailMessageConfigModel> {

    List<String> execute(List<String> recipients, TemplateMessageModel template, MailMessageConfigModel config);


}
