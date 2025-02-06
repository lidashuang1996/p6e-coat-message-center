package club.p6e.coat.message.center.config.mobile;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * MobileMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigModel extends ConfigModel, Serializable {

    /**
     * Set Application ID
     *
     * @param id Application ID
     */
    void setApplicationId(String id);

    /**
     * Get Application ID
     *
     * @return Application ID
     */
    String getApplicationId();

    /**
     * Set Application Key
     *
     * @param key Application Key
     */
    void setApplicationKey(String key);

    /**
     * Get Application Key
     *
     * @return Application Key
     */
    String getApplicationKey();

    /**
     * Set Application Name
     *
     * @param name Application Name
     */
    void setApplicationName(String name);

    /**
     * Get Application Name
     *
     * @return Application Name
     */
    String getApplicationName();

    /**
     * Set Application Secret
     *
     * @param secret Application Secret
     */
    void setApplicationSecret(String secret);

    /**
     * Get Application Secret
     *
     * @return Application Secret
     */
    String getApplicationSecret();

    /**
     * Set Other
     *
     * @param other Other
     */
    void setOther(Map<String, String> other);

    /**
     * Get Other
     *
     * @return Other
     */
    Map<String, String> getOther();

}
