package club.p6e.coat.message.center.launcher.router;

import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.launcher.LauncherModel;
import club.p6e.coat.message.center.launcher.LauncherRouteService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * LauncherDefaultRouteService
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LauncherDefaultRouteService extends LauncherPollingRouteService implements LauncherRouteService {

    /**
     * Router Name
     */
    private static final String DEFAULT_ROUTER_NAME = "LAUNCHER_DEFAULT_ROUTER";

    @Override
    public String name() {
        return DEFAULT_ROUTER_NAME;
    }

    @Override
    public ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs) {
        return super.execute(launcher, configs);
    }

}
