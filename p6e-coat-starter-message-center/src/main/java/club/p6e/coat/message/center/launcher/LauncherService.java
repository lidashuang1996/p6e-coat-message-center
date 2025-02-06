package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigModel;

/**
 * LauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherService<T extends ConfigModel> {

    /**
     * Get Name
     *
     * @return Name
     */
    String name();

    /**
     * Get Message Center Type
     *
     * @return Message Center Type
     */
    MessageCenterType type();

    /**
     * Execute Launcher Service
     *
     * @param ltm    Launcher Template Model
     * @param config Config Model
     * @return Launcher Result Model
     */
    LauncherResultModel execute(LauncherTemplateModel ltm, T config);

}
