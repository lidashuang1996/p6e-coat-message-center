package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.service.LauncherRouteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 默认的发射器路由服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = DefaultLauncherRouteServiceImpl.class,
        ignored = DefaultLauncherRouteServiceImpl.class
)
public class DefaultLauncherRouteServiceImpl extends PollingLauncherRouteServiceImpl implements LauncherRouteService {

    @Override
    public String name() {
        return "DEFAULT";
    }

    @Override
    public ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs) {
        return super.execute(launcher, configs);
    }

}
