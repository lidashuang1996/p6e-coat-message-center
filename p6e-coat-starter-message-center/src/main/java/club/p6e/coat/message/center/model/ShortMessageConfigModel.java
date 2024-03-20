package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * 短消息配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigModel extends ConfigModel {

    /**
     * 设置应用编号
     *
     * @param id 应用编号
     */
    void setApplicationId(String id);

    /**
     * 获取应用编号
     *
     * @return 应用编号
     */
    String getApplicationId();

    /**
     * 设置应用 KEY
     *
     * @param key 应用 KEY
     */
    void setApplicationKey(String key);

    /**
     * 获取应用 KEY
     *
     * @return 应用 KEY
     */
    String getApplicationKey();

    /**
     * 设置应用名称
     *
     * @param name 应用名称
     */
    void setApplicationName(String name);

    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    String getApplicationName();

    /**
     * 设置应用密钥
     *
     * @param secret 应用密钥
     */
    void setApplicationSecret(String secret);

    /**
     * 获取应用密钥
     *
     * @return 应用密钥
     */
    String getApplicationSecret();

    /**
     * 设置应用领域
     *
     * @param domain 应用领域
     */
    void setApplicationDomain(String domain);

    /**
     * 获取应用领域
     *
     * @return 应用领域
     */
    String getApplicationDomain();

    /**
     * 设置其它参数
     *
     * @param other 其它参数
     */
    void setOther(Map<String, String> other);

    /**
     * 获取其它参数
     *
     * @return 其它参数
     */
    Map<String, String> getOther();

}
