package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;
import club.p6e.coat.message.center.model.TemplateMessageModel;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
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
     * 客户端缓存对象
     */
    private static final Map<String, FirebaseApp> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileMessageLauncherServiceGoogleImpl.class);

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法注入
     *
     * @param threadPool 消息中心线程池对象
     */
    public MobileMessageLauncherServiceGoogleImpl(MessageCenterThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return "GOOGLE";
    }

    @Override
    public List<String> execute(List<String> recipients, TemplateMessageModel template, MobileMessageConfigModel config) {
        final int size = recipients.size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> rs = recipients.subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            threadPool.submit(() -> {
                try {
                    execute(getClient(config), rs, template);
                } catch (Exception e) {
                    LOGGER.error("GOOGLE MMS CONFIG ERROR >>> " + e.getMessage());
                }
            });
        }
        return recipients;
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
        FirebaseApp firebase = CLIENT_CACHE.get(name);
        if (firebase == null) {
            firebase = createClient(config);
            CLIENT_CACHE.put(name, firebase);
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
        final String content = config.content();
        final String name = config.getApplicationName();
        FirebaseApp firebase = CLIENT_CACHE.get(name);
        if (firebase == null) {
            try (final InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
                final FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(input))
                        .build();
                return FirebaseApp.initializeApp(options);
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
