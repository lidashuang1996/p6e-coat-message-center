package club.p6e.coat.message.center.model;

import club.p6e.coat.message.center.MessageType;

import java.io.Serializable;

/**
 * ConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigModel extends Serializable {

    /**
     * ID
     *
     * @return ID
     */
    int id();

    /**
     * Rule
     *
     * @return 限流规则
     */
    String rule();

    /**
     * Type
     *
     * @return Type
     */
    MessageType type();

    /**
     * Enable
     *
     * @return Enable
     */
    boolean enable();

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Content
     *
     * @return Content
     */
    String content();

    /**
     * Description
     *
     * @return Description
     */
    String description();

    /**
     * Parser
     *
     * @return Parser
     */
    String parser();

    /**
     * Parser Source
     *
     * @return Parser Source
     */
    byte[] parserSource();

}
