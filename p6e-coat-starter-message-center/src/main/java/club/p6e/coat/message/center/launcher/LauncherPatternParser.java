package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigSource;

/**
 * 发射模式解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherPatternParser {

    /**
     * 获取发射的配置源对象
     *
     * @param launcherSource 发射器源对象
     * @return 配置源对象
     */
    public ConfigSource execute(LauncherSource launcherSource);

}
