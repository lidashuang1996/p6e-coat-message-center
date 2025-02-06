package club.p6e.coat.message.center.config.telegram;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * TelegramMessageConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TelegramMessageConfigParserService extends ConfigParserService<TelegramMessageConfigModel> {

    /**
     * Get Message Type
     *
     * @return Message Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.TELEGRAM;
    }

}
