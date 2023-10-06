package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.MobileMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;
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
        value = GoogleFcmMobileMessageLauncherPlatform.class,
        ignored = GoogleFcmMobileMessageLauncherPlatform.class
)
public class GoogleFcmMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {

    @Override
    public List<Function<Void, Void>> execute(MobileMessageConfigData config, TemplateData template, List<String> recipients) {
        return List.of(u -> null, u -> null);
    }

}
