package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * 邮件消息配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageConfigModel extends ConfigModel {

    /**
     * 设置主机端口
     *
     * @param port 主机端口
     */
    void setPort(int port);

    /**
     * 获取主机端口
     *
     * @return 主机端口
     */
    int getPort();

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    void setHost(String host);

    /**
     * 获取主机地址
     *
     * @return 主机地址
     */
    String getHost();

    /**
     * 设置是否开启认证
     *
     * @param auth 是否开启认证
     */
    void setAuth(boolean auth);

    /**
     * 获取是否开启认证
     *
     * @return 是否开启认证
     */
    boolean isAuth();

    /**
     * 设置否开启TLS
     *
     * @param tls 是否开启 TLS
     */
    void setTls(boolean tls);

    /**
     * 获取是否开启TLS
     *
     * @return 是否开启TLS
     */
    boolean isTls();

    /**
     * 设置发件人
     *
     * @param from 发件人
     */
    void setFrom(String from);

    /**
     * 获取发件人
     *
     * @return 发件人
     */
    String getFrom();

    /**
     * 设置密码
     *
     * @param password 密码
     */
    void setPassword(String password);

    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 设置其他参数
     *
     * @param other 其他参数
     */
    void setOther(Map<String, String> other);

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    Map<String, String> getOther();

}
