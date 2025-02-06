package club.p6e.coat.message.center.config.wechat;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigParserService;

/**
 * WeChatMessageConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageConfigParserService extends ConfigParserService<WeChatMessageConfigModel> {

    /**
     * Get Message Type
     *
     * @return Message Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.WECHAT;
    }

}
