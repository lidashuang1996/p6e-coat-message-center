package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface LogService {

    Map<String, List<String>> create(List<String> recipients, TemplateMessageModel message);


    void update(Map<String, List<String>> list, String result);

}
