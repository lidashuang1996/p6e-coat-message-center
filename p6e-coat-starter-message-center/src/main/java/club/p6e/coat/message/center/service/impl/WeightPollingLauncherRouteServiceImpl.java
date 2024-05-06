package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.service.LauncherRouteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = WeightPollingLauncherRouteServiceImpl.class,
        ignored = WeightPollingLauncherRouteServiceImpl.class
)
public class WeightPollingLauncherRouteServiceImpl
        extends PollingLauncherRouteServiceImpl implements LauncherRouteService {

    @Override
    public String name() {
        return "WEIGHT_POLLING";
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
                final List<String> weight = new ArrayList<>();
                final Map<String, ConfigModel> configs = new HashMap<>();
                for (final ConfigModel item : result) {
                    final Integer attribute = getAttributeData(launcher, item);
                    if (attribute != null) {
                        weight.add(item.id() + "@" + attribute);
                        configs.put(String.valueOf(item.id()), item);
                    }
                }
                if (weight.isEmpty() || configs.isEmpty()) {
                    return null;
                } else if (weight.size() == 1 || configs.size() == 1) {
                    final List<String> ks = new ArrayList<>(configs.keySet());
                    return configs.get(ks.get(0));
                } else {
                    int total = 0;
                    for (final String item : weight) {
                        total += Integer.parseInt(item.substring(item.lastIndexOf("@") + 1));
                    }
                    int rTotal = 0;
                    final int rIndex = index.getAndIncrement() % total;
                    for (final String item : weight) {
                        final int iMark = item.lastIndexOf("@");
                        final int iIndex = Integer.parseInt(item.substring(iMark));
                        if (rIndex < rTotal + iIndex) {
                            return configs.get(item.substring(0, iMark));
                        } else {
                            rTotal = rTotal + iIndex;
                        }
                    }
                    return null;
                }
            }
        }
    }

    private Integer getAttributeData(LauncherModel launcher, ConfigModel config) {
        try {
            for (final LauncherModel.ConfigMapperModel item : launcher.configs()) {
                if (item.id() == config.id()) {
                    final Map<String, String> content =
                            JsonUtil.fromJsonToMap(item.attribute(), String.class, String.class);
                    if (content != null && content.get("weight") != null) {
                        return Integer.valueOf(content.get("weight"));
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
