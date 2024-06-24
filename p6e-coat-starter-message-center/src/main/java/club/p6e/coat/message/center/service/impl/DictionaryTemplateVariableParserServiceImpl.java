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
 * 字典变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = DictionaryTemplateVariableParserServiceImpl.class,
        ignored = DictionaryTemplateVariableParserServiceImpl.class
)
public class DictionaryTemplateVariableParserServiceImpl implements TemplateVariableParserService {

    /**
     * 排序
     */
    private static final int ORDER = 2000;

    /**
     * 标记的前缀
     */
    private static final String MARK_PREFIX = "#DIC_";

    /**
     * 缓存对象
     */
    private Map<String, Map<String, String>> cache = Collections.unmodifiableMap(new HashMap<>());

    /**
     * 初始化字典
     *
     * @param data 字典对象
     */
    @SuppressWarnings("ALL")
    public void init(Map<String, Map<String, String>> data) {
        if (data != null) {
            final Map<String, Map<String, String>> map = new HashMap<>();
            for (final Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                map.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
            }
            cache = Collections.unmodifiableMap(map);
        }
    }

    /**
     * 读取字典内容
     *
     * @return 字典内容对象
     */
    public Map<String, Map<String, String>> getData() {
        return cache;
    }

    @Override
    public String execute(String key, String language) {
        try {
            if (key.startsWith(MARK_PREFIX)) {
                final Map<String, String> data = cache.get(URLDecoder.decode(
                        key.substring(MARK_PREFIX.length()), StandardCharsets.UTF_8));
                if (data != null) {
                    if (language == null) {
                        return executeDefaultValue(data);
                    } else {
                        final String result = data.get(language);
                        if (result == null) {
                            return executeDefaultValue(data);
                        } else {
                            return result;
                        }
                    }
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return null;
    }

    private String executeDefaultValue(Map<String, String> data) {
        if (data == null) {
            return null;
        } else {
            if (data.get("_") == null) {
                if (data.get("-") == null) {
                    return null;
                } else {
                    return data.get("-");
                }
            } else {
                return data.get("_");
            }
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
