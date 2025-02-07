package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * 启动器映射配置不存在异常
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherMapperConfigNotExistException extends CustomException {

    public static final int DEFAULT_CODE = 22300;

    private static final String DEFAULT_SKETCH = "LAUNCHER_MAPPER_CONFIG_NO_EXIST_EXCEPTION";

    public LauncherMapperConfigNotExistException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
