package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

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
    MessageType type();

    /**
     * Execute Launcher Service
     */
    Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, T config);

}
