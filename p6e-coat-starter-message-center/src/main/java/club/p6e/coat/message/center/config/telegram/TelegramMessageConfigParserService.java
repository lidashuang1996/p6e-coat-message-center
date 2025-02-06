package club.p6e.coat.message.center.config.telegram;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.config.ConfigModel;
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
    default MessageType type() {
        return MessageType.TELEGRAM;
    }

    /**
     * Execute Config Parser Service
     * [Config Model] -- Transform --> [Telegram Message Config Model]
     *
     * @param cm Config Model
     * @return Telegram Message Config Model
     */
    @Override
    TelegramMessageConfigModel execute(ConfigModel cm);

}
