package club.p6e.coat.message.center.config;

import club.p6e.coat.message.center.MessageType;

/**
 * 基础配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ConfigModel {

    /**
     * 获取 ID
     *
     * @return ID
     */
    public int id();

    /**
     * 是否启用
     * @return
     */
    public boolean enable();

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
    public MessageType type();

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String content();

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String description();


    public String parser();


    public byte[] parserSource();

    /**
     * 获取限流规则
     *
     * @return 限流规则
     */
    public String rule();


}
