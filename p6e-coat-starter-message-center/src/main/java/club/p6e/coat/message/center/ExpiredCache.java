package club.p6e.coat.message.center;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExpiredCache
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class ExpiredCache implements Serializable {

    /**
     * Expired Cache Model
     */
    @Data
    @Accessors(chain = true)
    public static class Model implements Serializable {
        private volatile long date;
        private volatile long interval;
        private volatile Object data;

        public Model(Object data) {
            this.data = data;
            this.interval = 3600_1000L;
            this.date = System.currentTimeMillis();
        }
    }

    /**
     * Cache Object
     */
    private static final Map<String, ConcurrentHashMap<String, Model>> CACHE = new ConcurrentHashMap<>();

    /**
     * Get Cache Object
     *
     * @param type Cache Type
     * @param key  Cache Key
     * @param <T>  Cache Value Class Type
     * @return Cache Value Class Type
     */
    public static <T> T get(String type, String key) {
        final ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            return null;
        } else {
            final Model model = data.get(key);
            if (model == null) {
                data.remove(key);
                return null;
            } else {
                if (System.currentTimeMillis() > model.getDate() + model.getInterval()) {
                    data.remove(key);
                    return null;
                } else {
                    return (T) model.getData();
                }
            }
        }
    }

    /**
     * Set Cache Object
     *
     * @param type  Cache Type
     * @param key   Cache Key
     * @param value Cache Value
     */
    public static void set(String type, String key, Object value) {
        ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            data = create(type);
        }
        data.put(key, new Model(value));
    }

    /**
     * Clear Cache Object
     */
    public static void clear() {
        for (final ConcurrentHashMap<String, Model> value : CACHE.values()) {
            value.clear();
        }
        CACHE.clear();
    }

    /**
     * Create Cache Object
     *
     * @param type Type
     * @return ConcurrentHashMap Object
     */
    public synchronized static ConcurrentHashMap<String, Model> create(String type) {
        return CACHE.computeIfAbsent(type, k -> new ConcurrentHashMap<>(16));
    }

}
