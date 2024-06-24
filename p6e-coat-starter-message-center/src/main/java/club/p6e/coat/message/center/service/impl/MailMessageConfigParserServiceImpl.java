package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.service.MailMessageConfigParserService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 源配置对象转邮件消息配置对象解析器
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MailMessageConfigParserServiceImpl implements MailMessageConfigParserService {

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "MAIL_DEFAULT";

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public MailMessageConfigModel execute(ConfigModel config) {
        return new SimpleMailMessageConfigModel(config) {{
            if (config.content() != null) {
                final Map<String, String> data = JsonUtil.fromJsonToMap(config.content(), String.class, String.class);
                if (data != null) {
                    setHost(data.get("host"));
                    setFrom(data.get("from"));
                    setPassword(data.get("password"));
                    setPort(Integer.parseInt(data.get("port")));
                    setTls(Boolean.parseBoolean(data.get("tls")));
                    setAuth(Boolean.parseBoolean(data.get("auth")));
                    setOther(data);
                }
            }
        }};
    }

    /**
     * 简单邮件消息配置模型
     */
    public static class SimpleMailMessageConfigModel implements MailMessageConfigModel, Serializable {
        /**
         * 端口
         */
        private int port = 25;

        /**
         * 邮件服务器地址
         */
        private String host;

        /**
         * 是否开启认证
         */
        private boolean auth = false;

        /**
         * 是否开启 TLS
         */
        private boolean tls = false;

        /**
         * 发送者
         */
        private String from;

        /**
         * 密码
         */
        private String password;

        /**
         * 其他配置
         */
        public Map<String, String> other = new HashMap<>();

        /**
         * 源配置对象
         */
        private final ConfigModel model;

        /**
         * 构造方法注入源配置对象
         *
         * @param model 配置对象
         */
        public SimpleMailMessageConfigModel(ConfigModel model) {
            this.model = model;
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
            this.other = other;
        }

        @Override
        public Map<String, String> getOther() {
            return other;
        }

    }

}
