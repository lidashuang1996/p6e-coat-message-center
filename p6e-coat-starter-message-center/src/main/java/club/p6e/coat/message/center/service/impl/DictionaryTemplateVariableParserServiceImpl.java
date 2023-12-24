package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.service.TemplateVariableParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

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
     * 读取字典内容
     *
     * @return 字典内容对象
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
