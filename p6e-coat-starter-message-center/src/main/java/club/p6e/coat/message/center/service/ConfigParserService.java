package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;

/**
 * 配置解析器服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigParserService {

    /**
     * 获取配置解析器的名称
     *
     * @return 配置解析器的名称
     */
    String name();

    /**
     * ConfigModel >>> ([XXX]ConfigModel extends ConfigModel)
     *
     * @param config 配置源对象
     * @return XXX 配置数据对象
     */
    ConfigModel execute(ConfigModel config);

}
