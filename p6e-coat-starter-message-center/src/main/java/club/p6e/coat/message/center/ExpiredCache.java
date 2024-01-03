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

    @Data
    @Accessors(chain = true)
    private static class Model implements Serializable {
        private volatile long date;
        private volatile long interval;
        private volatile Object data;

        public Model(Object data) {
            this.data = data;
            this.interval = 20000;
            this.date = System.currentTimeMillis();
        }
    }


    private static final Map<String, ConcurrentHashMap<String, Model>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("ALL")
    public static <T> T get(String type, String key) {
        final ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            return null;
        } else {
            return (T) data.get(key).getData();
        }
    }

    public static void set(String type, String key, Object value) {
        ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            data = create(type);
        }
        data.put(key, new Model(value));
    }

    @SuppressWarnings("ALL")
    public synchronized static ConcurrentHashMap<String, Model> create(String type) {
        return CACHE.computeIfAbsent(type, k -> new ConcurrentHashMap<>(16));
    }

}
