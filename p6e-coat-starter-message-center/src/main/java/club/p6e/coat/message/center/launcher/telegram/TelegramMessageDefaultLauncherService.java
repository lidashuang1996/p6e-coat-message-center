package club.p6e.coat.message.center.launcher.telegram;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.telegram.TelegramMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.log.LogService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * TelegramMessageDefaultLauncherService
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class TelegramMessageDefaultLauncherService implements TelegramMessageLauncherService {

    /**
     * Cache Type
     */
    protected static final String CACHE_TYPE = "TELEGRAM_CLIENT";

    /**
     * Launcher Name
     */
    private static final String DEFAULT_LAUNCHER_NAME = "TELEGRAM_DEFAULT_LAUNCHER";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramMessageDefaultLauncherService.class);

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
    public TelegramMessageDefaultLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, TelegramMessageConfigModel config) {
        threadPool.submit(() -> {
            try {
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> START SEND TELEGRAM.");
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> TELEGRAM CLIENT: {}", JsonUtil.toJson(config));
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> TELEGRAM TEMPLATE: {}", ltm.getMessageTitle());
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> TELEGRAM TEMPLATE CONTENT: {}", ltm.getMessageContent());
                // execute the operation of sending telegram
                send(client(config), ltm);
            } finally {
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> END SEND TELEGRAM.");
            }
        });
        return () -> 0;
    }

    /**
     * Send Telegram Message
     *
     * @param session  Session
     * @param template Template
     */
    public void send(Session session, LauncherTemplateModel template) {
        final String chat = template.getChat();
        if (chat != null) {
            final List<Model> list = JsonUtil.fromJsonToList(template.getMessageContent(), Model.class);
            if (list != null) {
                for (final Model item : list) {
                    if (item != null && item.getType() != null && item.getContent() != null) {
                        switch (item.getType().toLowerCase()) {
                            case "text":
                                session.sendText(chat, item.getContent());
                                break;
                            case "html":
                                session.sendHtml(chat, item.getContent());
                                break;
                            case "photo":
                                File photo = null;
                                if (item.getPhoto() != null && !item.getPhoto().isEmpty()) {
                                    photo = template.getAttachment().get(Integer.parseInt(item.getPhoto()));
                                }
                                session.sendPhoto(chat, photo, item.getContent());
                                break;
                            case "video":
                                File thumb = null;
                                File video = null;
                                if (item.getThumb() != null && !item.getThumb().isEmpty()) {
                                    thumb = template.getAttachment().get(Integer.parseInt(item.getThumb()));
                                }
                                if (item.getVideo() != null && !item.getVideo().isEmpty()) {
                                    video = template.getAttachment().get(Integer.parseInt(item.getVideo()));
                                }
                                session.sendVideo(chat, thumb, video, item.getContent());
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Get Client Session
     *
     * @param config Client Config
     * @return Client Session
     */
    public Session client(TelegramMessageConfigModel config) {
        final String name = Md5Util.execute(Md5Util.execute(config.getBotToken()));
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
    public Session client(String name, TelegramMessageConfigModel config) {
        Session session = ExpiredCache.get(CACHE_TYPE, name);
        if (session == null) {
            try {
                final TelegramBotsApi telegram = new TelegramBotsApi(DefaultBotSession.class);
                if (config.isProxy()) {
                    final DefaultBotOptions options = new DefaultBotOptions();
                    options.setProxyHost(config.getProxyHost());
                    options.setProxyPort(config.getProxyPort());
                    options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
                    session = new Session(options, config);
                } else {
                    session = new Session(config);
                }
                telegram.registerBot(session);
                ExpiredCache.set(CACHE_TYPE, name, session);
            } catch (Exception e) {
                LOGGER.error("[ TELEGRAM LAUNCHER ] CLIENT ERROR >>> {}", e.getMessage());
            }
        }
        return session;
    }

    @Data
    public static class Model implements Serializable {
        private String type;
        private String photo;
        private String thumb;
        private String video;
        private String content;
    }

    /**
     * TelegramLongPollingBot ->> Session
     */
    public static class Session extends TelegramLongPollingBot {

        /**
         * Telegram Message Config Model
         */
        private final TelegramMessageConfigModel config;

        /**
         * Construct Initialization
         *
         * @param config Telegram Message Config Model
         */
        public Session(TelegramMessageConfigModel config) {
            this.config = config;
        }

        /**
         * Construct Initialization
         *
         * @param options Default Bot Options
         * @param config  Telegram Message Config Model
         */
        public Session(DefaultBotOptions options, TelegramMessageConfigModel config) {
            super(options);
            this.config = config;
        }

        @Override
        public String getBotToken() {
            return config.getBotToken();
        }

        @Override
        public String getBotUsername() {
            return config.getBotUsername();
        }

        @Override
        public void onUpdateReceived(Update update) {
            String id = null;
            String text = null;
            if (update.getMessage() != null) {
                text = update.getMessage().getText();
                id = String.valueOf(update.getMessage().getChat().getId());
            }
            if (update.getChannelPost() != null) {
                text = update.getChannelPost().getText();
                id = String.valueOf(update.getChannelPost().getChat().getId());
            }
            if (id != null && text != null) {
                final Map<String, String> chats = config.getChats();
                if (chats != null && !chats.isEmpty()) {
                    for (final String key : chats.keySet()) {
                        final String value = chats.get(key);
                        // write channel ID through message reverse injection
                        if (value.startsWith("@") && (key + value).equals(text)) {
                            // refresh cache chats data
                            chats.put(key, id);
                            // replace config chats data
                            final String content = config.content().replaceAll(value, id);
                            // database update chats config data
                            SpringUtil.getBean(DataSourceRepository.class).updateConfigContent(config.id(), content);
                            try {
                                // return success message
                                execute(SendMessage.builder().chatId(id).text("[ " + key + value + "] CONFIG SUCCESS").build());
                            } catch (Exception e) {
                                LOGGER.error("[ TELEGRAM LAUNCHER ] CHAT_CHANNEL ERROR >>> {}", e.getMessage());
                            }
                            break;
                        }
                    }
                }
            }
        }

        /**
         * Send Text Message
         *
         * @param chat    Chat
         * @param content Content Data
         */
        public void sendText(String chat, String content) {
            try {
                final String id = config.getChats().get(chat);
                if (id != null) {
                    execute(SendMessage.builder().chatId(id).text(content).build());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Send Html Message
         *
         * @param chat    Chat
         * @param content Content Data
         */
        public void sendHtml(String chat, String content) {
            try {
                final String id = config.getChats().get(chat);
                if (id != null) {
                    execute(SendMessage.builder().chatId(id).text(content).parseMode(ParseMode.HTML).build());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Send Photo Message
         *
         * @param chat    Chat
         * @param photo   Photo File
         * @param content Content Data
         */
        public void sendPhoto(String chat, File photo, String content) {
            try {
                final String id = config.getChats().get(chat);
                if (id != null) {
                    final SendPhoto.SendPhotoBuilder builder = SendPhoto.builder().chatId(id);
                    if (content != null && !content.isEmpty()) {
                        builder.caption(content);
                    }
                    execute(builder.photo(new InputFile(photo)).build());
                }
            } catch (Exception e) {
                LOGGER.error("[ TELEGRAM LAUNCHER ] SEND PHOTO ERROR >>> {}", e.getMessage());
            }
        }

        /**
         * Send Video Message
         *
         * @param chat    Chat
         * @param thumb   Thumb File
         * @param video   Video File
         * @param content Content Data
         */
        public void sendVideo(String chat, File thumb, File video, String content) {
            try {
                final String id = config.getChats().get(chat);
                if (id != null) {
                    final SendVideo.SendVideoBuilder builder = SendVideo.builder().chatId(id);
                    if (thumb != null) {
                        builder.thumb(new InputFile(thumb));
                    }
                    if (content != null && !content.isEmpty()) {
                        builder.caption(content);
                    }
                    execute(builder.video(new InputFile(video)).build());
                }
            } catch (Exception e) {
                LOGGER.error("[ TELEGRAM LAUNCHER ] SEND VIDEO ERROR >>> {}", e.getMessage());
            }
        }

    }

}
