package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.service.MailMessageLauncherService;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MailMessageLauncherServiceImpl implements MailMessageLauncherService {

    @Data
    @AllArgsConstructor
    private static class CacheModel {
        private String name;
        private long date;
        private Session session;
    }

    /**
     * 缓存时间
     */
    public static final long CACHE_TIME = 1000 * 60 * 60 * 24;

    public static final int MAX_RECIPIENT_LENGTH = 30;

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailMessageLauncherServiceImpl.class);

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 缓存模型对象
     */
    private final Map<String, CacheModel> cache = new LinkedHashMap<>();

    /**
     * 构造方法初始化
     *
     * @param threadPool 消息中心线程池对象
     */
    public MailMessageLauncherServiceImpl(MessageCenterThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    private Session getSession(String from, String password, java.util.Properties properties) {
        final String key = from + "_" + password;
        final long now = System.currentTimeMillis();
        CacheModel model = cache.get(key);
        if (model == null || model.getDate() + CACHE_TIME < now) {
            model = new CacheModel(from, now, Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            }));
            cache.put(key, model);
        }
        return model.getSession() == null ? null : model.getSession();
    }

    @Override
    public String name() {
        return "DEFAULT";
    }

    @Override
    public List<String> execute(List<String> recipients, TemplateMessageModel template, MailMessageConfigModel config) {
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
        String from;
        String password;
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
            from = config.getFrom();
            properties.put("mail.smtp.from", config.getFrom());
        }
        if (config.getPassword() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the password in config data is null");
        } else {
            password = config.getPassword();
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
        final Session session = getSession(from, password, properties);
        final int size = recipients.size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> rs = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            threadPool.submit(() -> send(session, from, rs, template));
        }
        return null;
    }


    /**
     * 发送邮件
     *
     * @param session    会话对象
     * @param from       发件人
     * @param recipients 收件人
     * @param template   模板对象
     */
    private void send(Session session, String from, List<String> recipients, TemplateMessageModel template) {
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
                message.setFrom(new InternetAddress(from));
                message.setSubject(template.title());
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
                            LOGGER.error("[MAIL MESSAGE ATTACHMENT]", e);
                        }
                    }
                }
                message.setContent(multipart);
                Transport.send(message);
            }
        } catch (MessagingException e) {
            LOGGER.error("[MAIL MESSAGE]", e);
        }
    }

}
