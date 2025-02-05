package club.p6e.coat.message.center.model;

import java.io.Serializable;

/**
 * TemplateModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateModel extends Serializable {

    /**
     * ID
     *
     * @return ID
     */
    Integer id();

    /**
     * Key
     *
     * @return Key
     */
    String key();

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Language
     *
     * @return Language
     */
    String language();

    /**
     * Type
     *
     * @return Type
     */
    String type();

    /**
     * Title
     *
     * @return Title
     */
    String title();

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
