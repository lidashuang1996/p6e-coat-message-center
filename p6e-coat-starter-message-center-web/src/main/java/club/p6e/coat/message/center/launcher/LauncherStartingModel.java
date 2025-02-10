package club.p6e.coat.message.center.launcher;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * LauncherStartingModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherStartingModel extends Serializable {

    /**
     * Get ID
     *
     * @return ID
     */
    Integer id();

    /**
     * Get Language
     *
     * @return Language
     */
    String language();

    /**
     * Get Param
     *
     * @return Param
     */
    Map<String, String> param();

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

}
