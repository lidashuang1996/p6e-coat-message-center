package club.p6e.coat.message.center.launcher.mobile;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.mobile.MobileMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * MobileMessageLauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherService extends LauncherService<MobileMessageConfigModel> {

    /**
     * Get Message Type
     *
     * @return Message Type
     */
    @Override
    default MessageCenterType type() {
        return MessageCenterType.MOBILE;
    }

}
