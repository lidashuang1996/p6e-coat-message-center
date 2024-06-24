package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * 启动器路由配置转换异常
 *
 * @author lidashuang
 * @version 1.0
 */
public class LauncherRouteConfigConvertException extends CustomException {

    public static final int DEFAULT_CODE = 22500;

    private static final String DEFAULT_SKETCH = "LAUNCHER_ROUTE_CONFIG_CONVERT_EXCEPTION";

    public LauncherRouteConfigConvertException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
