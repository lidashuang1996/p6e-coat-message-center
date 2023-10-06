package club.p6e.coat.message.center.template;

import club.p6e.coat.message.center.variable.BasicVariableParser;

/**
 * 模板解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateParser {

    /**
     * 执行模板源对象转换为模板数据对象
     *
     * @param templateSource 模板源对象
     * @param variableParser 变量解析器对象
     * @return 模板数据对象
     */
    public TemplateData execute(TemplateSource templateSource, BasicVariableParser variableParser);

}
