package club.p6e.coat.message.center.config.mobile;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * MobileMessageConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigParserService extends ConfigParserService<MobileMessageConfigModel> {

    /**
     * Get Message Type
     *
     * @return Message Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.MOBILE;
    }

}
