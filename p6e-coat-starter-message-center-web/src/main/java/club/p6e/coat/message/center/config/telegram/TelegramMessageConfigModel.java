package club.p6e.coat.message.center.config.telegram;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * TelegramMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TelegramMessageConfigModel extends ConfigModel, Serializable {

    /**
     * Set Bot Token
     *
     * @param token Bot Token
     */
    void setBotToken(String token);

    /**
     * Get Bot Token
     *
     * @return Bot Token
     */
    String getBotToken();

    /**
     * Set Bot Username
     *
     * @param botUsername Bot Username
     */
    void setBotUsername(String botUsername);

    /**
     * Get Bot Username
     *
     * @return Bot Username
     */
    String getBotUsername();

    /**
     * Set Chat Channel
     *
     * @param chat    Chat Value
     * @param channel Channel Value
     */
    void putChatChannel(String chat, String channel);

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
     * Get Proxy
     *
     * @return Proxy
     */
    boolean isProxy();

    /**
     * Set Proxy
     *
     * @param proxy Proxy
     */
    void setProxy(boolean proxy);

    /**
     * Set Proxy Port
     *
     * @param proxyPort Proxy Port
     */
    void setProxyPort(int proxyPort);

    /**
     * Get Proxy Port
     *
     * @return Proxy
     */
    int getProxyPort();

    /**
     * Set Proxy Host
     *
     * @param proxyHost Proxy Host
     */
    void setProxyHost(String proxyHost);

    /**
     * Get Proxy Host
     *
     * @return Proxy Host
     */
    String getProxyHost();

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
