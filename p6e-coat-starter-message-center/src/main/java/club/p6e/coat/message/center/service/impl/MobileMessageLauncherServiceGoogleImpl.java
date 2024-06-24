package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.service.LogService;
import club.p6e.coat.message.center.service.MobileMessageLauncherService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google 移动消息发射服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MobileMessageLauncherServiceGoogleImpl implements MobileMessageLauncherService {

    /**
     * 最多的收件人长度
     */
    public static final int MAX_RECIPIENT_LENGTH = 300;

    /**
     * 默认的模板解析器名称
     */
    private static final String GOOGLE_PARSER = "MOBILE_GOOGLE";

    /**
     * 缓存类型
     */
    private static final String CACHE_TYPE = "MOBILE_GOOGLE_CLIENT";

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileMessageLauncherServiceGoogleImpl.class);

    /**
     * 日志服务
     */
    private final LogService logService;

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法初始化
     *
     * @param logService 日志服务对象
     * @param threadPool 消息中心线程池对象
     */
    public MobileMessageLauncherServiceGoogleImpl(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return GOOGLE_PARSER;
    }

    @Override
    public Map<String, List<String>> execute(List<String> recipients, TemplateMessageModel template, MobileMessageConfigModel config) {
        final int size = recipients.size();
        final Map<String, List<String>> result = new HashMap<>(16);
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            final Map<String, List<String>> ls = logService.create(recipient, template);
            result.putAll(ls);
            threadPool.submit(() -> {
                try {
                    LOGGER.info("[ MOBILE GOOGLE MESSAGE ] >>> start send mobile google.");
                    LOGGER.info("[ MOBILE GOOGLE MESSAGE ] >>> recipient: {}", recipient);
                    LOGGER.info("[ MOBILE GOOGLE MESSAGE ] >>> template title: {}", template.getMessageTitle());
                    LOGGER.info("[ MOBILE GOOGLE MESSAGE ] >>> template content: {}", template.getMessageContent());
                    execute(getClient(config), recipient, template);
                    LOGGER.info("[ MOBILE GOOGLE MESSAGE ] >>> end send mobile google.");
                } catch (Exception e) {
                    LOGGER.error("[ MOBILE GOOGLE MESSAGE ERROR ] >>> {}", e.getMessage());
                } finally {
                    logService.update(ls, "SUCCESS");
                }
            });
        }
        return result;
    }

    /**
     * 获取 FirebaseApp 对象
     *
     * @param config 配置对象
     * @return FirebaseApp 对象
     * @throws Exception 异常对象
     */
    private FirebaseApp getClient(MobileMessageConfigModel config) throws Exception {
        final String name = config.getApplicationName();
        FirebaseApp firebase = ExpiredCache.get(CACHE_TYPE, name);
        if (firebase == null) {
            firebase = createClient(config);
        }
        return firebase;
    }

    /**
     * 创建 FirebaseApp 对象
     *
     * @param config 配置对象
     * @throws Exception 异常对象
     */
    private synchronized FirebaseApp createClient(MobileMessageConfigModel config) throws Exception {
        final String name = config.getApplicationName();
        final String content = config.content();
        FirebaseApp firebase = ExpiredCache.get(CACHE_TYPE, name);
        if (firebase == null) {
            try (final InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
                final FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(input))
                        .build();
                firebase = FirebaseApp.initializeApp(options);
                ExpiredCache.set(CACHE_TYPE, name, firebase);
            }
        }
        return firebase;
    }

    @SuppressWarnings("ALL")
    private void execute(FirebaseApp firebase, List<String> recipients, TemplateMessageModel template) {
        try {
            final MulticastMessage.Builder builder = MulticastMessage.builder();
            builder.addAllTokens(recipients);
            final Map<String, String> content = JsonUtil.fromJsonToMap(
                    template.getMessageContent(), String.class, String.class);
            if (content != null && content.get("fcm-options") != null) {
                builder.setFcmOptions(JsonUtil.fromJson(content.get("fcm-options"), FcmOptions.class));
            }
            if (content != null && content.get("notification") != null) {
                builder.setNotification(JsonUtil.fromJson(content.get("notification"), Notification.class));
            }
            if (content != null && content.get("android-config") != null) {
                builder.setAndroidConfig(JsonUtil.fromJson(content.get("android-config"), AndroidConfig.class));
            }
            if (content != null && content.get("data") != null) {
                builder.putAllData(JsonUtil.fromJsonToMap(content.get("data"), String.class, String.class));
            }
            final BatchResponse batchResponse = FirebaseMessaging.getInstance(firebase).sendEachForMulticast(builder.build());
            LOGGER.info("[ANDROID GOOGLE PUSH SUCCESS] >>> " + batchResponse.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            LOGGER.error("[ANDROID GOOGLE PUSH ERROR]", e);
            throw new RuntimeException(e);
        }
    }
}
