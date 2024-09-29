package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.TelegramMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.service.LogService;
import club.p6e.coat.message.center.service.TelegramMessageLauncherService;
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
 * @author lidashuang
 * @version 1.0
 */
@Component
public class TelegramMessageLauncherServiceImpl implements TelegramMessageLauncherService {

    /**
     * 缓存类型
     */
    protected static final String CACHE_TYPE = "TELEGRAM_CLIENT";

    /**
     * 默认的模板解析器名称
     */
    private static final String DEFAULT_PARSER = "TELEGRAM_DEFAULT";

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
     * @param logService 日志服务对象
     * @param threadPool 消息中心线程池对象
     */
    public TelegramMessageLauncherServiceImpl(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramMessageLauncherServiceImpl.class);

    @Override
    public String name() {
        return DEFAULT_PARSER;
    }

    @Override
    public Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, TelegramMessageConfigModel config) {
        threadPool.submit(() -> {
            try {
                LOGGER.info("[ TELEGRAM MESSAGE ] >>> start send message.");
                LOGGER.info("[ TELEGRAM MESSAGE ] >>> config: {}", JsonUtil.toJson(config));
                send(get(config), template);
                LOGGER.info("[ TELEGRAM MESSAGE ] >>> end send message.");
            } finally {
                //logService.update(logData, "SUCCESS");
            }
        });
        return Map.of();
    }

    @SuppressWarnings("ALL")
    public void send(Session session, TemplateMessageModel template) {
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

    public synchronized Session get(TelegramMessageConfigModel config) {
        final String name = Md5Util.execute(config.getBotToken());
        Session session = ExpiredCache.get(CACHE_TYPE, name);
        if (session == null) {
            session = create(name, config);
        }
        return session;
    }

    public synchronized Session create(String name, TelegramMessageConfigModel config) {
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
                throw new RuntimeException(e);
            }
        }
        return session;
    }

    public static class Session extends TelegramLongPollingBot {

        private final TelegramMessageConfigModel config;

        public Session(TelegramMessageConfigModel config) {
            this.config = config;
        }

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
            final String text = update.getMessage().getText();
            final long id = update.getMessage().getChat().getId();
            final Map<String, String> chats = config.getChats();
            if (chats != null && !chats.isEmpty()) {
                for (final String key : chats.keySet()) {
                    final String value = chats.get(key);
                    if (value.startsWith("@") && value.equals(text)) {
                        final String content = config.content().replaceAll(text, String.valueOf(id));
                        SpringUtil.getBean(DataSourceRepository.class).updateConfigContent(config.id(), content);
                    }
                }
            }
        }

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
                throw new RuntimeException(e);
            }
        }

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
                throw new RuntimeException(e);
            }
        }

    }

}
