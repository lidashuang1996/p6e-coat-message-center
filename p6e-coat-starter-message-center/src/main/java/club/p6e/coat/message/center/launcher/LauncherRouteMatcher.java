package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.config.ConfigModel;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherRouteMatcher {

    ConfigModel execute(LauncherModel launcher, List<ConfigModel> list);

}
