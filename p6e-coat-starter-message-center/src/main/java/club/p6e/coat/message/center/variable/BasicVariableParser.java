package club.p6e.coat.message.center.variable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础的变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public class BasicVariableParser extends KeyValueVariableParser implements VariableParser {

    /**
     * 基本变量对象缓存
     */
    private static final Map<String, VariableParser> CACHE = new ConcurrentHashMap<>();

    static {
        register("DATE_TIME_VARIABLE_PARSER", new DateTimeVariableParser());
        register("DICTIONARY_VARIABLE_PARSER", new DictionaryVariableParser());
    }

    /**
     * 卸载变量解析器
     *
     * @param key 解析器名称
     */
    public static void unregister(String key) {
        CACHE.remove(key);
    }

    /**
     * 注册变量解析器
     *
     * @param key   解析器名称
     * @param value 解析器对象
     */
    public static void register(String key, VariableParser value) {
        CACHE.put(key, value);
    }

    /**
     * 构造方法初始化
     */
    public BasicVariableParser() {
        super(null);
    }

    /**
     * 构造方法初始化
     *
     * @param data KEY/VALUE 数据对象
     */
    public BasicVariableParser(Map<String, String> data) {
        super(data);
    }

    @Override
    public String execute(String key) {
        for (final VariableParser parser : CACHE.values().stream().sorted().toList()) {
            final String value = parser.execute(key);
            if (value != null) {
                return value;
            }
        }
        return super.execute(key);
    }

}
