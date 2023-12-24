package club.p6e.coat.message.center.model;

import club.p6e.coat.message.center.MessageType;

import java.util.List;

/**
 * 发射器源
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherModel {

    /**
     * 获取 ID
     *
     * @return ID
     */
    public Integer id();

    /**
     * 获取类型
     *
     * @return 类型
     */
    public MessageType type();

    /**
     * 获取记号
     *
     * @return 记号
     */
    public String mark();

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String name();

    /**
     * 获取是否启用
     *
     * @return 是否启用
     */
    public Integer enable();

    /**
     * 获取模板
     *
     * @return 模板
     */
    public String template();

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String description();

    /**
     * 获取模式
     *
     * @return 模式
     */
    public String pattern();

    /**
     * 获取模式字节源
     *
     * @return 模式字节源
     */
    public byte[] patternSource();

    /**
     * 获取语言
     *
     * @return 语言
     */
    public String language();

    public List<ConfigMapperModel> configs();

    public interface ConfigMapperModel {
        public Integer id();
        public String attribute();
    }
}
