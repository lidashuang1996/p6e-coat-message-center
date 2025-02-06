package club.p6e.coat.message.center.template;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * KeyValueTemplateVariableParserServiceImpl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class KeyValueTemplateVariableParserService implements TemplateVariableParserService {

    /**
     * ORDER
     */
    private static final int ORDER = 0;

    /**
     * MARK PREFIX
     */
    private static final String MARK_PREFIX = "#KV_";

    /**
     * Cache Object
     */
    private Map<String, String> cache = Collections.unmodifiableMap(new HashMap<>());

    /**
     * Initialize Key/Value
     *
     * @param data Key/Value
     */
    @SuppressWarnings("ALL")
    public void init(Map<String, String> data) {
        if (data != null) {
            cache = Collections.unmodifiableMap(data);
        }
    }

    /**
     * Get Key/Value
     *
     * @return Key/Value
     */
    public Map<String, String> getData() {
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
                final String value = cache.get(URLDecoder.decode(
                        key.substring(MARK_PREFIX.length()), StandardCharsets.UTF_8));
                if (value != null) {
                    return value;
                }
            }
        } catch (Exception ignore) {
            // ignore exception
        }
        return null;
    }

}
