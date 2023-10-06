package club.p6e.coat.message.center.variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * KEY/VALUE 变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class KeyValueVariableParser implements VariableParser {

    /**
     * 排序
     */
    private static final int ORDER = 0;

    /**
     * 默认的对象
     */
    private static final Map<String, String> DEFAULT = Collections.unmodifiableMap(new HashMap<>());

    /**
     * KEY/VALUE 数据对象
     */
    private final Map<String, String> data;

    /**
     * 构造方法初始化
     */
    public KeyValueVariableParser() {
        this(null);
    }

    /**
     * 构造方法初始化
     *
     * @param data KEY/VALUE 数据对象
     */
    public KeyValueVariableParser(Map<String, String> data) {
        if (data == null) {
            this.data = DEFAULT;
        } else {
            this.data = Collections.unmodifiableMap(data);
        }
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public String execute(String key) {
        return data.get(key);
    }

    /**
     * 获取 KEY/VALUE 数据对象
     *
     * @return KEY/VALUE 数据对象
     */
    public Map<String, String> getData() {
        return data;
    }

}
