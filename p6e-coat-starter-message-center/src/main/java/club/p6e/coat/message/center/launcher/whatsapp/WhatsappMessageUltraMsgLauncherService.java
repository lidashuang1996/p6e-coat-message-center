package club.p6e.coat.message.center.launcher.whatsapp;

import club.p6e.coat.common.utils.HttpUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.whatsapp.WhatsappMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.log.LogService;
import lombok.Data;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * WhatsApp Message Ultra Msg Launcher Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WhatsappMessageUltraMsgLauncherService implements WhatsappMessageLauncherService {

    /**
     * HTTP Client
     */
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * Launcher Name
     */
    private static final String DEFAULT_LAUNCHER_NAME = "WHATSAPP_ULTRA_MSG";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsappMessageUltraMsgLauncherService.class);

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
    public WhatsappMessageUltraMsgLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return DEFAULT_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, WhatsappMessageConfigModel config) {
        threadPool.submit(() -> {
            try {
                LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> START SEND WHATSAPP");
                LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> WHATSAPP CONFIG: {}", JsonUtil.toJson(config));
                LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> WHATSAPP TEMPLATE: {}", ltm.getMessageTitle());
                LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> WHATSAPP TEMPLATE CONTENT: {}", ltm.getMessageContent());
                send(config, ltm);
            } finally {
                LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> END SEND WHATSAPP");
            }
        });
        return null;
    }

    /**
     * Send WhatsApp Message
     *
     * @param config   Config
     * @param template Template
     */
    public void send(WhatsappMessageConfigModel config, LauncherTemplateModel template) {
        final String chat = template.getChat();
        if (chat != null) {
            final List<Model> contents = JsonUtil.fromJsonToList(template.getMessageContent(), Model.class);
            LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> WHATSAPP CONTENT: {}", contents);
            for (final Model coi : contents) {
                if (coi != null) {
                    send(config, template, chat, coi);
                }
            }
        }
    }

    /**
     * Send WhatsApp Message
     *
     * @param config Config
     * @param chat   Chat
     * @param model  Model
     */
    public void send(WhatsappMessageConfigModel config, LauncherTemplateModel template, String chat, Model model) {
        if (config != null && chat != null && model != null) {
            String type = null;
            String url = config.getUrl();
            final String token = config.getToken();
            final String session = config.getSession();
            final String to = config.getChats().get(chat);
            final Map<String, String> params = new HashMap<>();
            if (model.getType() != null && "text".equalsIgnoreCase(model.getType())) {
                type = model.getType();
                url = url + "/" + session + "/messages/chat";
                params.put("to", to);
                params.put("token", token);
                params.put("body", model.getContent());
            } else if (model.getType() != null && "image".equalsIgnoreCase(model.getType())) {
                type = model.getType();
                url = url + "/" + session + "/messages/image";
                params.put("to", to);
                params.put("token", token);
                final List<File> files = template.getAttachment();
                final int fileIndex = Integer.parseInt(model.getPhoto());
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    if (fileIndex < files.size()) {
                        final byte[] buffer = new byte[1024];
                        final File file = files.get(fileIndex);
                        int read;
                        try (FileInputStream fis = new FileInputStream(file)) {
                            while ((read = fis.read(buffer)) != -1) {
                                os.write(buffer, 0, read);
                            }
                        }
                    }
                    params.put("image", Base64.getEncoder().encodeToString(os.toByteArray()));
                } catch (Exception e) {
                    LOGGER.error("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> BODY ERROR: {}/{} >>> {}", session, chat, e.getMessage(), e);
                }
                params.put("caption", model.getContent());
            } else if (model.getType() != null && "video".equalsIgnoreCase(model.getType())) {
                type = model.getType();
                url = url + "/" + session + "/messages/video";
                params.put("to", chat);
                params.put("token", token);
                params.put("video", model.getContent());
                params.put("caption", model.getContent());
            }
            if (type == null) {
                return;
            }
            LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> {} ::: {}/{} >>> {} ::: {}", url, token, session, chat, JsonUtil.toJson(params));
            final Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            final List<BasicNameValuePair> list = new ArrayList<>(
                    params.entrySet().stream().map((entry) ->
                            new BasicNameValuePair(entry.getKey(), entry.getValue())).toList());
            try {
                final String result = HttpUtil.doPost(HTTP_CLIENT, url, headers, new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
                LOGGER.info("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> RESULT: {}/{} >>> {}", session, chat, result);
            } catch (IOException e) {
                LOGGER.error("[ WHATSAPP ULTRA MSG LAUNCHER ] >>> SEND ERROR: {}/{} >>> {}", session, chat, e.getMessage(), e);
            }
        }
    }

    /**
     * Model
     */
    @Data
    public static class Model implements Serializable {
        /**
         * Type
         */
        private String type;

        /**
         * Photo
         */
        private String photo;

        /**
         * Video
         */
        private String video;

        /**
         * Content
         */
        private String content;
    }

}
