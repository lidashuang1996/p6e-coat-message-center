package club.p6e.coat.message.center.launcher.impl;

import club.p6e.coat.message.center.config.MobileMessageConfigModel;
import club.p6e.coat.message.center.template.TemplateModel;

import java.util.List;
import java.util.function.Function;

/**
 * 移动消息发射平台
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherPlatform {

    /**
     * 获取平台名称
     *
     * @return 平台名称
     */
    public String name();

    /**
     * 执行发射消息
     *
     * @param config     配置对象
     * @param template   模板对象
     * @param recipients 收件人
     * @return 需要执行的函数列表对象
     */
    public List<Function<Void, String>> execute(MobileMessageConfigModel config, TemplateModel template, List<String> recipients);

}
