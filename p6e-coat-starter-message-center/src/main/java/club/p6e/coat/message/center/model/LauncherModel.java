package club.p6e.coat.message.center.model;

import club.p6e.coat.message.center.MessageType;

import java.util.List;

/**
 * 发射器模型
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
    Integer id();

    /**
     * 是否启用
     *
     * @return 是否启用
     */
    boolean enable();

    /**
     * 类型
     *
     * @return 类型
     */
    MessageType type();

    /**
     * 名称
     *
     * @return 名称
     */
    String name();

    /**
     * 模板
     *
     * @return 模板
     */
    String template();

    /**
     * 描述
     *
     * @return 描述
     */
    String description();

    /**
     * 路由
     *
     * @return 模式
     */
    String route();

    /**
     * 路由源代码
     *
     * @return 路由源代码
     */
    byte[] routeSource();


    /**
     * 路由
     *
     * @return 模式
     */
    String parser();

    /**
     * 路由源代码
     *
     * @return 路由源代码
     */
    byte[] parserSource();

    /**
     * 配置列表
     *
     * @return 配置列表
     */
    List<ConfigMapperModel> configs();

    /**
     * 发射器映射配置模型
     */
    interface ConfigMapperModel {

        /**
         * ID
         *
         * @return ID
         */
        Integer id();

        /**
         * 属性
         *
         * @return 属性
         */
        String attribute();
    }
    
}
