package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.model.ConfigModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherRouteService {

    /**
     * 获取配置解析器的名称
     *
     * @return 配置解析器的名称
     */
    String name();

    ConfigModel execute(LauncherModel launcher, List<ConfigModel> list);

}
