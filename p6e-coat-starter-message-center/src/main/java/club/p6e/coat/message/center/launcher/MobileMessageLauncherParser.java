
package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.MobileMessageConfigData;

import java.util.function.Function;

/**
 * 移动消息发射解析器
 * MobileMessageConfigData 移动消息配置类型
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MobileMessageLauncherParser extends LauncherParser<MobileMessageConfigData> {

    /**
     * 注册移动消息发射某平台的实现
     *
     * @param key   平台名称
     * @param value 移动消息发射平台对象
     */
    public void register(String key, MobileMessageLauncherPlatform value);

    /**
     * 卸载移动消息发射某平台的实现
     *
     * @param key 平台名称
     */
    public void unregister(String key);

}
