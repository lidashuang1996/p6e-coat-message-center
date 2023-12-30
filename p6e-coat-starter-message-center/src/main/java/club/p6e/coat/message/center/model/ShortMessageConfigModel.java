package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * 短信配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ShortMessageConfigModel extends ConfigModel {


    public void setApplicationName(String applicationName);

    public String getApplicationName();

    public void setApplicationId(String applicationId);

    public String getApplicationId();

    public void setApplicationSecret(String applicationSecret);

    public String getApplicationSecret();

    public void setApplicationPlatform(String applicationPlatform);

    public String getApplicationPlatform();

    public void setApplicationDomain(String applicationPlatform);

    public String getApplicationDomain();

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    public void setOther(Map<String, String> other);

    public Map<String, String> getOther();

}
