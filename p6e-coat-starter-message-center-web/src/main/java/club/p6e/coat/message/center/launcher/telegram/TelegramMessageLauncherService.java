package club.p6e.coat.message.center.launcher.telegram;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.telegram.TelegramMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * TelegramMessageLauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TelegramMessageLauncherService extends LauncherService<TelegramMessageConfigModel> {

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.TELEGRAM;
    }

}
