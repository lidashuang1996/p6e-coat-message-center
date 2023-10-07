package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.MobileMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;
import club.p6e.coat.message.center.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * GOOGLE FCM
 * 移动消息发送平台
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = AndroidGoogleMobileMessageLauncherPlatform.class,
        ignored = AndroidGoogleMobileMessageLauncherPlatform.class
)
public class AndroidGoogleMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {

    /**
     * 最多的收件人长度
     */
    private static final int MAX_RECIPIENT_LENGTH = 300;

    /**
     * 客户端缓存对象
     */
    private static final Map<String, FirebaseApp> FIREBASE_APP_CACHE = new ConcurrentHashMap<>();

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidGoogleMobileMessageLauncherPlatform.class);

    @Override
    public String name() {
        return "ANDROID_GOOGLE";
    }

    @Override
    public List<Function<Void, String>> execute(MobileMessageConfigData config, TemplateData template, List<String> recipients) {
        final FirebaseMessaging firebaseMessaging;
        try {
            firebaseMessaging = FirebaseMessaging.getInstance(getFirebaseApp(config));
        } catch (Exception e) {
            throw new RuntimeException("[GOOGLE PUSH FIREBASE MESSAGING ERROR]", e);
        }
        final String content = template.content();
        final Model model = JsonUtil.fromJson(content, Model.class);
        if (model == null) {
            throw new NullPointerException("[GOOGLE PUSH MODEL ERROR] >> model value is null");
        }
        final int size = recipients.size();
        final int stride = MAX_RECIPIENT_LENGTH;
        final List<Function<Void, String>> result = new ArrayList<>();
        for (int i = 0; i < size; i = i + stride) {
            final List<String> rs = recipients.subList(i, Math.min(i + stride, size));
            result.add(v -> {
                final MulticastMessage.Builder builder = MulticastMessage.builder();
                builder.addAllTokens(rs);
                if (model.getFcmOptions() != null) {
                    builder.setFcmOptions(getFcmOptions(model));
                }
                if (model.getNotification() != null) {
                    builder.setNotification(getNotification(model));
                }
                if (model.getData() != null && !model.getData().isEmpty()) {
                    builder.putAllData(model.getData());
                }
                if (model.getAndroid() != null) {
                    builder.setAndroidConfig(getAndroid(model));
                }
                try {
                    final BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(builder.build());
                    return batchResponse.getSuccessCount() + "_" + batchResponse.getFailureCount();
                } catch (FirebaseMessagingException e) {
                    LOGGER.error("[ANDROID GOOGLE PUSH ERROR]", e);
                    throw new RuntimeException(e);
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
    private FirebaseApp getFirebaseApp(MobileMessageConfigData config) throws Exception {
        final String name = config.applicationName();
        FirebaseApp firebase = FIREBASE_APP_CACHE.get(name);
        if (firebase == null) {
            firebase = createFirebaseApp(config);
            FIREBASE_APP_CACHE.put(name, firebase);
        }
        return firebase;
    }

    /**
     * 创建 FirebaseApp 对象
     *
     * @param config 配置对象
     * @throws Exception 异常对象
     */
    private synchronized FirebaseApp createFirebaseApp(MobileMessageConfigData config) throws Exception {
        final String name = config.applicationName();
        FirebaseApp firebase = FIREBASE_APP_CACHE.get(name);
        if (firebase == null) {
            try (
                    final InputStream inputStream = new ByteArrayInputStream(config.applicationContent().getBytes())
            ) {
                final FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(inputStream))
                        .build();
                return FirebaseApp.initializeApp(options);
            }
        }
        return firebase;
    }

    /**
     * 获取 AndroidConfig 对象
     *
     * @param model 模型对象
     * @return AndroidConfig 对象
     */
    private AndroidConfig getAndroid(Model model) {
        final AndroidConfig.Builder builder = AndroidConfig.builder();
        if (model.getAndroid().getPriority() != null) {
            if ("NORMAL".equalsIgnoreCase(model.getAndroid().getPriority())) {
                builder.setPriority(AndroidConfig.Priority.NORMAL);
            } else {
                builder.setPriority(AndroidConfig.Priority.HIGH);
            }
        }
        if (model.getAndroid().getTtl() != null) {
            builder.setTtl(Long.parseLong(model.getAndroid().getTtl()));
        }
        if (model.getAndroid().getTtl() != null) {
            builder.setTtl(Long.parseLong(model.getAndroid().getTtl()));
        }
        if (model.getAndroid().getData() != null
                && !model.getAndroid().getData().isEmpty()) {
            builder.putAllData(model.getAndroid().getData());
        }
        if (model.getAndroid().getFcmOptions() != null
                && model.getAndroid().getFcmOptions().getAnalyticsLabel() != null) {
            builder.setFcmOptions(AndroidFcmOptions.builder()
                    .setAnalyticsLabel(model.getAndroid().getFcmOptions().getAnalyticsLabel()).build());
        }
        if (model.getAndroid().getDirectBootOk() != null) {
            builder.setDirectBootOk(model.getAndroid().getDirectBootOk());
        }
        if (model.getAndroid().getNotification() != null) {
            builder.setNotification(getAndroidNotification(model));
        }
        return builder.build();
    }

    /**
     * 获取 AndroidNotification 对象
     *
     * @param model 模型对象
     * @return AndroidNotification 对象
     */
    private AndroidNotification getAndroidNotification(Model model) {
        final AndroidNotification.Builder builder = AndroidNotification.builder();
        final AndroidNotificationModel notification = model.getAndroid().getNotification();
        if (notification.getTitle() != null) {
            builder.setTitle(notification.getTitle());
        }
        if (notification.getBody() != null) {
            builder.setBody(notification.getBody());
        }
        if (notification.getImage() != null) {
            builder.setImage(notification.getImage());
        }
        if (notification.getIcon() != null) {
            builder.setIcon(notification.getIcon());
        }
        if (notification.getColor() != null) {
            builder.setColor(notification.getColor());
        }
        if (notification.getSound() != null) {
            builder.setSound(notification.getSound());
        }
        if (notification.getTag() != null) {
            builder.setTag(notification.getTag());
        }
        if (notification.getClickAction() != null) {
            builder.setClickAction(notification.getClickAction());
        }
        if (notification.getBodyLocKey() != null) {
            builder.setBodyLocalizationKey(notification.getBodyLocKey());
        }
        if (notification.getBodyLocArgs() != null) {
            builder.addAllBodyLocalizationArgs(notification.getBodyLocArgs());
        }
        if (notification.getTitleLocKey() != null) {
            builder.setTitleLocalizationKey(notification.getTitleLocKey());
        }
        if (notification.getTitleLocArgs() != null) {
            builder.addAllTitleLocalizationArgs(notification.getTitleLocArgs());
        }
        if (notification.getChannelId() != null) {
            builder.setChannelId(notification.getChannelId());
        }
        if (notification.getTicker() != null) {
            builder.setTicker(notification.getTicker());
        }
        if (notification.getSticky() != null) {
            builder.setSticky(notification.getSticky());
        }
        if (notification.getEventTime() != null) {
            builder.setEventTimeInMillis(Long.parseLong(notification.getEventTime()));
        }
        if (notification.getLocalOnly() != null) {
            builder.setLocalOnly(notification.getLocalOnly());
        }
        if (notification.getNotificationPriority() != null) {
            switch (notification.getNotificationPriority().toUpperCase()) {
                case "MIN" -> builder.setPriority(AndroidNotification.Priority.MIN);
                case "LOW" -> builder.setPriority(AndroidNotification.Priority.LOW);
                case "HIGH" -> builder.setPriority(AndroidNotification.Priority.HIGH);
                case "MAX" -> builder.setPriority(AndroidNotification.Priority.MAX);
                default -> builder.setPriority(AndroidNotification.Priority.DEFAULT);
            }
        }
        if (notification.getDefaultSound() != null) {
            builder.setDefaultSound(notification.getDefaultSound());
        }
        if (notification.getDefaultVibrateTimings() != null) {
            builder.setDefaultVibrateTimings(notification.getDefaultVibrateTimings());
        }
        if (notification.getDefaultLightSettings() != null) {
            builder.setDefaultLightSettings(notification.getDefaultLightSettings());
        }
        if (notification.getVibrateTimings() != null) {
            final long[] vibrateTimingsInMillis = new long[notification.getVibrateTimings().size()];
            for (int i = 0; i < notification.getVibrateTimings().size(); i++) {
                vibrateTimingsInMillis[i] = Long.parseLong(notification.getVibrateTimings().get(i));
            }
            builder.setVibrateTimingsInMillis(vibrateTimingsInMillis);
        }
        if (notification.getVisibility() != null) {
            switch (notification.getVisibility().toUpperCase()) {
                case "SECRET" -> builder.setVisibility(AndroidNotification.Visibility.SECRET);
                case "PUBLIC" -> builder.setVisibility(AndroidNotification.Visibility.PUBLIC);
                default -> builder.setVisibility(AndroidNotification.Visibility.PRIVATE);
            }
        }
        if (notification.getNotificationCount() != null) {
            builder.setNotificationCount(notification.getNotificationCount());
        }
        if (notification.getLightSettings() != null) {
            builder.setLightSettings(getAndroidNotificationLightSettings(model));
        }
        return builder.build();
    }

    /**
     * 获取 LightSettings 对象
     *
     * @param model 模型对象
     * @return LightSettings 对象
     */
    private LightSettings getAndroidNotificationLightSettings(Model model) {
        final LightSettings.Builder builder = LightSettings.builder();
        final AndroidNotificationLightSettingsModel settings = model.getAndroid().getNotification().getLightSettings();
        if (settings.getColor() != null) {
            builder.setColor(LightSettingsColor.fromString(settings.getColor()));
        }
        if (settings.getLightOnDuration() != null) {
            builder.setLightOnDurationInMillis(Long.parseLong(settings.getLightOnDuration()));
        }
        if (settings.getLightOffDuration() != null) {
            builder.setLightOffDurationInMillis(Long.parseLong(settings.getLightOffDuration()));
        }
        return builder.build();
    }

    /**
     * 获取 FcmOptions 对象
     *
     * @param model 模型对象
     * @return FcmOptions 对象
     */
    private FcmOptions getFcmOptions(Model model) {
        final FcmOptions.Builder builder = FcmOptions.builder();
        if (model.getFcmOptions() != null
                && model.getFcmOptions().getAnalyticsLabel() != null) {
            builder.setAnalyticsLabel(model.getFcmOptions().getAnalyticsLabel());
        }
        return builder.build();
    }

    /**
     * 获取 Notification 对象
     *
     * @param model 模型对象
     * @return Notification 对象
     */
    private Notification getNotification(Model model) {
        final Notification.Builder builder = Notification.builder();
        if (model.getNotification().getTitle() != null) {
            builder.setTitle(model.getNotification().getTitle());
        }
        if (model.getNotification().getBody() != null) {
            builder.setTitle(model.getNotification().getBody());
        }
        if (model.getNotification().getImage() != null) {
            builder.setTitle(model.getNotification().getImage());
        }
        return builder.build();
    }

    @Data
    private static class Model implements Serializable {
        private AndroidModel android;
        private NotificationModel notification;
        @JsonProperty("fcm_options")
        private FcmOptionsModel fcmOptions;
        private Map<String, String> data;
    }

    @Data
    private static class FcmOptionsModel implements Serializable {
        private String analyticsLabel;
    }

    @Data
    private static class NotificationModel implements Serializable {
        private String title;
        private String body;
        private String image;
    }

    @Data
    private static class AndroidModel implements Serializable {
        // NORMAL
        // HIGH
        private String priority;
        private String ttl;
        private Map<String, String> data;
        @JsonProperty("fcm_options")
        private FcmOptionsModel fcmOptions;
        @JsonProperty("direct_boot_ok")
        private Boolean directBootOk;
        private AndroidNotificationModel notification;
    }

    @Data
    private static class AndroidNotificationModel implements Serializable {
        private String title;
        private String body;
        private String image;
        private String icon;
        private String color;
        private String sound;
        private String tag;
        @JsonProperty("click_action")
        private String clickAction;
        @JsonProperty("body_loc_key")
        private String bodyLocKey;
        @JsonProperty("body_loc_args")
        private List<String> bodyLocArgs;
        @JsonProperty("title_loc_key")
        private String titleLocKey;
        @JsonProperty("title_loc_args")
        private List<String> titleLocArgs;
        @JsonProperty("channel_id")
        private String channelId;
        private String ticker;
        private Boolean sticky;
        @JsonProperty("event_time")
        private String eventTime;
        @JsonProperty("local_only")
        private Boolean localOnly;

        // PRIORITY_UNSPECIFIED	如果未指定优先级，则通知优先级设置为PRIORITY_DEFAULT 。
        // PRIORITY_MIN	最低通知优先级。具有此PRIORITY_MIN的通知可能不会显示给用户，除非在特殊情况下，例如详细的通知日志。
        // PRIORITY_LOW	较低的通知优先级。与具有PRIORITY_DEFAULT的通知相比，UI 可以选择将通知显示得更小，或者显示在列表中的不同位置。
        // PRIORITY_DEFAULT	默认通知优先级。如果应用程序不对自己的通知设置优先级，则对所有通知使用此值。
        // PRIORITY_HIGH	更高的通知优先级。将其用于更重要的通知或警报。与具有PRIORITY_DEFAULT的通知相比，UI 可以选择将这些通知显示得更大，或者显示在通知列表中的不同位置。
        // PRIORITY_MAX	最高通知优先级。将此用于需要用户及时注意或输入的应用程序最重要的项目。
        //
        // MIN
        // LOW
        // DEFAULT
        // HIGH
        // MAX
        @JsonProperty("notification_priority")
        private String notificationPriority;
        @JsonProperty("default_sound")
        private Boolean defaultSound;
        @JsonProperty("default_vibrate_timings")
        private Boolean defaultVibrateTimings;
        @JsonProperty("default_light_settings")
        private Boolean defaultLightSettings;
        @JsonProperty("vibrate_timings")
        private List<String> vibrateTimings;
        // VISIBILITY_UNSPECIFIED	如果未指定，则默认为Visibility.PRIVATE 。
        // PRIVATE	在所有锁屏上显示此通知，但在安全锁屏上隐藏敏感或私人信息。
        // PUBLIC	在所有锁屏上完整显示此通知。
        // SECRET	不要在安全锁屏上泄露此通知的任何部分。
        private String visibility;
        @JsonProperty("notification_count")
        private Integer notificationCount;
        @JsonProperty("light_settings")
        private AndroidNotificationLightSettingsModel lightSettings;
    }

    @Data
    private static class AndroidNotificationLightSettingsModel implements Serializable {
        private String color;
        private String lightOnDuration;
        private String lightOffDuration;
    }
}
