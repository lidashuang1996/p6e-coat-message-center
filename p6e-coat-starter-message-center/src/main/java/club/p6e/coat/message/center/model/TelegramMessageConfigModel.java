package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * 邮件消息配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TelegramMessageConfigModel extends ConfigModel {

    void setBotToken(String botToken);
    String getBotToken();

    void setBotUsername(String botUsername);
    String getBotUsername();

    void putChannelChat(String channel, String chat);
    void setChannelChats(Map<String, String> channelChats);
    Map<String, String> getChats();

    boolean isProxy();
    void setProxy(boolean proxy);

    void setProxyHost(String proxyHost);
    String getProxyHost();

    void setProxyPort(int proxyPort);
    int getProxyPort();

}
