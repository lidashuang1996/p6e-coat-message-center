package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.WeChatMessageConfigModel;

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
    default MessageType type() {
        return MessageType.WECHAT;
    }

    /**
     * Execute Config Parser Service
     * [Config Model] -- Transform --> [We Chat Message Config Model]
     *
     * @param cm Config Model
     * @return We Chat Message Config Model
     */
    @Override
    WeChatMessageConfigModel execute(ConfigModel cm);

}
