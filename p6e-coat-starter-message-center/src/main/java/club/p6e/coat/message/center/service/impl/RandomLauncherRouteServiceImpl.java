package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.service.LauncherRouteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机的发射器路由服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = RandomLauncherRouteServiceImpl.class,
        ignored = RandomLauncherRouteServiceImpl.class
)
public class RandomLauncherRouteServiceImpl implements LauncherRouteService {

    @Override
    public String name() {
        return "RANDOM";
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
