package club.p6e.coat.message.center;

import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherStartingModel;

/**
 * MessageCenterService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MessageCenterService {

    /**
     * Push Message
     *
     * @param starting Launcher Starting Model
     * @return Launcher Result Model
     */
    LauncherResultModel push(LauncherStartingModel starting);

}
