package club.p6e.coat.message.center.variable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 当前日期时间变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public class DateTimeVariableParser implements VariableParser {

    /**
     * 排序
     */
    private static final int ORDER = 1000;

    /**
     * 标记的前缀
     */
    private static final String MARK_PREFIX = "CURRENT_DATE_TIME_";

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public String execute(String key) {
        if (key.startsWith(MARK_PREFIX)) {
            return DateTimeFormatter.ofPattern(key.substring(MARK_PREFIX.length())).format(LocalDateTime.now());
        }
        return null;
    }

}
