package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * 启动器类型不匹配异常
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherConfigTypeMismatchException extends CustomException {

    public static final int DEFAULT_CODE = 22800;

    private static final String DEFAULT_SKETCH = "LAUNCHER_TYPE_MISMATCH_EXCEPTION";

    public LauncherConfigTypeMismatchException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
