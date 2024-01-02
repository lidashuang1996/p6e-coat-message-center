package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MobileMessageConfigModel;

/**
 * 移动消息配置解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigParserService extends ConfigParserService {

    /**
     * 执行将配置数据对象转换为移动消息配置数据对象
     *
     * @param config 配置数据对象
     * @return 移动消息配置数据对象
     */
    @Override
    MobileMessageConfigModel execute(ConfigModel config);

}
