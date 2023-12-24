package club.p6e.coat.message.center.launcher.impl;

import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.Properties;
import club.p6e.coat.message.center.config.MailMessageConfigModel;
import club.p6e.coat.message.center.template.CommunicationTemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MailMessageLauncherServiceImpl implements MailMessageLauncherService {

    @Override
    public List<String> execute(List<String> recipients, CommunicationTemplateModel template, MailMessageConfigModel config) {
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
        if (template.getCommunicationTitle() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the title in template data is null");
        }
        if (template.getCommunicationContent() == null) {
            throw new NullPointerException(
                    "when performing the send email operation, it was found that the content in template data is null");
        }
        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });





        final int size = recipients.size();
        final int stride = this.properties.getMail().getMaxRecipientLength();
        for (int i = 0; i < size; i = i + stride) {
            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
            threadPool.submit(() -> send(session, config.from(), rs, template));
        }


        return null;
    }

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailMessageLauncherServiceImpl.class);

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     * @param threadPool 消息中心线程池对象
     */
    public MailMessageLauncherServiceImpl(Properties properties, MessageCenterThreadPool threadPool) {
        this.properties = properties;
        this.threadPool = threadPool;
    }

    /**
     * 发送邮件
     *
     * @param session    会话对象
     * @param from       发件人
     * @param recipients 收件人
     * @param template   模板对象
     */
    private void send(Session session, String from, List<String> recipients, TemplateModel template) {
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
                if (template.attachments() != null
                        && !template.attachments().isEmpty()) {
                    for (int i = 0; i < template.attachments().size(); i++) {
                        try {
                            final String attachment = template.attachments().get(i);
                            final String path = FileUtil.convertAbsolutePath(FileUtil.composePath(
                                    properties.getMail().getAttachment().getPath(),
                                    attachment
                            ));
                            final File file = new File(path);
                            if (!FileUtil.checkFileExist(file)) {
                                throw new FileNotFoundException(
                                        "when performing an email sending operation, reading the attachment "
                                                + "file [" + file + "], it was found that the file does not exist");
                            }
                            final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                            attachmentBodyPart.attachFile(file);
                            attachmentBodyPart.setFileName(FileUtil.composeFile(
                                    "attachment" + (i == 0 ? "" : ("_" + i)),
                                    FileUtil.getSuffix(attachment)
                            ));
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
