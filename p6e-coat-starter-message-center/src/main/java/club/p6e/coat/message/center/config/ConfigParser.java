package club.p6e.coat.message.center.config;


/**
 * 配置解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigParser {

    /**
     * 执行配置源对象转换为配置数据对象
     *
     * @param config 配置源对象
     * @return 配置数据对象
     */
    ConfigModel execute(ConfigModel config);

}
