package club.p6e.coat.message.center.config;

/**
 * 基础配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigData {

    /**
     * 获取 ID
     *
     * @return ID
     */
    public Integer id();

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String name();

    /**
     * 获取类型
     *
     * @return 类型
     */
    public String type();

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String content();
}
