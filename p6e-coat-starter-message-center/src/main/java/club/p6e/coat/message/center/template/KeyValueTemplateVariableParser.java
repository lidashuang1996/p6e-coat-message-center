package club.p6e.coat.message.center.template;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * KEY/VALUE 变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = KeyValueTemplateVariableParser.class,
        ignored = KeyValueTemplateVariableParser.class
)
public class KeyValueTemplateVariableParser implements TemplateVariableParser {

    /**
     * 排序
     */
    private static final int ORDER = 0;

    /**
     * 缓存对象
     */
    private Map<String, String> cache = Collections.unmodifiableMap(new HashMap<>());

    /**
     * 初始化字典
     *
     * @param data 字典对象
     */
    public void init(Map<String, String> data) {
        if (data != null) {
            cache = Collections.unmodifiableMap(data);
        }
    }

    /**
     * 读取 KEY/VALUE 数据
     *
     * @return KEY/VALUE 数据对象
     */
    public Map<String, String> getData() {
        return cache;
    }

    @Override
    public String execute(String key) {
        try {
            if (cache != null) {
                final String value = cache.get(key);
                if (value != null) {
                    return value;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
