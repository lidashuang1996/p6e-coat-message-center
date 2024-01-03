package club.p6e.coat.message.center;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class ExternalSourceClassLoader extends ClassLoader {

    /**
     * 实列对象
     */
    private static final ExternalSourceClassLoader INSTANCE = new ExternalSourceClassLoader();

    /**
     * 获取单例的实列对象
     *
     * @return 实列对象
     */
    public static ExternalSourceClassLoader getInstance() {
        return INSTANCE;
    }

    /**
     * 缓存对象
     */
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    /**
     * 临时保存的 CLASS BYTES 字节码
     */
    private final Map<String, byte[]> classBytesMap = new ConcurrentHashMap<>();

    /**
     * 私有的构造方法
     */
    private ExternalSourceClassLoader() {
    }

    @SuppressWarnings("ALL")
    public <T> T newClassInstance(String className, byte[] classBytes, Class<T> classType) {
        try {
            if (className != null) {
                Object result = cache.get(className);
                if (result == null) {
                    final Class<?> clazz = loadClass(className, classBytes);
                    result = clazz.getDeclaredConstructor().newInstance();
                    cache.put(className, result);
                }
                return (T) result;
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private Class<?> loadClass(String className, byte[] classBytes) throws ClassNotFoundException {
        classBytesMap.put(className, classBytes);
        return loadClass(className);
    }

    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {
        final byte[] classBytes = classBytesMap.get(className);
        if (classBytes == null) {
            throw new ClassNotFoundException(className);
        }
        return defineClass(className, classBytes, 0, classBytes.length);
    }

}
