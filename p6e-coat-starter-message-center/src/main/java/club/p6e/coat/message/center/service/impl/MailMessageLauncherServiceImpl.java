package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.service.LogService;
import club.p6e.coat.message.center.service.MailMessageLauncherService;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MailMessageLauncherServiceImpl implements MailMessageLauncherService {

    /**
     * 最大收件人长度
     */
    public static int MAX_RECIPIENT_LENGTH = 20;

    /**
     * 缓存类型
     */
    protected static final String CACHE_TYPE = "MAIL_CLIENT";

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "DEFAULT";

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailMessageLauncherServiceImpl.class);

    /**
     * 日志服务
     */
    protected final LogService logService;

    /**
     * 消息中心线程池对象
     */
    protected final MessageCenterThreadPool threadPool;

    /**
     * 构造方法初始化
     *
     * @param threadPool 消息中心线程池对象
     */
    public MailMessageLauncherServiceImpl(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, MailMessageConfigModel config) {
        if (config == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found config data is null");
        }
        if (template == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found template data is null");
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found recipients is null");
        }
        final java.util.Properties properties = new java.util.Properties();
        properties.put("mail.smtp.auth", config.isAuth());
        properties.put("mail.smtp.port", config.getPort());
        properties.put("mail.smtp.ssl.enable", config.isTls());
        if (config.getHost() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the host in config data is null");
        } else {
            properties.put("mail.smtp.host", config.getHost());
        }
        if (config.getFrom() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the from in config data is null");
        } else {
            properties.put("mail.smtp.from", config.getFrom());
        }
        if (config.getPassword() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the password in config data is null");
        }
        if (config.getOther() != null) {
            for (final String key : config.getOther().keySet()) {
                if (key.startsWith("@")) {
                    properties.put(key.substring(1), config.getOther().get(key));
                }
            }
        }
        if (template.getMessageTitle() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the title in template data is null");
        }
        if (template.getMessageContent() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the content in template data is null");
        }
        final int size = recipients.size();
        final Map<String, List<String>> result = new HashMap<>(16);
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            final Map<String, List<String>> logData = logService.create(recipient, template);
            result.putAll(logData);
            threadPool.submit(() -> {
                try {
                    send(getClient(config, properties), config.getFrom(), recipient, template);
                } catch (Exception e) {
                    // ignore
                } finally {
                    logService.update(logData, "SUCCESS");
                }
            });
        }
        return result;
    }


    protected Session getClient(MailMessageConfigModel config, java.util.Properties properties) {
        final String name = Md5Util.execute(config.getHost() + ":"
                + config.getPort() + "@" + config.getFrom() + "_" + config.getPassword());
        Session session = ExpiredCache.get(CACHE_TYPE, name);
        if (session == null) {
            session = createClient(name, config, properties);
        }
        return session;
    }

    protected synchronized Session createClient(String name, MailMessageConfigModel config, java.util.Properties properties) {
        Session session = ExpiredCache.get(CACHE_TYPE, name);
        if (session == null) {
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getFrom(), config.getPassword());
                }
            });
            ExpiredCache.set(CACHE_TYPE, name, session);
        }
        return session;
    }


    /**
     * 发送邮件
     *
     * @param session    会话对象
     * @param from       发件人
     * @param recipients 收件人
     * @param template   模板对象
     */
    protected void send(Session session, String from, List<String> recipients, TemplateMessageModel template) {
        try {
            if (recipients != null && !recipients.isEmpty()) {
                final List<InternetAddress> list = new ArrayList<>();
                for (final String recipient : recipients) {
                    list.add(new InternetAddress(recipient));
                }
                final MimeMessage message = new MimeMessage(session);
                message.addRecipients(
                        Message.RecipientType.BCC,
                        list.toArray(new InternetAddress[0])
                );
                message.setSubject(template.title());
                message.setFrom(new InternetAddress(from));
                final MimeMultipart multipart = new MimeMultipart();
                final MimeBodyPart htmlBodyPart = new MimeBodyPart();
                htmlBodyPart.setContent(template.content(), "text/html; charset=utf-8");
                multipart.addBodyPart(htmlBodyPart);
                if (template.getAttachment() != null
                        && !template.getAttachment().isEmpty()) {
                    for (int i = 0; i < template.getAttachment().size(); i++) {
                        try {
                            final File file = template.getAttachment().get(i);
                            if (!FileUtil.checkFileExist(file)) {
                                throw new FileNotFoundException(
                                        "when performing an email sending operation, reading the attachment "
                                                + "file [" + file + "], it was found that the file does not exist");
                            }
                            final String suffix = FileUtil.getSuffix(file.getName());
                            final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                            attachmentBodyPart.attachFile(file);
                            attachmentBodyPart.setFileName(FileUtil.composeFile(
                                    "attachment" + (i == 0 ? "" : ("_" + i)), suffix));
                            multipart.addBodyPart(attachmentBodyPart);
                        } catch (Exception e) {
                            LOGGER.error("[ MAIL MESSAGE ATTACHMENT ERROR ]", e);
                        }
                    }
                }
                message.setContent(multipart);
                Transport.send(message);
            }
        } catch (Exception e) {
            LOGGER.error("[ MAIL MESSAGE SEND ERROR ]", e);
        }
    }

}
