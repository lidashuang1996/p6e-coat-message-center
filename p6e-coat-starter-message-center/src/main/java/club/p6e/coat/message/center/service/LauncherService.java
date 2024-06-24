package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * 发射器服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherService<T extends ConfigModel> {

    /**
     * 获取发射器的名称
     *
     * @return 发射器的名称
     */
    String name();

    /**
     * 执行发射器
     *
     * @param config     配置对象
     * @param template   模板对象
     * @param recipients 收件人列表
     * @return 执行结果
     */
    Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, T config);

}
