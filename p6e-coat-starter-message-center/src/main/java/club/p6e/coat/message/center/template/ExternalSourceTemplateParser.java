package club.p6e.coat.message.center.template;

import club.p6e.coat.message.center.ExternalSourceClassLoader;
import club.p6e.coat.message.center.variable.BasicVariableParser;

/**
 * 外部数据源模板解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public class ExternalSourceTemplateParser implements TemplateParser {

    @Override
    public TemplateData execute(TemplateSource templateSource, BasicVariableParser variableParser) {
        if (templateSource != null
                && templateSource.parser() != null
                && templateSource.parserSource() != null) {
            final ExternalSourceClassLoader loader = ExternalSourceClassLoader.getInstance();
            final TemplateParser externalSourceTemplateParser = loader.newClassInstance(
                    templateSource.parser(),
                    templateSource.parserSource(),
                    TemplateParser.class
            );
            externalSourceTemplateParser.execute(templateSource, variableParser);
        }
        throw new NullPointerException("template source parameters [parser/source] is null");
    }

}
