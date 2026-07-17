package club.p6e.coat.message.center.config.whatsapp;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * WhatsApp Message Config Model
 *
 * @author lidashuang
 * @version 1.0
 */
public interface WhatsappMessageConfigModel extends ConfigModel, Serializable {

    /**
     * Get Url
     *
     * @return Url
     */
    String getUrl();

    /**
     * Set Url
     *
     * @param url Url
     */
    void setUrl(String url);

    /**
     * Get Session
     *
     * @return Session
     */
    String getSession();

    /**
     * Set Session
     *
     * @param session Session
     */
    void setSession(String session);

    /**
     * Get Api Token
     *
     * @return Api Token
     */
    String getToken();

    /**
     * Set Api Token
     *
     * @param token Api Token
     */
    void setToken(String token);

    /**
     * Set Chats
     *
     * @param chats Chats
     */
    void setChats(Map<String, String> chats);

    /**
     * Get Chats
     *
     * @return Chats
     */
    Map<String, String> getChats();

    /**
     * Get Other
     *
     * @return Other
     */
    Map<String, String> getOther();

    /**
     * Set Other
     *
     * @param other Other
     */
    void setOther(Map<String, String> other);

}
