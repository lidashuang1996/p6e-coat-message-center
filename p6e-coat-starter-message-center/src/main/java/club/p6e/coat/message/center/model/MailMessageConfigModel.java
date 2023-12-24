package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * 邮件配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageConfigModel extends ConfigModel {

    /**
     * 获取主机端口
     *
     * @return 主机端口
     */
    public void setPort(int port);
    public int getPort();

    /**
     * 获取主机地址
     *
     * @return 主机地址
     */
    public void setHost(String host);
    public String getHost();

    /**
     * 获取是否开启认证
     *
     * @return 是否开启认证
     */
    public void setAuth(boolean auth);

    public boolean isAuth();

    /**
     * 获取是否开启 TLS
     *
     * @return 是否开启 TLS
     */
    public void setTls(boolean tls);

    public boolean isTls();

    /**
     * 获取发件人
     *
     * @return 发件人
     */
    public void setFrom(String from);

    public String getFrom();

    /**
     * 获取密码
     *
     * @return 密码
     */
    public void setPassword(String password);
    public String getPassword();

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    public void setOther(Map<String, String> other);
    public Map<String, String> getOther();


}
