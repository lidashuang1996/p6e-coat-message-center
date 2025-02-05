package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * TelegramMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TelegramMessageConfigModel extends ConfigModel {

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
     * @param channel
     * @param chat
     */
    void putChannelChat(String channel, String chat);

    /**
     * @param channelChats
     */
    void setChannelChats(Map<String, String> channelChats);

    /**
     * @return
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

}
