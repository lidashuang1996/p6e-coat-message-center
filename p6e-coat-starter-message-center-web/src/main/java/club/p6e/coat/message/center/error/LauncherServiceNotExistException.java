package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * 启动服务不存在异常
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherServiceNotExistException extends CustomException {

    public static final int DEFAULT_CODE = 22900;

    private static final String DEFAULT_SKETCH = "LAUNCHER_SERVICE_NO_EXIST_EXCEPTION";

    public LauncherServiceNotExistException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
