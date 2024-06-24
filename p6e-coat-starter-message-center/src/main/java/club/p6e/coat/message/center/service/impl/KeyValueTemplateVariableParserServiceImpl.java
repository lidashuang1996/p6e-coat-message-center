package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.service.TemplateVariableParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        value = KeyValueTemplateVariableParserServiceImpl.class,
        ignored = KeyValueTemplateVariableParserServiceImpl.class
)
public class KeyValueTemplateVariableParserServiceImpl implements TemplateVariableParserService {

    /**
     * 排序
     */
    private static final int ORDER = 0;

    /**
     * 标记的前缀
     */
    private static final String MARK_PREFIX = "#KV_";

    /**
     * 缓存对象
     */
    private Map<String, String> cache = Collections.unmodifiableMap(new HashMap<>());

    /**
     * 初始化字典
     *
     * @param data 字典对象
     */
    @SuppressWarnings("ALL")
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
    public String execute(String key, String language) {
        try {
            if (key.startsWith(MARK_PREFIX)) {
                final String value = cache.get(URLDecoder.decode(
                        key.substring(MARK_PREFIX.length()), StandardCharsets.UTF_8));
                if (value != null) {
                    return value;
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
