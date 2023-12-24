package club.p6e.coat.message.center.launcher.impl;

import club.p6e.coat.message.center.config.MailMessageConfigModel;
import club.p6e.coat.message.center.template.CommunicationTemplateModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageLauncherService extends LauncherService<MailMessageConfigModel> {

    List<String> execute(List<String> recipients, CommunicationTemplateModel template, MailMessageConfigModel config);


}
