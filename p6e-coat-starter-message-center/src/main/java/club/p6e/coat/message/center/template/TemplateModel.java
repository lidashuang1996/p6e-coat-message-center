package club.p6e.coat.message.center.template;

/**
 * 模板源
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateModel {
    /**
     * 获取 ID
     *
     * @return ID
     */
    Integer id();

    /**
     * 获取类型
     *
     * @return 类型
     */
    String type();

    /**
     * 获取标记
     *
     * @return 标记
     */
    String mark();

    /**
     * 获取名称
     *
     * @return 名称
     */
    String name();

    /**
     * 获取标题
     *
     * @return 标题
     */
    String title();

    /**
     * 获取内容
     *
     * @return 内容
     */
    String content();

    /**
     * 获取描述
     *
     * @return 描述
     */
    String description();

    /**
     * 获取解析器
     *
     * @return 解析器
     */
    String parser();

    /**
     * 获取解析器源
     *
     * @return 解析器字节码
     */
    byte[] parserSource();

}
