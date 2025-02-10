package club.p6e.coat.message.center;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExternalSourceClassLoader
 *
 * @author lidashuang
 * @version 1.0
 */
public final class ExternalSourceClassLoader extends ClassLoader {

    private static final ExternalSourceClassLoader INSTANCE = new ExternalSourceClassLoader();

    public static ExternalSourceClassLoader getInstance() {
        return INSTANCE;
    }

    private static byte[] writeName(String name) {
        return writeContent(name.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] writeContent(byte[] bytes) {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            for (final byte b : bytes) {
                final int item = b & 0xFF;
                if (item == 0xFA) {
                    result.write(0xFF);
                    result.write(0x01);
                } else if (item == 0xFB) {
                    result.write(0xFF);
                    result.write(0x02);
                } else if (item == 0xFC) {
                    result.write(0xFF);
                    result.write(0x03);
                } else if (item == 0xFF) {
                    result.write(0xFF);
                    result.write(0x00);
                } else {
                    result.write(b);
                }
            }
            return result.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @SuppressWarnings("ALL")
    public static byte[] classFileToBytes(List<File> files) {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            for (final File file : files) {
                if (file.isFile()) {
                    try (
                            final FileInputStream input = new FileInputStream(file);
                            final ByteArrayOutputStream output = new ByteArrayOutputStream()
                    ) {

                        output.write(0xFA);
                        output.write(writeName(file.getName()));
                        output.write(0xFB);

                        int read;
                        final byte[] buffer = new byte[1024];
                        while ((read = input.read(buffer)) != -1) {
                            final byte[] content = new byte[read];
                            System.arraycopy(buffer, 0, content, 0, read);
                            output.write(writeContent(content));
                        }

                        output.write(0xFC);
                        result.writeBytes(output.toByteArray());
                    }
                } else {
                    throw new RuntimeException(file + " not exist error.");
                }
            }
            return result.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
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
            if (className != null && classBytes != null && classType != null) {
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

    @SuppressWarnings("ALL")
    public <T> T newPackageClassInstance(String name, byte[] bytes, Class<T> type) {
        try {
            if (name != null && bytes != null && type != null) {
                Object result = cache.get(name);
                if (result == null) {
                    load(bytes);
                    result = findClass(name).getDeclaredConstructor().newInstance();
                    cache.put(name, result);
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

    private void load(byte[] bytes) {
        boolean a = false;
        boolean c = false;
        ByteArrayOutputStream ba = null;
        ByteArrayOutputStream bc = null;
        try {
            for (final byte b : bytes) {
                final int item = b & 0xFF;
                if (item == 0xFA) {
                    a = true;
                    ba = new ByteArrayOutputStream();
                } else if (item == 0xFB) {
                    a = false;
                    c = true;
                    if (ba != null) {
                        ba.close();
                    }
                    bc = new ByteArrayOutputStream();
                } else if (item == 0xFC) {
                    c = false;
                    if (bc != null) {
                        bc.close();
                    }
                    if (ba != null && bc != null) {
                        final byte[] ab = read(ba.toByteArray());
                        final byte[] cb = read(bc.toByteArray());
                        if (ab != null && cb != null) {
                            loadClass(new String(ab, StandardCharsets.UTF_8), cb);
                        }
                    }
                } else if (a) {
                    ba.write(item);
                } else if (c) {
                    bc.write(item);
                }
            }
        } catch (Exception e) {
            if (ba != null) {
                try {
                    ba.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (bc != null) {
                try {
                    bc.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private byte[] read(byte[] bytes) {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            for (int i = 0; i < bytes.length; i++) {
                final int item = bytes[i] & 0xFF;
                if (item == 0xFF && i + 1 < bytes.length) {
                    if ((bytes[i + 1] & 0xFF) == 0x00) {
                        result.write(0xFF);
                    } else if ((bytes[i + 1] & 0xFF) == 0x01) {
                        result.write(0xFA);
                    } else if ((bytes[i + 1] & 0xFF) == 0x02) {
                        result.write(0xFB);
                    } else if ((bytes[i + 1] & 0xFF) == 0x03) {
                        result.write(0xFC);
                    } else {
                        result.write(bytes[i]);
                        result.write(bytes[i + 1]);
                    }
                } else {
                    result.write(bytes[i]);
                }
            }
            return result.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
