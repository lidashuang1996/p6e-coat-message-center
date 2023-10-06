package club.p6e.coat.message.center.config;

/**
 * 移动消息配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigData extends ConfigData {

    public String applicationId();

    public String applicationKey();

    public String applicationSecret();

    public String applicationUrl();

    public String platform();

}
