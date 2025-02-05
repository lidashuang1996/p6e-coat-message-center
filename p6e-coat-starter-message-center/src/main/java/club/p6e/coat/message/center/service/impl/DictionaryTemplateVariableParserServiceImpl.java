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
 * DictionaryTemplateVariableParserServiceImpl
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
     * ORDER
     */
    private static final int ORDER = 2000;

    /**
     * MARK PREFIX
     */
    private static final String MARK_PREFIX = "#DIC_";

    /**
     * Cache Dictionary Object
     */
    private Map<String, Map<String, String>> cache = Collections.unmodifiableMap(new HashMap<>());

    /**
     * Initialize Dictionary
     *
     * @param data Dictionary
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
     * Get Dictionary
     *
     * @return Dictionary
     */
    public Map<String, Map<String, String>> getData() {
        return cache;
    }

    @Override
    public int getOrder() {
        return ORDER;
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
            // ignore exception
        }
        return null;
    }

    /**
     * Execute Dictionary Default Value
     *
     * @param data Dictionary Data
     * @return Default Value
     */
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

}
