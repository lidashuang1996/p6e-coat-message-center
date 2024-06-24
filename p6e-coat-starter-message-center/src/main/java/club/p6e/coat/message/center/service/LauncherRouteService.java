package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.model.ConfigModel;

import java.util.List;

/**
 * 发射器路由服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherRouteService {

    /**
     * 获取发射器路由服务名称
     *
     * @return 发射器路由服务名称
     */
    String name();

    /**
     * 执行发射器路由服务
     *
     * @param configs  配置模型列表
     * @param launcher 发射器模型
     * @return 配置模型
     */
    ConfigModel execute(LauncherModel launcher, List<ConfigModel> configs);

}
