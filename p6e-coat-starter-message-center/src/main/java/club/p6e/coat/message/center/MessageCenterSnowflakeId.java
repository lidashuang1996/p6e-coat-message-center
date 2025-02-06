package club.p6e.coat.message.center;

import club.p6e.coat.common.utils.SnowflakeIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * SnowflakeId
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MessageCenterSnowflakeId.class,
        ignored = MessageCenterSnowflakeId.class
)
public class MessageCenterSnowflakeId {

    /**
     * Snowflake Name
     */
    public static final String SNOWFLAKE_NAME = "MESSAGE_CENTER_LOG_SNOWFLAKE";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCenterSnowflakeId.class);

    /**
     * Construct Initialization
     */
    public MessageCenterSnowflakeId() {
        SnowflakeIdUtil.register(SNOWFLAKE_NAME, 0, 0);
        LOGGER.info("[ MESSAGE CENTER ] SNOWFLAKE ID >>> ( {} ) WORKER ID: {}, DATACENTER ID: {}", SNOWFLAKE_NAME, 0, 0);
    }

    /**
     * Generate Snowflake ID
     *
     * @return Snowflake ID
     */
    public long generate() {
        return SnowflakeIdUtil.getInstance(SNOWFLAKE_NAME).nextId();
    }

}
