package club.p6e.coat.message.center.config.whatsapp;

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
 * WhatsApp Message Config Default Parser Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WhatsappMessageConfigDefaultParserService implements WhatsappMessageConfigParserService {

    /**
     * Parser Name
     */
    private static final String PARSER_NAME = "WHATSAPP_DEFAULT";

    @Override
    public String name() {
        return PARSER_NAME;
    }

    @Override
    public WhatsappMessageConfigModel execute(ConfigModel cm) {
        final SimpleWhatsappMessageConfigModel model = new SimpleWhatsappMessageConfigModel(cm);
        if (cm.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(cm.content(), String.class, Object.class);
            if (data != null) {
                model.setUrl(TransformationUtil.objectToString(data.get("url")));
                model.setToken(TransformationUtil.objectToString(data.get("token")));
                model.setSession(TransformationUtil.objectToString(data.get("session")));
                if (data.get("channel-chat") instanceof final Map<?, ?> content) {
                    final Map<String, String> chats = new HashMap<>();
                    for (final Object key : content.keySet()) {
                        final Object value = content.get(key);
                        if (key instanceof final String k && value instanceof final String v) {
                            chats.put(k, v);
                        }
                    }
                    model.setChats(chats);
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
     * Simple WhatsApp Message Config Model
     */
    public static class SimpleWhatsappMessageConfigModel implements WhatsappMessageConfigModel, Serializable {

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Other Data
         */
        public Map<String, String> other = Collections.unmodifiableMap(new HashMap<>());

        /**
         * Url
         */
        private String url;

        /**
         * Token
         */
        private String token;

        /**
         * Session
         */
        private String session;

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
        public SimpleWhatsappMessageConfigModel(ConfigModel source) {
            this.source = source;
        }

        @Override
        public int id() {
            return this.source == null ? 0 : this.source.id();
        }

        @Override
        public String rule() {
            return this.source == null ? null : this.source.rule();
        }

        @Override
        public MessageCenterType type() {
            return this.source == null ? null : this.source.type();
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
        public String getUrl() {
            return this.url;
        }

        @Override
        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String getSession() {
            return this.session;
        }

        @Override
        public void setSession(String session) {
            this.session = session;
        }

        @Override
        public String getToken() {
            return this.token;
        }

        @Override
        public void setToken(String token) {
            this.token = token;
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
        public Map<String, String> getOther() {
            return other;
        }

        @Override
        public void setOther(Map<String, String> other) {
            if (other != null) {
                this.other = Collections.unmodifiableMap(other);
            }
        }

    }

}
