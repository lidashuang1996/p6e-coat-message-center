package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * 移动消息发射服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherService extends LauncherService<MobileMessageConfigModel> {

    /**
     * 执行移动消息发射
     *
     * @param config     配置对象
     * @param template   模板对象
     * @param recipients 收件人列表
     * @return 结果对象
     */
    Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, MobileMessageConfigModel config);

}
