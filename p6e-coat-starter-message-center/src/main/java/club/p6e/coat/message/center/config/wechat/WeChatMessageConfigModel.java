package club.p6e.coat.message.center.config.wechat;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * WeChatMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WeChatMessageConfigModel extends ConfigModel, Serializable {

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
     * Set Access Token Url
     *
     * @param url Access Token Url
     */
    void setAccessTokenUrl(String url);

    /**
     * Get Access Token Url
     *
     * @return Access Token Url
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
