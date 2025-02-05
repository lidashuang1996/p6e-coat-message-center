package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;

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
    default MessageType type() {
        return MessageType.MOBILE;
    }

    /**
     * Execute Config Parser Service
     * [Config Model] -- Transform --> [Mobile Message Config Model]
     *
     * @param cm Config Model
     * @return Mobile Message Config Model
     */
    @Override
    MobileMessageConfigModel execute(ConfigModel cm);

}
