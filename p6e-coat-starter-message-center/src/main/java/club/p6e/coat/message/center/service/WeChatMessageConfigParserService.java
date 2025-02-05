package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MailMessageConfigModel;

/**
 * WeChatMessageConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageConfigParserService extends ConfigParserService<MailMessageConfigModel> {

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
