package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageType;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * LauncherModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherStartingModel extends Serializable {

    /**
     * ID
     *
     * @return ID
     */
    Integer id();

    /**
     * Enable
     *
     * @return Enable
     */
    boolean enable();

    /**
     * Type
     *
     * @return Type
     */
    MessageType type();

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Template
     *
     * @return Template
     */
    String template();

    /**
     * Description
     *
     * @return Description
     */
    String description();

    /**
     * Route
     *
     * @return Route
     */
    String route();

    /**
     * RouteSource
     *
     * @return RouteSource
     */
    byte[] routeSource();

    /**
     * Parser
     *
     * @return Parser
     */
    String parser();

    /**
     * Parser Source
     *
     * @return ParserSource
     */
    List<String> recipients();

    /**
     * Config List
     *
     * @return Config List
     */
    List<File> attachment();

    /**
     * 发射器映射配置模型
     */
    interface ConfigMapperModel {

        /**
         * ID
         *
         * @return ID
         */
        Integer id();

        /**
         * Attribute
         *
         * @return Attribute
         */
        String attribute();

    }

}
