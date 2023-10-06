package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.MobileMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;
import club.p6e.coat.message.center.utils.GeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = DefaultMobileMessageLauncherParser.class,
        ignored = DefaultMobileMessageLauncherParser.class
)
public class DefaultMobileMessageLauncherParser implements MobileMessageLauncherParser {

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMobileMessageLauncherParser.class);

    private final Map<String, MobileMessageLauncherPlatform> platforms = new ConcurrentHashMap<>();

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    public DefaultMobileMessageLauncherParser(MessageCenterThreadPool threadPool) {
        this.threadPool = threadPool;
        register("MIUI", new MiUiOsMobileMessageLauncherPlatform());
        register("COLOR", new ColorOsMobileMessageLauncherPlatform());
        register("ORIGIN", new OriginOsMobileMessageLauncherPlatform());
        register("GOOGLE", new GoogleFcmMobileMessageLauncherPlatform());
        register("HARMONY", new HarmonyOsMobileMessageLauncherPlatform());
    }

    @Override
    public LauncherData execute(MobileMessageConfigData config, TemplateData template, List<String> recipients) {
        if (config == null) {
            throw new NullPointerException(
                    "when performing the send MMS operation, it was found config data is null");
        }
        if (template == null) {
            throw new NullPointerException(
                    "when performing the send MMS operation, it was found template data is null");
        }
        if (recipients == null) {
            throw new NullPointerException(
                    "when performing the send MMS operation, it was found recipients is null");
        }
        if (config.platform() == null) {
            throw new NullPointerException(
                    "when performing the send MMS operation, it was found that the platform in template data is null");
        }
        // submit send mail task
        final MobileMessageLauncherPlatform launcherPlatform = platforms.get(config.platform());
        if (launcherPlatform == null) {
            throw new RuntimeException();
        }
        LOGGER.info("[MMS MESSAGE] >>> PLATFORMS: " + config.platform());
        List<Function<Void, Void>> list = launcherPlatform.execute(config, template, recipients);
        if (list == null) {
            list = new ArrayList<>();
        }
        LOGGER.info("[MMS MESSAGE] >>> TASK SIZE: " + list.size());
        for (final Function<Void, Void> item : list) {
            threadPool.submit(() -> item.apply(null));
        }
        return new LauncherData() {
            @Override
            public String id() {
                return GeneratorUtil.uuid();
            }

            @Override
            public String type() {
                return config.type();
            }
        };
    }

    @Override
    public void register(String key, MobileMessageLauncherPlatform value) {
        platforms.put(key, value);
    }

    @Override
    public void unregister(String key) {
        platforms.remove(key);
    }
}
