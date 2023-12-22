package club.p6e.coat.message.center.template;

import java.util.Map;

/**
 * 模板解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateParser {

    /**
     * 执行模板模型对象转换为通讯模板模型对象
     *
     * @param template 模板模型对象
     * @param data     数据变量对象
     * @return 通讯模板模型对象
     */
    CommunicationTemplateModel execute(TemplateModel template, Map<String, String> data);

}
