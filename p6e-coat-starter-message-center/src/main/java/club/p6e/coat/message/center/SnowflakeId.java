package club.p6e.coat.message.center;

import club.p6e.coat.common.utils.SnowflakeIdUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = SnowflakeId.class,
        ignored = SnowflakeId.class
)
public class SnowflakeId {

    public static final String LOG_SNOWFLAKE_NAME = "log";

    public SnowflakeId() {
        SnowflakeIdUtil.register(LOG_SNOWFLAKE_NAME, 0, 0);
    }

    public long generate() {
        return SnowflakeIdUtil.getInstance(LOG_SNOWFLAKE_NAME).nextId();
    }

}
