package club.p6e.coat.message.center.launcher.telegram;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.config.telegram.TelegramMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherStartingModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.launcher.mail.MailMessageDefaultLauncherService;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.log.LogService;
import club.p6e.coat.message.center.template.TemplateModel;
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
import java.util.ArrayList;
import java.util.HashMap;
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
    public MessageType type() {
        return null;
    }

    @Override
    public LauncherResultModel execute(LauncherStartingModel starting, TemplateModel template, TelegramMessageConfigModel config) {
        // build launcher template model object
        final LauncherTemplateModel ltm = LauncherTemplateModel.build(starting, template);
        threadPool.submit(() -> {
            try {
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> START SEND TELEGRAM.");
                LOGGER.info("[ TELEGRAM LAUNCHER ] >>> CLIENT: {}", JsonUtil.toJson(config));
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

    @SuppressWarnings("ALL")
    public void send(Session session, LauncherTemplateModel template) {
        final Map<String, String> param = template.getMessageParam();
        if (param != null && param.get("channel") != null) {
            final String channel = param.get("channel");
            final List<Map> data = JsonUtil.fromJsonToList(template.getMessageContent(), Map.class);
            if (data != null) {
                final List<Map<String, String>> list = new ArrayList<>();
                for (Map item : data) {
                    final Map<String, String> option = new HashMap<>();
                    list.add(option);
                    for (final Object key : item.keySet()) {
                        final Object value = item.get(key);
                        if (key != null && value != null) {
                            option.put(String.valueOf(key), String.valueOf(value));
                        }
                    }
                }
                for (Map<String, String> item : list) {
                    if (item != null && item.get("type") != null && item.get("content") != null) {
                        switch (item.get("type").toLowerCase()) {
                            case "text":
                                session.sendText(channel, item.get("content"));
                                break;
                            case "html":
                                session.sendHtml(channel, item.get("content"));
                                break;
                            case "photo":
                                File photo = null;
                                if (item.get("photo") != null && !item.get("photo").isEmpty()) {
                                    photo = template.getAttachment().get(Integer.parseInt(item.get("photo")));
                                }
                                session.sendPhoto(channel, photo, item.get("content"));
                                break;
                            case "video":
                                File thumb = null;
                                File video = null;
                                if (item.get("thumb") != null && !item.get("thumb").isEmpty()) {
                                    thumb = template.getAttachment().get(Integer.parseInt(item.get("thumb")));
                                }
                                if (item.get("video") != null && !item.get("video").isEmpty()) {
                                    video = template.getAttachment().get(Integer.parseInt(item.get("video")));
                                }
                                session.sendVideo(channel, thumb, video, item.get("content"));
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
         * @param channel Channel
         * @param content Content Data
         */
        public void sendText(String channel, String content) {
            try {
                final String id = config.getChats().get(channel);
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
         * @param channel Channel
         * @param content Content Data
         */
        public void sendHtml(String channel, String content) {
            try {
                final String id = config.getChats().get(channel);
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
         * @param channel Channel
         * @param photo   Photo File
         * @param content Content Data
         */
        public void sendPhoto(String channel, File photo, String content) {
            try {
                final String id = config.getChats().get(channel);
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
         * @param channel Channel
         * @param thumb   Thumb File
         * @param video   Video File
         * @param content Content Data
         */
        public void sendVideo(String channel, File thumb, File video, String content) {
            try {
                final String id = config.getChats().get(channel);
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
