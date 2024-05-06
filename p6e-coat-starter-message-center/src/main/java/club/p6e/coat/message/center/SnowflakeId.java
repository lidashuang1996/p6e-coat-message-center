package club.p6e.coat.message.center;

import club.p6e.coat.common.utils.SnowflakeIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 雪花算法 ID 配置
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class SnowflakeId {

    /**
     * 消息中心日志的雪花算法 ID 名称
     */
    public static final String MESSAGE_CENTER_LOG_SNOWFLAKE_NAME = "message-center-log";

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeId.class);

    /**
     * 构造方法初始化
     */
    public SnowflakeId() {
        SnowflakeIdUtil.register(MESSAGE_CENTER_LOG_SNOWFLAKE_NAME, 0, 0);
        LOGGER.info("[ MESSAGE CENTER SNOWFLAKE ID ] >>> ( {} ) WORKER ID: {}, DATACENTER ID: {}", MESSAGE_CENTER_LOG_SNOWFLAKE_NAME, 0, 0);
    }

    /**
     * 生成下一个 ID
     *
     * @return 下一个 ID
     */
    public long generate() {
        return SnowflakeIdUtil.getInstance(MESSAGE_CENTER_LOG_SNOWFLAKE_NAME).nextId();
    }

}
