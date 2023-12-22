package club.p6e.coat.message.center.template;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface CommunicationTemplateModel extends TemplateModel {

    Map<String, String> getCommunicationParam();
    void setCommunicationParam(Map<String, String> content);

    String getCommunicationContent();
    void setCommunicationContent(String content);


    /**
     * 获取解析后的内容
     *
     * @return 解析后的内容
     */
    List<File> getAttachment();
    void cleanAttachment();
    void addAttachment(File file);
    void removeAttachment(File file);
    void removeAttachmentAt(int index);
    void setAttachment(List<File> files);

}
