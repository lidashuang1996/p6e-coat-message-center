package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.model.TemplateModel;
import club.p6e.coat.message.center.service.TemplateParserService;
import club.p6e.coat.message.center.service.TemplateVariableParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.function.Function;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = TemplateParserService.class,
        ignored = TemplateParserServiceImpl.class
)
public class TemplateParserServiceImpl implements TemplateParserService {

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "DEFAULT";

    /**
     * 是否用变量名替换空的值
     */
    public static boolean IS_VARIABLES_REPLACE_EMPTY_VALUE = false;

    /**
     * 模板变量解析器列表
     */
    private final List<TemplateVariableParserService> templateVariableParserList;

    /**
     * 构造方法初始化
     *
     * @param templateVariableParserList 模板变量解析器列表
     */
    public TemplateParserServiceImpl(List<TemplateVariableParserService> templateVariableParserList) {
        // 对模板变量解析器列表进行排序
        templateVariableParserList.sort(Comparator.comparingInt(Ordered::getOrder));
        this.templateVariableParserList = templateVariableParserList;
    }

    @Override
    public TemplateMessageModel execute(TemplateModel template, Map<String, String> data) {
        if (template == null || template.parser() == null || template.content() == null) {
            return null;
        }
        if (DEFAULT_PARSER.equalsIgnoreCase(template.parser())) {
            return new SimpleCommunicationTemplateModel(template) {{
                final Map<String, String> param = Objects.requireNonNullElseGet(data, HashMap::new);
                setCommunicationContent(
                        convert(template.content(), name -> {
                            String value = param.get(name);
                            if (value == null) {
                                for (final TemplateVariableParserService templateVariableParser : templateVariableParserList) {
                                    value = templateVariableParser.execute(name);
                                    if (value != null) {
                                        param.put(name, value);
                                        return value;
                                    }
                                }
                            }
                            return value;
                        })
                );
                setCommunicationParam(param);
            }};
        } else {
            throw new RuntimeException();
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
     * @param vf      变量替换的方法
     * @return 模板解析后的内容
     */
    private static String convert(String content, Function<String, String> vf) {
        return convert(content, vf, IS_VARIABLES_REPLACE_EMPTY_VALUE);
    }

    /**
     * 执行解析模板的内容
     *
     * @param content                      模板的内容
     * @param vf                           变量替换的方法
     * @param isVariablesReplaceEmptyValue 是否用变量名称替换空的数据内容
     * @return 模板解析后的内容
     */
    private static String convert(String content, Function<String, String> vf, boolean isVariablesReplaceEmptyValue) {
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
                        final String ks = key.toString();
                        key = null;
                        if (vf != null) {
                            final String value = vf.apply(getKey(ks));
                            if (value != null) {
                                result.append(value);
                                continue;
                            }
                        }
                        final String defaultValue = getDefaultValue(ks);
                        if (defaultValue != null) {
                            result.append(defaultValue);
                            continue;
                        }
                        if (isVariablesReplaceEmptyValue) {
                            result.append(ks);
                        } else {
                            result.append(START_CHAR)
                                    .append(CONTENT_START_CHAR)
                                    .append(ks).append(CONTENT_END_CHAR);
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

    /**
     * 读取键的内容
     *
     * @param content 模板的内容
     * @return 键的值
     */
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

    /**
     * 读取默认值的内容
     *
     * @param content 模板的内容
     * @return 默认值
     */
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

    /**
     * 简单的通讯模板模型
     */
    private static class SimpleCommunicationTemplateModel implements TemplateMessageModel {

        /**
         * 模板模型对象
         */
        private final TemplateModel model;

        /**
         * 通讯的模板结果
         */
        private String communicationContent;

        /**
         * 通讯的模板附件
         */
        private List<File> communicationAttachment;

        /**
         * 通讯的模板请求参数
         */
        private Map<String, String> communicationParam;

        /**
         * 构造方法初始化
         *
         * @param model 模板模型对象
         */
        private SimpleCommunicationTemplateModel(TemplateModel model) {
            this.model = model;
        }

        @Override
        public Integer id() {
            return model == null ? null : model.id();
        }

        @Override
        public String key() {
            return null;
        }

        @Override
        public String type() {
            return model == null ? null : model.type();
        }

        @Override
        public String mark() {
            return model == null ? null : model.mark();
        }

        @Override
        public String name() {
            return model == null ? null : model.name();
        }

        @Override
        public String title() {
            return model == null ? null : model.title();
        }

        @Override
        public String content() {
            return model == null ? null : model.content();
        }

        @Override
        public String description() {
            return model == null ? null : model.description();
        }

        @Override
        public String parser() {
            return model == null ? null : model.parser();
        }

        @Override
        public byte[] parserSource() {
            return model == null ? null : model.parserSource();
        }


        @Override
        public Map<String, String> getCommunicationParam() {
            return communicationParam;
        }

        @Override
        public void setCommunicationParam(Map<String, String> communicationParam) {
            this.communicationParam = communicationParam;
        }

        @Override
        public String getCommunicationTitle() {
            return null;
        }

        @Override
        public void setCommunicationTitle(String title) {

        }

        @Override
        public String getCommunicationContent() {
            return communicationContent;
        }

        @Override
        public void setCommunicationContent(String communicationContent) {
            this.communicationContent = communicationContent;
        }

        @Override
        public Map<String, String> getMessageParam() {
            return null;
        }

        @Override
        public void setMessageParam(Map<String, String> param) {

        }

        @Override
        public String getMessageTitle() {
            return null;
        }

        @Override
        public void setMessageTitle(String title) {

        }

        @Override
        public String getMessageContent() {
            return null;
        }

        @Override
        public void setMessageContent(String content) {

        }

        @Override
        public List<File> getAttachment() {
            return communicationAttachment;
        }

        @Override
        public void cleanAttachment() {
            if (communicationAttachment != null) {
                communicationAttachment.clear();
                communicationAttachment = null;
            }
        }

        @Override
        public void addAttachment(File file) {
            if (communicationAttachment == null) {
                communicationAttachment = new ArrayList<>();
            }
            communicationAttachment.add(file);
        }

        @Override
        public void removeAttachment(File file) {
            if (communicationAttachment != null) {
                communicationAttachment.remove(file);
            }
        }

        @Override
        public void removeAttachmentAt(int index) {
            if (communicationAttachment != null) {
                communicationAttachment.remove(index);
            }
        }

        @Override
        public void setAttachment(List<File> files) {
            communicationAttachment = files;
        }

    }
}
