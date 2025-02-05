package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;

/**
 * ConfigParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigParserService<T extends ConfigModel> {

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
     * Execute Config Parser Service
     * [Config Model] -- Transform --> [Config Model Subclass]
     *
     * @param cm Config Model
     * @return Config Model Subclass
     */
    T execute(ConfigModel cm);

}
