package club.p6e.coat.message.center;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class ExpiredCache implements Serializable {

    /**
     * 缓存数据对象
     */
    @Data
    @Accessors(chain = true)
    private static class Model implements Serializable {
        private volatile long date;
        private volatile long interval;
        private volatile Object data;

        public Model(Object data) {
            this.data = data;
            this.interval = 3600_1000L;
            this.date = System.currentTimeMillis();
        }
    }

    @SuppressWarnings("ALL")
    private static final Map<String, ConcurrentHashMap<String, Model>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("ALL")
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

    @SuppressWarnings("ALL")
    public static void set(String type, String key, Object value) {
        ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            data = create(type);
        }
        data.put(key, new Model(value));
    }

    @SuppressWarnings("ALL")
    public static void clean() {
        for (final ConcurrentHashMap<String, Model> value : CACHE.values()) {
            value.clear();
        }
        CACHE.clear();
    }

    @SuppressWarnings("ALL")
    public synchronized static ConcurrentHashMap<String, Model> create(String type) {
        return CACHE.computeIfAbsent(type, k -> new ConcurrentHashMap<>(16));
    }

}
