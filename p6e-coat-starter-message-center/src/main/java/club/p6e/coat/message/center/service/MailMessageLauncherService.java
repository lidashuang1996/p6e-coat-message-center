package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * 邮件发射器服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageLauncherService extends LauncherService<MailMessageConfigModel> {

    /**
     * 执行邮件发射
     *
     * @param config     配置对象
     * @param template   模板对象
     * @param recipients 收件人列表
     * @return 结果对象
     */
    Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, MailMessageConfigModel config);

}
