package club.p6e.coat.message.center.config;

import java.util.Map;

/**
 * 移动消息配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigModel extends ConfigModel {

    public String applicationName();

    public String applicationContent();

    public String platform();

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    public Map<String, String> other();

}
