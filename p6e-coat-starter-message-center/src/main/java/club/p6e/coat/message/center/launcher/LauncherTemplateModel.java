package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.template.TemplateModel;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 模板消息模型
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LauncherTemplateModel extends TemplateModel {

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

    /**
     * 设置日志参数
     *
     * @param param 日志参数
     */
    void setLogData(Map<String, String> param);

    /**
     * 添加日志参数
     *
     * @param key   日志参数 KEY
     * @param value 日志参数 VALUE
     */
    void putLogData(String key, String value);

    /**
     * 获取日志参数
     *
     * @return 日志参数
     */
    Map<String, String> getLogData();

    static LauncherTemplateModel build(LauncherStartingModel starting, TemplateModel template) {
        return new LauncherTemplateModel() {

            @Override
            public Integer id() {
                return 0;
            }

            @Override
            public String key() {
                return "";
            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public String language() {
                return "";
            }

            @Override
            public String type() {
                return "";
            }

            @Override
            public String title() {
                return "";
            }

            @Override
            public String content() {
                return "";
            }

            @Override
            public String description() {
                return "";
            }

            @Override
            public String parser() {
                return "";
            }

            @Override
            public byte[] parserSource() {
                return new byte[0];
            }

            @Override
            public Map<String, String> getMessageParam() {
                return Map.of();
            }

            @Override
            public void setMessageParam(Map<String, String> param) {

            }

            @Override
            public String getMessageTitle() {
                return "";
            }

            @Override
            public void setMessageTitle(String title) {

            }

            @Override
            public String getMessageContent() {
                return "";
            }

            @Override
            public void setMessageContent(String content) {

            }

            @Override
            public List<File> getAttachment() {
                return List.of();
            }

            @Override
            public void cleanAttachment() {

            }

            @Override
            public void addAttachment(File file) {

            }

            @Override
            public void removeAttachment(File file) {

            }

            @Override
            public void removeAttachmentAt(int index) {

            }

            @Override
            public void setAttachment(List<File> files) {

            }

            @Override
            public void setLogData(Map<String, String> param) {

            }

            @Override
            public void putLogData(String key, String value) {

            }

            @Override
            public Map<String, String> getLogData() {
                return Map.of();
            }
        };
    }

}
