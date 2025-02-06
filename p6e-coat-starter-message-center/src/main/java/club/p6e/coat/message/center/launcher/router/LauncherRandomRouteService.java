package club.p6e.coat.message.center.launcher.router;

import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.launcher.LauncherModel;
import club.p6e.coat.message.center.launcher.LauncherRouteService;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LauncherRandomRouteService
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherRandomRouteService implements LauncherRouteService {

    /**
     * Router Name
     */
    private static final String DEFAULT_ROUTER_NAME = "LAUNCHER_RANDOM_ROUTER";

    @Override
    public String name() {
        return DEFAULT_ROUTER_NAME;
    }

    @Override
    public ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs) {
        if (configs == null || configs.isEmpty()) {
            return null;
        } else {
            final List<ConfigModel> result = configs
                    .stream()
                    .filter(ConfigModel::enable)
                    .sorted(Comparator.comparing(ConfigModel::id))
                    .toList();
            if (result.isEmpty()) {
                return null;
            } else if (result.size() == 1) {
                return result.get(0);
            } else {
                return result.get(ThreadLocalRandom.current().nextInt(result.size()));
            }
        }
    }

}
