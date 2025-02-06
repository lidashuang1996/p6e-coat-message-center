package club.p6e.coat.message.center.config.mail;

import club.p6e.coat.message.center.MessageCenterType;
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
    default MessageCenterType type() {
        return MessageCenterType.MAIL;
    }

}
