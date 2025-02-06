package club.p6e.coat.message.center.config.sms;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * ShortMessageConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigParserService extends ConfigParserService<ShortMessageConfigModel> {

    /**
     * Get Message Type
     *
     * @return Message Type
     */
    @Override
    default MessageType type() {
        return MessageType.SMS;
    }

    /**
     * Execute Config Parser Service
     * [Config Model] -- Transform --> [Short Message Config Model]
     *
     * @param cm Config Model
     * @return Short Message Config Model
     */
    ShortMessageConfigModel execute(ConfigModel cm);

}
