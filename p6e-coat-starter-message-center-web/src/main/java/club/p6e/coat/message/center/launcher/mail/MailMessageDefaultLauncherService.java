package club.p6e.coat.message.center.launcher.mail;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.mail.MailMessageConfigModel;
import club.p6e.coat.message.center.error.MessageCenterConfigException;
import club.p6e.coat.message.center.error.MessageCenterFileException;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * MailMessageDefaultLauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MailMessageDefaultLauncherService implements MailMessageLauncherService {

    /**
     * Maximum recipient length
     */
    public static int MAX_RECIPIENT_LENGTH = 20;

    /**
     * Cache Type
     */
    private static final String CACHE_TYPE = "MAIL_CLIENT";

    /**
     * Launcher Name
     */
    private static final String DEFAULT_LAUNCHER_NAME = "MAIL_DEFAULT_LAUNCHER";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailMessageDefaultLauncherService.class);

    /**
     * Log Service
     */
    protected final LogService logService;

    /**
     * Message Center Thread Pool Object
     */
    protected final MessageCenterThreadPool threadPool;

    /**
     * Construct Initialization
     *
     * @param logService Log Service
     * @param threadPool Thread Pool Object
     */
    public MailMessageDefaultLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, MailMessageConfigModel config) {
        // validate config object
        validate(config);
        // segmentation send mail task
        final int size = ltm.getRecipients().size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = ltm.getRecipients().subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            // submit email sending tasks in the thread pool
            threadPool.submit(() -> {
                try {
                    LOGGER.info("[ MAIL LAUNCHER ] >>> START SEND MAIL.");
                    LOGGER.info("[ MAIL LAUNCHER ] >>> MAIL FROM: {}", config.getFrom());
                    LOGGER.info("[ MAIL LAUNCHER ] >>> MAIL RECIPIENTS: {}", recipient);
                    LOGGER.info("[ MAIL LAUNCHER ] >>> MAIL CLIENT: {}", JsonUtil.toJson(config));
                    LOGGER.info("[ MAIL LAUNCHER ] >>> MAIL TEMPLATE TITLE: {}", ltm.getMessageTitle());
                    LOGGER.info("[ MAIL LAUNCHER ] >>> MAIL TEMPLATE CONTENT: {}", ltm.getMessageContent());
                    // execute the operation of sending emails
                    send(client(config), config.getFrom(), recipient, ltm);
                } finally {
                    LOGGER.info("[ MAIL LAUNCHER ] >>> END SEND MAIL.");
                }
            });
        }
        return () -> 0;
    }

    /**
     * Validate Config Model
     *
     * @param config Config Model
     */
    protected void validate(MailMessageConfigModel config) {
        if (config == null) {
            throw new MessageCenterConfigException(
                    this.getClass(),
                    "fun validateConfig(MailMessageConfigModel config).",
                    "validate config [MailMessageConfigModel] value is null."
            );
        }
        if (config.getHost() == null) {
            throw new MessageCenterConfigException(
                    this.getClass(),
                    "fun validateConfig(MailMessageConfigModel config).",
                    "validate config [MailMessageConfigModel] host value is null."
            );
        }
        if (config.getFrom() == null) {
            throw new MessageCenterConfigException(
                    this.getClass(),
                    "fun validateConfig(MailMessageConfigModel config).",
                    "validate config [MailMessageConfigModel] from value is null."
            );
        }
        if (config.getPassword() == null) {
            throw new MessageCenterConfigException(
                    this.getClass(),
                    "fun validateConfig(MailMessageConfigModel config).",
                    "validate config [MailMessageConfigModel] password value is null."
            );
        }
    }

    /**
     * Get Client Session
     *
     * @param config Client Config
     * @return Client Session
     */
    protected Session client(MailMessageConfigModel config) {
        final String name = Md5Util.execute(Md5Util.execute(config.getHost() + ":"
                + config.getPort() + "@" + config.getFrom() + "_" + config.getPassword()));
        Session session = ExpiredCache.get(CACHE_TYPE, name);
        if (session == null) {
            session = client(name, config);
        }
        return session;
    }

    /**
     * Create Client Session
     *
     * @param name   Client Name
     * @param config Client Config
     * @return Client Session
     */
    protected synchronized Session client(String name, MailMessageConfigModel config) {
        Session session = ExpiredCache.get(CACHE_TYPE, name);
        if (session == null) {
            final java.util.Properties properties = new java.util.Properties();
            properties.put("mail.smtp.auth", config.isAuth());
            properties.put("mail.smtp.port", config.getPort());
            properties.put("mail.smtp.ssl.enable", config.isTls());
            if (config.getHost() != null) {
                properties.put("mail.smtp.host", config.getHost());
            }
            if (config.getFrom() != null) {
                properties.put("mail.smtp.from", config.getFrom());
            }
            if (config.getPassword() != null) {
                properties.put("mail.smtp.password", config.getPassword());
            }
            if (config.getOther() != null) {
                for (final String key : config.getOther().keySet()) {
                    if (key.startsWith("@")) {
                        properties.put(key.substring(1), config.getOther().get(key));
                    }
                }
            }
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
     * Send Mail Message
     *
     * @param form       Form Address
     * @param recipients Recipients
     * @param session    Client Session
     * @param ltm        Launcher Template Model
     */
    protected void send(Session session, String form, List<String> recipients, LauncherTemplateModel ltm) {
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
                message.setSubject(ltm.getMessageTitle());
                message.setFrom(new InternetAddress(form));
                final MimeMultipart multipart = new MimeMultipart();
                final MimeBodyPart htmlBodyPart = new MimeBodyPart();
                htmlBodyPart.setContent(ltm.getMessageContent(), "text/html; charset=utf-8");
                multipart.addBodyPart(htmlBodyPart);
                if (ltm.getAttachment() != null
                        && !ltm.getAttachment().isEmpty()) {
                    for (final File file : ltm.getAttachment()) {
                        try {
                            if (!FileUtil.checkFileExist(file)) {
                                throw new MessageCenterFileException(
                                        this.getClass(),
                                        "fun send(Session session, String from, " +
                                                "List<String> recipients, LauncherTemplateModel template).",
                                        "reading the attachment file " +
                                                "[" + file + "], it was found that the file does not exist."
                                );
                            }
                            if (file.getName().toLowerCase().startsWith("embedded-")) {
                                final MimeBodyPart embeddedBodyPart = new MimeBodyPart();
                                embeddedBodyPart.setHeader("Content-ID", FileUtil.getName(file.getName()));
                                embeddedBodyPart.setDataHandler(new DataHandler(new FileDataSource(file)));
                                multipart.addBodyPart(embeddedBodyPart);
                            } else {
                                final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                                attachmentBodyPart.attachFile(file);
                                attachmentBodyPart.setFileName(file.getName());
                                multipart.addBodyPart(attachmentBodyPart);
                            }
                        } catch (Exception e) {
                            LOGGER.error("[ MAIL LAUNCHER ] ATTACHMENT ERROR >>> {}", e.getMessage());
                        }
                    }
                }
                message.setContent(multipart);
                Transport.send(message);
            }
        } catch (Exception e) {
            LOGGER.error("[ MAIL LAUNCHER ] ERROR >>> {}", e.getMessage());
        }
    }

}
