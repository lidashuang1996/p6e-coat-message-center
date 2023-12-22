package club.p6e.coat.message.center.config;

import java.util.Map;

/**
 * 短信配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigModel extends ConfigModel {

    public String applicationSign();

    public String applicationKeyId();

    public String applicationKeySecret();

    public String applicationDomain();

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    public Map<String, String> other();

}
