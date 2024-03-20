package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * @author lidashuang
 * @version 1.0
 */
public class LauncherRouteConfigException extends CustomException {

    public static final int DEFAULT_CODE = 22400;

    private static final String DEFAULT_SKETCH = "LAUNCHER_ROUTE_CONFIG_EXCEPTION";

    public LauncherRouteConfigException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
