package club.p6e.coat.message.center.config;

/**
 * 配置源
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigSource {

    /**
     * 获取 ID
     *
     * @return ID
     */
    public Integer id();

    /**
     * 获取限流规则
     *
     * @return 限流规则
     */
    public String rule();

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
     * 获取是否启用
     *
     * @return 是否启用
     */
    public Integer enable();

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String content();

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String description();

    /**
     * 获取解析器
     *
     * @return 解析器
     */
    public String parser();

    /**
     * 获取解析器源
     *
     * @return 解析器字节码
     */
    public byte[] parserSource();

    /**
     * 获取属性
     *
     * @return 属性
     */
    public String attribute();

}
