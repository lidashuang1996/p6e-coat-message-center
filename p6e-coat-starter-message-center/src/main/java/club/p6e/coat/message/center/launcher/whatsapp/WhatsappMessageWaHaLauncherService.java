package club.p6e.coat.message.center.launcher.whatsapp;

import club.p6e.coat.common.utils.HttpUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.whatsapp.WhatsappMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.log.LogService;
import lombok.Data;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WhatsApp Message Wa Ha Launcher Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WhatsappMessageWaHaLauncherService implements WhatsappMessageLauncherService {

    /**
     * Http Client Object
     */
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * Launcher Name
     */
    private static final String DEFAULT_LAUNCHER_NAME = "WHATSAPP_WA_HA";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsappMessageWaHaLauncherService.class);

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
    public WhatsappMessageWaHaLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
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
                LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> START SEND WHATSAPP");
                LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> WHATSAPP CONFIG: {}", JsonUtil.toJson(config));
                LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> WHATSAPP TEMPLATE: {}", ltm.getMessageTitle());
                LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> WHATSAPP TEMPLATE CONTENT: {}", ltm.getMessageContent());
                send(config, ltm);
            } finally {
                LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> END SEND WHATSAPP");
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
            LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> WHATSAPP CONTENT: {}", contents);
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
            final Map<String, Object> params = new HashMap<>();
            final String token = config.getToken();
            final String session = config.getSession();
            final String chatId = config.getChats().get(chat);
            params.put("chatId", chatId);
            params.put("session", session);
            if (model.getType() != null && "text".equalsIgnoreCase(model.getType())) {
                type = model.getType();
                url = url + "/api/sendText";
                params.put("text", model.getContent());
            } else if (model.getType() != null && "image".equalsIgnoreCase(model.getType())) {
                type = model.getType();
                url = url + "/api/sendImage";
                if (model.getPhoto() != null) {
                    params.put("file", Map.of(
                            "data", "@{FILE_INDEX_" + model.getPhoto() + "}",
                            "filename", "image.jpg",
                            "mimetype", "image/jpeg"
                    ));
                }
                params.put("caption", model.getContent());
            } else if (model.getType() != null && "video".equalsIgnoreCase(model.getType())) {
                type = model.getType();
                url = url + "/api/sendVideo";
                if (model.getVideo() != null) {
                    params.put("file", Map.of(
                            "data", "@{FILE_INDEX_" + model.getVideo() + "}",
                            "filename", "video.mp4",
                            "mimetype", "video/mp4"
                    ));
                }
                params.put("caption", model.getContent());
            }
            if (type == null) {
                return;
            }
            LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> {} ::: {}/{} >>> {} ::: {}", url, token, session, chat, JsonUtil.toJson(params));
            try {
                final Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", token);
                headers.put("Content-Type", "application/json;charset=UTF-8");
                HttpPost httpPost = new HttpPost();
                httpPost.setURI(URI.create(url));
                httpPost.setEntity(new BufferedHttpEntity(new ByteArrayEntity(write(JsonUtil.toJson(params), template.getAttachment()))));
                for (String key : headers.keySet()) {
                    httpPost.setHeader(key, headers.get(key));
                }
                HttpUtil.doPost(HTTP_CLIENT, httpPost, (ResponseHandler<Boolean>) response -> {
                    final int code = response.getStatusLine().getStatusCode();
                    if (HttpStatus.SC_OK == code || HttpStatus.SC_CREATED == code) {
                        LOGGER.info("[ WHATSAPP WA HA LAUNCHER ] >>> RESULT: {}/{} >>> {}",
                                session, chat, EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
                    }
                    return true;
                });
            } catch (Exception e) {
                LOGGER.error("[ WHATSAPP WA HA LAUNCHER ] >>> ERROR: {}/{} >>> {}", session, chat, e.getMessage(), e);
            }
        }
    }

    public byte[] write(String content, List<File> files) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try {
                if (content.contains("@{FILE_INDEX_")) {
                    int index = 0;
                    while (true) {
                        final int newIndex = content.indexOf("@{FILE_INDEX_", index);
                        if (newIndex == -1) {
                            break;
                        }
                        os.write(content.substring(index, newIndex).getBytes(StandardCharsets.UTF_8));
                        index = newIndex + 13;
                        boolean fileIndexStatus = false;
                        final StringBuilder fileIndexStringBuffer = new StringBuilder();
                        for (int i = index; i < content.length(); i++) {
                            final char c = content.charAt(i);
                            if (c == '}') {
                                index = i + 1;
                                fileIndexStatus = true;
                                break;
                            } else {
                                fileIndexStringBuffer.append(c);
                            }
                        }
                        if (fileIndexStatus) {
                            try {
                                write(files.get(Integer.parseInt(fileIndexStringBuffer.toString())), os);
                            } catch (Exception e) {
                                LOGGER.error("WHATSAPP WA HA WRITE TO FILE STREAM ERROR: {}", e.getMessage(), e);
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    os.write(content.substring(index).getBytes(StandardCharsets.UTF_8));
                } else {
                    os.write(content.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception ee) {
                LOGGER.error("WHATSAPP WA HA WRITE TO OUTPUT STREAM ERROR: {}", ee.getMessage(), ee);
            }
            return os.toByteArray();
        } catch (Exception e) {
            LOGGER.error("[ WHATSAPP WA HA LAUNCHER ] >>> ERROR: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    public void write(File file, OutputStream os) {
        try (OutputStream base64OutputStream = Base64.getEncoder().wrap(os)) {
            int read;
            final byte[] buffer = new byte[1024];
            try (final FileInputStream fis = new FileInputStream(file)) {
                while ((read = fis.read(buffer)) != -1) {
                    base64OutputStream.write(buffer, 0, read);
                }
            }
            base64OutputStream.flush();
        } catch (IOException e) {
            LOGGER.error("[ WHATSAPP WA HA LAUNCHER ] BASE 64 ENCODE STRING/FILE ERROR: {}", e.getMessage(), e);
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
