package club.p6e.coat.message.center.model;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateMessageModel extends TemplateModel {

    Map<String, String> getCommunicationParam();
    void setCommunicationParam(Map<String, String> content);
    String getCommunicationTitle();
    void setCommunicationTitle(String title);
    String getCommunicationContent();
    void setCommunicationContent(String content);
    List<File> getAttachment();
    void cleanAttachment();
    void addAttachment(File file);
    void removeAttachment(File file);
    void removeAttachmentAt(int index);
    void setAttachment(List<File> files);

}
