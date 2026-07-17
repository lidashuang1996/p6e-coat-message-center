package club.p6e.coat.message.center.config.whatsapp;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * WhatsApp Message Config Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WhatsappMessageConfigParserService extends ConfigParserService<WhatsappMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.WHATSAPP;
    }

}
