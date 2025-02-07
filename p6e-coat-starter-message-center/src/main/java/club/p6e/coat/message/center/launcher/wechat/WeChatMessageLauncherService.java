package club.p6e.coat.message.center.launcher.wechat;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.wechat.WeChatMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * WeChatMessageLauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageLauncherService extends LauncherService<WeChatMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.WECHAT;
    }

}
