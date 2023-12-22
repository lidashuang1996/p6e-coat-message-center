package club.p6e.coat.message.center.config;

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
    public String port();

    /**
     * 获取主机地址
     *
     * @return 主机地址
     */
    public String host();

    /**
     * 获取是否开启认证
     *
     * @return 是否开启认证
     */
    public String auth();

    /**
     * 获取是否开启 TLS
     *
     * @return 是否开启 TLS
     */
    public String tls();

    /**
     * 获取发件人
     *
     * @return 发件人
     */
    public String from();

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String password();

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    public Map<String, String> other();

}
