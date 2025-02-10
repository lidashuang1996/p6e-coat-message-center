package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.template.TemplateModel;
import club.p6e.coat.message.center.template.TemplateVariableParserService;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * LauncherTemplateDefaultParserService
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherTemplateDefaultParserService implements LauncherTemplateParserService {

    /**
     * Parser Name
     */
    private static final String DEFAULT_PARSER = "LAUNCHER_TEMPLATE_DEFAULT_PARSER";

    /**
     * Replace Empty Values With Variable Names
     */
    public static boolean IS_VARIABLES_REPLACE_EMPTY_VALUE = false;

    /**
     * Mark Start Symbol
     */
    private static final String START_CHAR = "@";

    /**
     * Content End Symbol
     */
    private static final String CONTENT_END_CHAR = "}";

    /**
     * Content Start Symbol
     */
    private static final String CONTENT_START_CHAR = "{";

    /**
     * Content Default Symbol
     */
    private static final String CONTENT_DEFAULT_VALUE_CHAR = ":";

    /**
     * Template Variable Parser List
     */
    private final List<TemplateVariableParserService> templateVariableParserServices;

    /**
     * 执行解析模板的内容
     *
     * @param content Content Data
     * @param vf      Callback Replace Function
     * @return Parsing Template Content
     */
    private static String convert(String content, Function<String, String> vf) {
        return convert(content, vf, IS_VARIABLES_REPLACE_EMPTY_VALUE);
    }

    /**
     * Execute Parsing Template
     *
     * @param content                      Content Data
     * @param vf                           Callback Replace Function
     * @param isVariablesReplaceEmptyValue Variables Replace Empty Value
     * @return Parsing Template Content
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
     * Get Key Value
     *
     * @param content Content Data
     * @return Key Value
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
     * Get Default Value
     *
     * @param content Content Data
     * @return Default Value
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
     * Construct Initialization
     *
     * @param list Template Variable Parser Service List
     */
    public LauncherTemplateDefaultParserService(List<TemplateVariableParserService> list) {
        list.sort(Comparator.comparingInt(Ordered::getOrder));
        this.templateVariableParserServices = list;
    }

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public LauncherTemplateModel execute(LauncherStartingModel starting, TemplateModel template) {
        if (template == null || template.parser() == null || template.content() == null) {
            return null;
        }
        final SimpleTemplateMessageModel model = new SimpleTemplateMessageModel(template);
        final Function<String, String> vf = name -> {
            String value = starting.param().get(name);
            if (value == null) {
                for (final TemplateVariableParserService service : templateVariableParserServices) {
                    value = service.execute(name, starting.language());
                    if (value != null) {
                        return value;
                    }
                }
            }
            return value;
        };
        model.setAttachment(starting.attachment());
        model.setMessageParam(new HashMap<>(starting.param()));
        model.setMessageTitle(convert(template.title(), vf));
        model.setMessageContent(convert(template.content(), vf));
        return model;
    }

    /**
     * SimpleTemplateMessageModel
     */
    private static class SimpleTemplateMessageModel implements LauncherTemplateModel, Serializable {

        /**
         * Title
         */
        private String title;

        /**
         * Content
         */
        private String content;

        /**
         * Param
         */
        private Map<String, String> param;

        /**
         * Source Config Model
         */
        private final TemplateModel source;

        /**
         * Attachments
         */
        private final List<File> attachments = new CopyOnWriteArrayList<>();

        /**
         * Construct Initialization
         * Inject Source Template Model Object
         *
         * @param source Source Template Model
         */
        private SimpleTemplateMessageModel(TemplateModel source) {
            this.source = source;
        }

        @Override
        public Integer id() {
            return this.source == null ? null : this.source.id();
        }

        @Override
        public String key() {
            return this.source == null ? null : this.source.key();
        }

        @Override
        public String type() {
            return this.source == null ? null : this.source.type();
        }

        @Override
        public String name() {
            return this.source == null ? null : this.source.name();
        }

        @Override
        public String language() {
            return this.source == null ? null : this.source.language();
        }

        @Override
        public String title() {
            return this.source == null ? null : this.source.title();
        }

        @Override
        public String content() {
            return this.source == null ? null : this.source.content();
        }

        @Override
        public String description() {
            return this.source == null ? null : this.source.description();
        }

        @Override
        public String parser() {
            return this.source == null ? null : this.source.parser();
        }

        @Override
        public byte[] parserSource() {
            return this.source == null ? null : this.source.parserSource();
        }

        @Override
        public String getChat() {
            if (this.param != null) {
                return this.param.get("chat");
            }
            return null;
        }

        @Override
        public String getType() {
            if (this.param != null) {
                return this.param.get("type");
            }
            return null;
        }

        @Override
        public Map<String, String> getMessageParam() {
            return this.param;
        }

        @Override
        public void setMessageParam(Map<String, String> param) {
            this.param = param;
        }

        @Override
        public String getMessageTitle() {
            return title;
        }

        @Override
        public void setMessageTitle(String title) {
            this.title = title;
        }

        @Override
        public String getMessageContent() {
            return content;
        }

        @Override
        public void setMessageContent(String content) {
            this.content = content;
        }

        @Override
        public List<File> getAttachment() {
            return Collections.unmodifiableList(this.attachments);
        }

        @Override
        public void cleanAttachment() {
            this.attachments.clear();
        }

        @Override
        public void addAttachment(File file) {
            this.attachments.add(file);
        }

        @Override
        public void removeAttachment(File file) {
            this.attachments.remove(file);
        }

        @Override
        public void removeAttachmentAt(int index) {
            this.attachments.remove(index);
        }

        @Override
        public void setAttachment(List<File> files) {
            this.attachments.clear();
            this.attachments.addAll(files);
        }

        @Override
        public List<String> getRecipients() {
            return List.of();
        }

    }
}
