package club.p6e.coat.message.center.variable;

import java.util.Collections;
import java.util.Map;

/**
 * 字典变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public class DictionaryVariableParser implements VariableParser {

    /**
     * 排序
     */
    private static final int ORDER = 2000;

    /**
     * 字典的缓存对象
     */
    private static Map<String, String> CACHE = null;

    /**
     * 初始化字典
     *
     * @param data 字典对象
     */
    public static void init(Map<String, String> data) {
        CACHE = Collections.unmodifiableMap(data);
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public String execute(String key) {
        try {
            if (CACHE != null) {
                final String value = CACHE.get(key);
                if (value != null) {
                    return value;
                }
            }
        } catch (Exception e) {
            // ...
        }
        return null;
    }

}
