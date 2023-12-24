package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;

/**
 * 配置解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigParserService {

    /**
     * 执行配置源对象转换为配置数据对象
     *
     * @param config 配置源对象
     * @return 配置数据对象
     */
    ConfigModel execute(ConfigModel config);

}
