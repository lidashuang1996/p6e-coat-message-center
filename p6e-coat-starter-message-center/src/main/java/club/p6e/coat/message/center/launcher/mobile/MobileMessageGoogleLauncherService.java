package club.p6e.coat.message.center.launcher.mobile;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.Md5Util;
import club.p6e.coat.message.center.ExpiredCache;
import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.mobile.MobileMessageConfigModel;
import club.p6e.coat.message.center.launcher.LauncherResultModel;
import club.p6e.coat.message.center.launcher.LauncherTemplateModel;
import club.p6e.coat.message.center.log.LogService;
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

/**
 * Google 移动消息发射服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MobileMessageGoogleLauncherService implements MobileMessageLauncherService {

    /**
     * Maximum recipient length
     */
    public static final int MAX_RECIPIENT_LENGTH = 300;

    /**
     * Cache Type
     */
    private static final String CACHE_TYPE = "MOBILE_GOOGLE_CLIENT";

    /**
     * Launcher Name
     */
    private static final String GOOGLE_LAUNCHER_NAME = "MOBILE_GOOGLE_LAUNCHER";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileMessageGoogleLauncherService.class);

    /**
     * Log Service
     */
    protected final LogService logService;

    /**
     * Message Center Thread Pool Object
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * Construct Initialization
     *
     * @param logService Log Service
     * @param threadPool Thread Pool Object
     */
    public MobileMessageGoogleLauncherService(LogService logService, MessageCenterThreadPool threadPool) {
        this.logService = logService;
        this.threadPool = threadPool;
    }

    @Override
    public String name() {
        return GOOGLE_LAUNCHER_NAME;
    }

    @Override
    public LauncherResultModel execute(LauncherTemplateModel ltm, MobileMessageConfigModel config) {
        // segmentation send mail task
        final int size = ltm.getRecipients().size();
        for (int i = 0; i < size; i = i + MAX_RECIPIENT_LENGTH) {
            final List<String> recipient = ltm.getRecipients().subList(i, Math.min(i + MAX_RECIPIENT_LENGTH, size));
            // submit email sending tasks in the thread pool
            threadPool.submit(() -> {
                try {
                    LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] >>> START SEND MOBILE GOOGLE.");
                    LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] >>> MOBILE GOOGLE RECIPIENTS: {}", recipient);
                    LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] >>> MOBILE GOOGLE CLIENT: {}", JsonUtil.toJson(config));
                    LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] >>> MOBILE GOOGLE TEMPLATE TITLE: {}", ltm.getMessageTitle());
                    LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] >>> MOBILE GOOGLE TEMPLATE CONTENT: {}", ltm.getMessageContent());
                    // execute the operation of sending mobile
                    execute(client(config), recipient, ltm);
                } finally {
                    LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] >>> END SEND MOBILE GOOGLE.");
                }
            });
        }
        return () -> 0;
    }

    /**
     * Get Client Firebase App
     *
     * @param config Client Config
     * @return Client Firebase App
     */
    protected FirebaseApp client(MobileMessageConfigModel config) {
        try {
            final String name = Md5Util.execute(Md5Util.execute(config.getApplicationName()));
            FirebaseApp firebase = ExpiredCache.get(CACHE_TYPE, name);
            if (firebase == null) {
                firebase = client(name, config);
            }
            return firebase;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create Client Firebase App
     *
     * @param name   Client Name
     * @param config Client Config
     * @return Client Firebase App
     */
    protected synchronized FirebaseApp client(String name, MobileMessageConfigModel config) throws Exception {
        final String content = config.content();
        FirebaseApp firebase = ExpiredCache.get(CACHE_TYPE, name);
        if (firebase == null) {
            try (final InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
                final FirebaseOptions options = FirebaseOptions
                        .builder()
                        .setCredentials(GoogleCredentials.fromStream(input))
                        .build();
                firebase = FirebaseApp.initializeApp(options);
                ExpiredCache.set(CACHE_TYPE, name, firebase);
            }
        }
        return firebase;
    }


    /**
     * Send Mobile Message
     *
     * @param firebase   Client Firebase App
     * @param recipients Recipients
     * @param ltm        Launcher Template Model
     */
    protected void execute(FirebaseApp firebase, List<String> recipients, LauncherTemplateModel ltm) {
        try {
            final MulticastMessage.Builder builder = MulticastMessage.builder();
            builder.addAllTokens(recipients);
            final Map<String, String> content = JsonUtil.fromJsonToMap(ltm.getMessageContent(), String.class, String.class);
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
            LOGGER.info("[ MOBILE GOOGLE LAUNCHER ] RESULT >>> {}", batchResponse.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            LOGGER.error("[ MOBILE GOOGLE LAUNCHER ] ERROR >>> {}", e.getMessage());
        }
    }

}
