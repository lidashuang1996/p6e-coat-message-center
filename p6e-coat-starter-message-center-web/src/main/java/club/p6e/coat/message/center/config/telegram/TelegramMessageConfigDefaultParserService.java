package club.p6e.coat.message.center.config.telegram;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigModel;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TelegramMessageConfigParserServiceImpl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class TelegramMessageConfigDefaultParserService implements TelegramMessageConfigParserService {

    /**
     * Parser Name
     */
    private static final String DEFAULT_PARSER = "TELEGRAM_CONFIG_DEFAULT_PARSER";

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public TelegramMessageConfigModel execute(ConfigModel config) {
        final SimpleTelegramMessageConfigModel model = new SimpleTelegramMessageConfigModel(config);
        if (config.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(config.content(), String.class, Object.class);
            if (data != null) {
                if (data.get("bot-token") instanceof final String content) {
                    model.setBotToken(content);
                }
                if (data.get("bot-username") instanceof final String content) {
                    model.setBotUsername(content);
                }
                if (data.get("proxy") instanceof final String content) {
                    model.setProxy("1".equals(content) || "true".equalsIgnoreCase(content));
                }
                if (data.get("proxy-host") instanceof final String content) {
                    model.setProxyHost(content);
                }
                if (data.get("proxy-port") instanceof final String content) {
                    model.setProxyPort(Integer.parseInt(content));
                }
                if (data.get("channel-chat") instanceof final Map<?, ?> content) {
                    for (final Object key : content.keySet()) {
                        final Object value = content.get(key);
                        if (key instanceof final String k && value instanceof final String v) {
                            model.putChatChannel(k, v);
                        }
                    }
                }
                final Map<String, String> other = new HashMap<>();
                for (final String key : data.keySet()) {
                    other.put(key, TransformationUtil.objectToString(data.get(key)));
                }
                model.setOther(other);
            }
        }
        return model;
    }

    /**
     * SimpleTelegramMessageConfigModel
     */
    public static class SimpleTelegramMessageConfigModel implements TelegramMessageConfigModel, Serializable {

        /**
         * Proxy
         */
        private boolean proxy;

        /**
         * Proxy Port
         */
        private int proxyPort = 7890;

        /**
         * Proxy Host
         */
        private String proxyHost = "localhost";

        /**
         * Bot Token
         */
        private String botToken;

        /**
         * Bot Username
         */
        private String botUsername;

        /**
         * Other Data
         */
        private Map<String, String> other = Collections.unmodifiableMap(new HashMap<>());

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Chats
         */
        private final Map<String, String> chats = new ConcurrentHashMap<>();

        /**
         * Construct Initialization
         * Inject Source Config Model Object
         *
         * @param source Source Config Model
         */
        public SimpleTelegramMessageConfigModel(ConfigModel source) {
            this.source = source;
        }

        @Override
        public int id() {
            return this.source == null ? 0 : this.source.id();
        }

        @Override
        public boolean enable() {
            return this.source != null && this.source.enable();
        }

        @Override
        public String name() {
            return this.source == null ? null : this.source.name();
        }

        @Override
        public MessageCenterType type() {
            return this.source == null ? null : this.source.type();
        }

        @Override
        public String content() {
            return this.source == null ? null : this.source.content();
        }

        @Override
        public String description() {
            return this.source == null ? null : this.source.description();
        }

        @Override
        public String parser() {
            return this.source == null ? null : this.source.parser();
        }

        @Override
        public byte[] parserSource() {
            return this.source == null ? null : this.source.parserSource();
        }

        @Override
        public String rule() {
            return this.source == null ? null : this.source.rule();
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
        public void putChatChannel(String chat, String channel) {
            this.chats.put(chat, channel);
        }

        @Override
        public void setChats(Map<String, String> chats) {
            if (chats != null) {
                this.chats.clear();
                this.chats.putAll(chats);
            }
        }

        @Override
        public Map<String, String> getChats() {
            return Collections.unmodifiableMap(this.chats);
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
        public void setOther(Map<String, String> other) {
            if (other != null) {
                this.other = Collections.unmodifiableMap(other);
            }
        }

        @Override
        public Map<String, String> getOther() {
            return other;
        }

    }

}
