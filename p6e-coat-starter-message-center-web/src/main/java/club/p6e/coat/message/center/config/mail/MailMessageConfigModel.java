package club.p6e.coat.message.center.config.mail;

import club.p6e.coat.message.center.config.ConfigModel;

import java.io.Serializable;
import java.util.Map;

/**
 * MailMessageConfigModel
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageConfigModel extends ConfigModel, Serializable {

    /**
     * Set Port
     *
     * @param port Port
     */
    void setPort(int port);

    /**
     * Get Port
     *
     * @return Port
     */
    int getPort();

    /**
     * Set Host
     *
     * @param host Host
     */
    void setHost(String host);

    /**
     * Get Host
     *
     * @return Host
     */
    String getHost();

    /**
     * Set Auth
     *
     * @param auth Auth
     */
    void setAuth(boolean auth);

    /**
     * Get Auth
     *
     * @return Auth
     */
    boolean isAuth();

    /**
     * Set TLS
     *
     * @param tls TLS
     */
    void setTls(boolean tls);

    /**
     * Get TLS
     *
     * @return TLS
     */
    boolean isTls();

    /**
     * Set From
     *
     * @param from From
     */
    void setFrom(String from);

    /**
     * Get From
     *
     * @return From
     */
    String getFrom();

    /**
     * Set Password
     *
     * @param password Password
     */
    void setPassword(String password);

    /**
     * Get Password
     *
     * @return Password
     */
    String getPassword();

    /**
     * Set Other
     *
     * @param other Other
     */
    void setOther(Map<String, String> other);

    /**
     * Get Other
     *
     * @return Other
     */
    Map<String, String> getOther();

}
