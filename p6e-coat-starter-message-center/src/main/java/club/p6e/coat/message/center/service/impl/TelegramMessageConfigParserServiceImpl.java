package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.TelegramMessageConfigModel;
import club.p6e.coat.message.center.service.TelegramMessageConfigParserService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class TelegramMessageConfigParserServiceImpl implements TelegramMessageConfigParserService {

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "TELEGRAM_DEFAULT";

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public TelegramMessageConfigModel execute(ConfigModel config) {
        return new SimpleTelegramMessageConfigModel(config) {{
            if (config.content() != null) {
                final Map<String, Object> data = JsonUtil.fromJsonToMap(config.content(), String.class, Object.class);
                if (data != null) {
                    if (data.get("bot-token") instanceof final String content) {
                        setBotToken(content);
                    }
                    if (data.get("bot-username") instanceof final String content) {
                        setBotUsername(content);
                    }
                    if (data.get("proxy") instanceof final String content) {
                        setProxy("1".equals(content) || "true".equalsIgnoreCase(content));
                    }
                    if (data.get("proxy-host") instanceof final String content) {
                        setProxyHost(content);
                    }
                    if (data.get("proxy-port") instanceof final String content) {
                        setProxyPort(Integer.parseInt(content));
                    }
                    if (data.get("channel-chat") instanceof final Map<?, ?> content) {
                        for (final Object key : content.keySet()) {
                            final Object value = content.get(key);
                            if (key instanceof final String k && value instanceof final String v) {
                                putChannelChat(k, v);
                            }
                        }
                    }
                }
            }
        }};
    }

    /**
     * 简单邮件消息配置模型
     */
    public static class SimpleTelegramMessageConfigModel implements TelegramMessageConfigModel, Serializable {

        private boolean proxy;
        private String botToken;
        private String botUsername;
        private String proxyHost;
        private int proxyPort;

        private final Map<String, String> channelChats = new ConcurrentHashMap<>();

        /**
         * 源配置对象
         */
        private final ConfigModel model;

        public SimpleTelegramMessageConfigModel(ConfigModel model) {
            this.model = model;
        }

        @Override
        public void setBotToken(String botToken) {
            this.botToken = botToken;
        }

        @Override
        public String getBotToken() {
            return this.botToken;
        }

        @Override
        public void setBotUsername(String botUsername) {
            this.botUsername = botUsername;
        }

        @Override
        public String getBotUsername() {
            return this.botUsername;
        }

        @Override
        public void putChannelChat(String channel, String chat) {
            this.channelChats.put(channel, chat);
        }

        @Override
        public void setChannelChats(Map<String, String> channelChats) {
            this.channelChats.clear();
            this.channelChats.putAll(channelChats);
        }


        @Override
        public Map<String, String> getChats() {
            return this.channelChats;
        }

        @Override
        public boolean isProxy() {
            return proxy;
        }

        @Override
        public void setProxy(boolean proxy) {
            this.proxy = proxy;
        }

        @Override
        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }

        @Override
        public String getProxyHost() {
            return this.proxyHost;
        }

        @Override
        public void setProxyPort(int proxyPort) {
            this.proxyPort = proxyPort;
        }

        @Override
        public int getProxyPort() {
            return this.proxyPort;
        }

        @Override
        public int id() {
            return model == null ? 0 : model.id();
        }

        @Override
        public boolean enable() {
            return model != null && model.enable();
        }

        @Override
        public String name() {
            return model == null ? null : model.name();
        }

        @Override
        public MessageType type() {
            return model == null ? null : model.type();
        }

        @Override
        public String content() {
            return model == null ? null : model.content();
        }

        @Override
        public String description() {
            return model == null ? null : model.description();
        }

        @Override
        public String parser() {
            return model == null ? null : model.parser();
        }

        @Override
        public byte[] parserSource() {
            return model == null ? null : model.parserSource();
        }

        @Override
        public String rule() {
            return model == null ? null : model.rule();
        }
    }

}
