package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.MobileMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;

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
     * 执行发射消息
     *
     * @param config     配置对象
     * @param template   模板对象
     * @param recipients 收件人
     * @return 需要执行的函数列表对象
     */
    public List<Function<Void, Void>> execute(MobileMessageConfigData config, TemplateData template, List<String> recipients);

}
