package club.p6e.coat.message.center.error;

import club.p6e.coat.common.error.CustomException;
import club.p6e.coat.common.error.ResourceException;

/**
 * 启动器未启用异常
 *
 * @author lidashuang
 * @version 1.0
 */
public class MessageCenterConfigException extends CustomException {

    public static final int DEFAULT_CODE = 22200;

    private static final String DEFAULT_SKETCH = "LAUNCHER_NO_ENABLE_EXCEPTION";

    public MessageCenterConfigException(Class<?> sc, String error, String content) {
        super(sc, ResourceException.class, error, DEFAULT_CODE, DEFAULT_SKETCH, content);
    }

}
