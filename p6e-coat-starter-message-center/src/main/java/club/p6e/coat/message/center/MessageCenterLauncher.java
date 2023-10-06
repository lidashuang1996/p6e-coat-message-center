package club.p6e.coat.message.center;

import club.p6e.coat.message.center.launcher.Launcher;
import club.p6e.coat.message.center.launcher.LauncherData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息中心发射器
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MessageCenterLauncher.class,
        ignored = MessageCenterLauncher.class
)
public class MessageCenterLauncher {

    /**
     * 状态
     */
    private static String STATUS = "NOT_RUNNING";

    /**
     * 默认语言
     */
    private static final String DEFAULT_LANGUAGE = "ZH-CMN-HANS";

    /**
     * 发射器对象
     */
    private final Launcher launcher;

    /**
     * 读取状态
     *
     * @return 状态
     */
    public static String getStatus() {
        return STATUS;
    }

    /**
     * 写入状态
     *
     * @param status 状态
     */
    public static void setStatus(String status) {
        STATUS = status;
    }

    /**
     * 构造方法初始化
     *
     * @param launcher 发射器对象
     */
    public MessageCenterLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    /**
     * 执行发射消息
     *
     * @param mark       标记
     * @param recipients 收件人
     * @return 发射器回执对象
     */
    public LauncherData execute(String mark, List<String> recipients) {
        return launcher.execute(mark, DEFAULT_LANGUAGE, new HashMap<>(0), recipients);
    }

    /**
     * 执行发射消息
     *
     * @param mark       标记
     * @param language   语言
     * @param recipients 收件人
     * @return 发射器回执对象
     */
    public LauncherData execute(String mark, String language, List<String> recipients) {
        return launcher.execute(mark, language, new HashMap<>(0), recipients);
    }

    /**
     * 执行发射消息
     *
     * @param mark       标记
     * @param data       参数
     * @param recipients 收件人
     * @return 发射器回执对象
     */
    public LauncherData execute(String mark, Map<String, String> data, List<String> recipients) {
        return launcher.execute(mark, DEFAULT_LANGUAGE, data, recipients);
    }

    /**
     * 执行发射消息
     *
     * @param mark       标记
     * @param language   语言
     * @param data       参数
     * @param recipients 收件人
     * @return 发射器回执对象
     */
    public LauncherData execute(String mark, String language, Map<String, String> data, List<String> recipients) {
        return launcher.execute(mark, language, data, recipients);
    }

}
