package club.p6e.coat.message.center.config;

/**
 * 短信配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigData extends ConfigData {

    public String applicationId();

    public String applicationKey();

    public String applicationSecret();

    public String applicationUrl();

}
