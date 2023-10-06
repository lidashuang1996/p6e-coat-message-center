package club.p6e.coat.message.center.template;

import club.p6e.coat.message.center.variable.BasicVariableParser;
import club.p6e.coat.message.center.variable.VariableParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模板解析器（默认）
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = TemplateParser.class,
        ignored = DefaultTemplateParser.class
)
public class DefaultTemplateParser implements TemplateParser {

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "DEFAULT";

    /**
     * 是否用变量名替换空的值
     */
    public static boolean IS_VARIABLES_REPLACE_EMPTY_VALUE = false;

    /**
     * 外部源模板解析器
     */
    private final ExternalSourceTemplateParser externalSourceTemplateParser = new ExternalSourceTemplateParser();

    @Override
    public TemplateData execute(TemplateSource templateSource, BasicVariableParser variableParser) {
        if (templateSource == null) {
            throw new NullPointerException("template source is null");
        }
        if (templateSource.parser() == null) {
            throw new NullPointerException("template source parameters [parser] is null");
        }
        final String parser = templateSource.parser();
        if (DEFAULT_PARSER.equalsIgnoreCase(parser)) {
            return new TemplateData() {
                @Override
                public Integer id() {
                    return templateSource.id();
                }

                @Override
                public String type() {
                    return templateSource.type();
                }

                @Override
                public String mark() {
                    return templateSource.mark();
                }

                @Override
                public String name() {
                    return templateSource.name();
                }

                @Override
                public String title() {
                    return convert(templateSource.title(), variableParser);
                }

                @Override
                public String content() {
                    return convert(templateSource.content(), variableParser);
                }

                @Override
                public List<String> attachments() {
                    if (variableParser.getData() != null
                            && !variableParser.getData().isEmpty()) {
                        final List<String> result = new ArrayList<>();
                        for (final String key : variableParser.getData().keySet()) {
                            if (key.startsWith("attachment_")) {
                                result.add(key);
                            }
                        }
                        return result;
                    }
                    return null;
                }

                @Override
                public Map<String, String> variable() {
                    return variableParser.getData();
                }
            };
        } else {
            // 如果不是默认的名称的解析器，就执行外部源解析器执行解析
            return externalSourceTemplateParser.execute(templateSource, variableParser);
        }
    }

    /**
     * 标记开始的符号
     */
    private static final String START_CHAR = "@";

    /**
     * 内容结束的符号
     */
    private static final String CONTENT_END_CHAR = "}";

    /**
     * 内容开始的符号
     */
    private static final String CONTENT_START_CHAR = "{";

    /**
     * 内容默认值的符号
     */
    private static final String CONTENT_DEFAULT_VALUE_CHAR = ":";

    /**
     * 执行解析模板的内容
     *
     * @param content 模板的内容
     * @param parser  模板的数据解析器
     * @return 模板解析后的内容
     */
    private static String convert(String content, VariableParser parser) {
        return convert(content, parser, IS_VARIABLES_REPLACE_EMPTY_VALUE);
    }

    /**
     * 执行解析模板的内容
     *
     * @param content                      模板的内容
     * @param parser                       模板的数据解析器
     * @param isVariablesReplaceEmptyValue 是否用变量名称替换空的数据内容
     * @return 模板解析后的内容
     */
    private static String convert(String content, VariableParser parser, boolean isVariablesReplaceEmptyValue) {
        if (content == null || content.isEmpty()) {
            return "";
        } else {
            StringBuilder key = null;
            final int length = content.length();
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < length; i++) {
                final String c = String.valueOf(content.charAt(i));
                if (i + 1 < length && START_CHAR.equals(c)
                        && CONTENT_START_CHAR.equals(String.valueOf(content.charAt(i + 1)))) {
                    if (key != null) {
                        result.append(START_CHAR).append(CONTENT_START_CHAR).append(key);
                    }
                    i++;
                    key = new StringBuilder();
                } else if (key != null) {
                    if (CONTENT_END_CHAR.equals(c)) {
                        final String keySource = key.toString();
                        key = null;
                        if (parser != null) {
                            final String value = parser.execute(getKey(keySource));
                            if (value != null) {
                                result.append(value);
                                continue;
                            }
                        }
                        final String defaultValue = getDefaultValue(keySource);
                        if (defaultValue != null) {
                            result.append(defaultValue);
                            continue;
                        }
                        if (isVariablesReplaceEmptyValue) {
                            result.append(keySource);
                        } else {
                            result.append(START_CHAR)
                                    .append(CONTENT_START_CHAR)
                                    .append(keySource).append(CONTENT_END_CHAR);
                        }
                    } else {
                        key.append(c);
                    }
                } else {
                    result.append(c);
                }
            }
            if (key != null) {
                result.append(START_CHAR).append(CONTENT_START_CHAR).append(key);
            }
            return result.toString();
        }
    }

    private static String getKey(String content) {
        if (content != null && !content.isEmpty()) {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                final String ch = String.valueOf(content.charAt(i));
                if (CONTENT_DEFAULT_VALUE_CHAR.equals(ch)) {
                    break;
                } else {
                    result.append(content.charAt(i));
                }
            }
            return result.toString();
        }
        return "";
    }

    private static String getDefaultValue(String content) {
        if (content != null && !content.isEmpty()) {
            final StringBuilder result = new StringBuilder();
            for (int i = content.length() - 1; i >= 0; i--) {
                final String ch = String.valueOf(content.charAt(i));
                if (CONTENT_DEFAULT_VALUE_CHAR.equals(ch)) {
                    return !result.isEmpty() ? result.toString() : null;
                } else {
                    result.append(content.charAt(i));
                }
            }
        }
        return null;
    }
}
