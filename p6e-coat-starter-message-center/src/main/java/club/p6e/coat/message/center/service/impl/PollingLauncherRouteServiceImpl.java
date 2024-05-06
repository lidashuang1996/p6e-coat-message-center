package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.service.LauncherRouteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PollingLauncherRouteServiceImpl.class,
        ignored = PollingLauncherRouteServiceImpl.class
)
public class PollingLauncherRouteServiceImpl implements LauncherRouteService {

    protected final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String name() {
        return "POLLING";
    }

    @Override
    public ConfigModel execute(LauncherModel launcher, List<ConfigModel> list) {
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            final List<ConfigModel> result = list
                    .stream()
                    .filter(ConfigModel::enable)
                    .sorted(Comparator.comparing(ConfigModel::id))
                    .toList();
            if (result.isEmpty()) {
                return null;
            } else if (result.size() == 1) {
                return result.get(0);
            } else {
                return list.get((index.getAndIncrement() % result.size()));
            }
        }
    }

}
