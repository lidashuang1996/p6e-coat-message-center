package club.p6e.coat.message.center.model;

import club.p6e.coat.message.center.MessageType;

/**
 * 配置模型
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigModel {

    /**
     * ID
     *
     * @return ID
     */
    int id();

    /**
     * 限流规则
     *
     * @return 限流规则
     */
    String rule();

    /**
     * 类型
     *
     * @return 类型
     */
    MessageType type();

    /**
     * 是否启用
     *
     * @return 是否启用
     */
    boolean enable();

    /**
     * 名称
     *
     * @return 名称
     */
    String name();

    /**
     * 内容
     *
     * @return 内容
     */
    String content();

    /**
     * 描述
     *
     * @return 描述
     */
    String description();

    /**
     * 解析器
     *
     * @return 解析器
     */
    String parser();

    /**
     * 解析器源码
     *
     * @return 解析器源码
     */
    byte[] parserSource();

}
