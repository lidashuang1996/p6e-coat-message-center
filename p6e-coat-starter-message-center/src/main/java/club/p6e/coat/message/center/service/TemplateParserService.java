package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.model.TemplateModel;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 模板解析器服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateParserService {

    /**
     * 获取配置解析器的名称
     *
     * @return 配置解析器的名称
     */
    String name();

    /**
     * TemplateModel >>> TemplateMessageModel
     *
     * @param data        数据变量
     * @param template    模板模型
     * @param attachments 文件附件
     * @return 模板消息模型对象
     */
    TemplateMessageModel execute(TemplateModel template, Map<String, String> data, List<File> attachments);

}
