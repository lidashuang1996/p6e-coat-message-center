package club.p6e.coat.message.center.launcher;

/**
 * 发射解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageLauncherParser extends LauncherParser {

    MailMessageLauncherModel execute(LauncherModel launcher);

}
