package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.template.TemplateModel;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 模板消息模型
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherTemplateModel extends TemplateModel, Serializable {

    /**
     * 获取消息参数
     *
     * @return 消息参数
     */
    String getChat();

    String getType();

    /**
     * 获取消息参数
     *
     * @return 消息参数
     */
    Map<String, String> getMessageParam();

    /**
     * 设置消息参数
     *
     * @param param 消息参数
     */
    void setMessageParam(Map<String, String> param);

    /**
     * 获取消息标题
     *
     * @return 消息标题
     */
    String getMessageTitle();

    /**
     * 设置消息标题
     *
     * @param title 消息标题
     */
    void setMessageTitle(String title);

    /**
     * 获取消息内容
     *
     * @return 消息内容
     */
    String getMessageContent();

    /**
     * 设置消息内容
     *
     * @param content 消息内容
     */
    void setMessageContent(String content);

    /**
     * 获取附件
     *
     * @return 附件
     */
    List<File> getAttachment();

    /**
     * 清空附件
     */
    void cleanAttachment();

    /**
     * 添加附件
     *
     * @param file 附件
     */
    void addAttachment(File file);

    /**
     * 删除附件
     *
     * @param file 附件
     */
    void removeAttachment(File file);

    /**
     * 删除附件
     *
     * @param index 附件索引
     */
    void removeAttachmentAt(int index);

    /**
     * 设置附件
     *
     * @param files 附件
     */
    void setAttachment(List<File> files);

    List<String> getRecipients();

}
