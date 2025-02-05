package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * ShortMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigModel extends ConfigModel {

    /**
     * Set Application ID
     *
     * @param id Application ID
     */
    void setApplicationId(String id);

    /**
     * Get Application ID
     *
     * @return Get Application ID
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
     * Set Application Domain
     *
     * @param domain Application Domain
     */
    void setApplicationDomain(String domain);

    /**
     * Get Application Domain
     *
     * @return Application Domain
     */
    String getApplicationDomain();

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
