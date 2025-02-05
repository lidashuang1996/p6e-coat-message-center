package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * WeChatMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageConfigModel extends ConfigModel {

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
     * Set Application Name
     *
     * @param name Application Name
     */
    void setAccessTokenUrl(String name);

    /**
     * Get Application Name
     *
     * @return Application Name
     */
    String getAccessTokenUrl();

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
