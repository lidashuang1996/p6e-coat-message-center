package club.p6e.coat.message.center.launcher;

/**
 * 发射器回执
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherData {

    /**
     * 发射的消息编号
     *
     * @return 消息编号
     */
    public String id();

    /**
     * 发射的消息类型
     *
     * @return 消息类型
     */
    public String type();

}
