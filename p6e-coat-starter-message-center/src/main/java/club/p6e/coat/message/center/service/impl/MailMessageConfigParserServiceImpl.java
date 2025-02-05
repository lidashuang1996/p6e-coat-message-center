package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.service.MailMessageConfigParserService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MailMessageConfigParserServiceImpl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MailMessageConfigParserServiceImpl implements MailMessageConfigParserService {

    /**
     * PARSER_NAME
     */
    private static final String PARSER_NAME = "MAIL_CONFIG_DEFAULT_PARSER";

    @Override
    public String name() {
        return PARSER_NAME;
    }

    @Override
    public MailMessageConfigModel execute(ConfigModel cm) {
        final SimpleMailMessageConfigModel model = new SimpleMailMessageConfigModel(cm);
        if (cm.content() != null) {
            final Map<String, Object> data = JsonUtil.fromJsonToMap(cm.content(), String.class, Object.class);
            if (data != null) {
                model.setTls(TransformationUtil.objectToBoolean(data.get("tls")));
                model.setAuth(TransformationUtil.objectToBoolean(data.get("auth")));
                model.setFrom(TransformationUtil.objectToString(data.get("from")));
                model.setHost(TransformationUtil.objectToString(data.get("host")));
                model.setPort(TransformationUtil.objectToInteger(data.get("port")));
                model.setPassword(TransformationUtil.objectToString(data.get("password")));
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
     * SimpleMailMessageConfigModel
     */
    public static class SimpleMailMessageConfigModel implements MailMessageConfigModel, Serializable {

        /**
         * Port / Default Value 25
         */
        private int port = 25;

        /**
         * Host
         */
        private String host;

        /**
         * TLS / Default Value false
         */
        private boolean tls = false;

        /**
         * Auth / Default Value false
         */
        private boolean auth = false;

        /**
         * From
         */
        private String from;

        /**
         * Password
         */
        private String password;

        /**
         * Other Data
         */
        public Map<String, String> other = Collections.unmodifiableMap(new HashMap<>());

        /**
         * Source Config Model
         */
        private final ConfigModel source;

        /**
         * Construct Initialization
         * Inject Source Config Model Object
         *
         * @param source Source Config Model
         */
        public SimpleMailMessageConfigModel(ConfigModel source) {
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
        public MessageType type() {
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
        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public void setHost(String host) {
            this.host = host;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public void setAuth(boolean auth) {
            this.auth = auth;
        }

        @Override
        public boolean isAuth() {
            return auth;
        }

        @Override
        public void setTls(boolean tls) {
            this.tls = tls;
        }

        @Override
        public boolean isTls() {
            return tls;
        }

        @Override
        public void setFrom(String from) {
            this.from = from;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String getPassword() {
            return password;
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
