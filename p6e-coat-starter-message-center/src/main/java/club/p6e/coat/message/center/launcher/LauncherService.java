package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.template.TemplateModel;

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
     * Get Message Type
     *
     * @return Message Type
     */
    MessageCenterType type();

    /**
     * Execute Launcher Service
     */
    LauncherResultModel execute(LauncherStartingModel starting, TemplateModel template, T config);

}
