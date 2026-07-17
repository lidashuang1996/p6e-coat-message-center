package club.p6e.coat.message.center.launcher.whatsapp;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.whatsapp.WhatsappMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherService;

/**
 * WhatsApp Message Launcher Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WhatsappMessageLauncherService extends LauncherService<WhatsappMessageConfigModel> {

    @Override
    default MessageCenterType type() {
        return MessageCenterType.WHATSAPP;
    }

}
