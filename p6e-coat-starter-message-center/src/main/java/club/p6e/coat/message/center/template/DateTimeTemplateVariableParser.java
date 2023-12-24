package club.p6e.coat.message.center.template;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 当前日期时间变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = DateTimeTemplateVariableParser.class,
        ignored = DateTimeTemplateVariableParser.class
)
public class DateTimeTemplateVariableParser implements TemplateVariableParser {

    /**
     * 排序
     */
    private static final int ORDER = 1000;

    /**
     * 标记的前缀
     */
    private static final String MARK_PREFIX = "CURRENT_DATE_TIME_";

    @Override
    public String execute(String key) {
        if (key.startsWith(MARK_PREFIX)) {
            return DateTimeFormatter.ofPattern(key.substring(MARK_PREFIX.length())).format(LocalDateTime.now());
        }
        return null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}