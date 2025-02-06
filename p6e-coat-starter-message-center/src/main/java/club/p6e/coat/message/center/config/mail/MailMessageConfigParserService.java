package club.p6e.coat.message.center.config.mail;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * MailMessageConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageConfigParserService extends ConfigParserService<MailMessageConfigModel> {

    /**
     * Get Message Type
     *
     * @return Message Type
     */
    @Override
    default MessageType type() {
        return MessageType.MAIL;
    }

    /**
     * Execute Config Parser Service
     * [Config Model] -- Transform --> [Mail Message Config Model]
     *
     * @param cm Config Model
     * @return Mail Message Config Model
     */
    @Override
    MailMessageConfigModel execute(ConfigModel cm);

}
