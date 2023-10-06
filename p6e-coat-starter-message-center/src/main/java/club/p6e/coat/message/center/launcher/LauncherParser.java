package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigData;
import club.p6e.coat.message.center.template.TemplateData;

import java.util.List;

/**
 * 发射解析器
 *
 * @param <T> 配置类型
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherParser<T extends ConfigData> {

    /**
     * 执行发射消息
     *
     * @param configData   配置对象
     * @param templateData 模板对象
     * @param recipients   收件人
     * @return 发射器回执对象
     */
    public LauncherData execute(T configData, TemplateData templateData, List<String> recipients);

}
