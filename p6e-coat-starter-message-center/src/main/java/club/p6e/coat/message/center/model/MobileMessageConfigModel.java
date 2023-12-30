package club.p6e.coat.message.center.model;

import java.util.Map;

/**
 * 移动消息配置
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageConfigModel extends ConfigModel {

    public void setApplicationName(String applicationName);

    public String getApplicationName();

    public void setApplicationId(String applicationId);

    public String getApplicationId();

    public void setApplicationKey(String applicationId);

    public String getApplicationKey();

    public void setApplicationSecret(String applicationSecret);

    public String getApplicationSecret();

    /**
     * 获取其他参数
     *
     * @return 其他参数
     */
    public void setOther(Map<String, String> other);

    public Map<String, String> getOther();

}
