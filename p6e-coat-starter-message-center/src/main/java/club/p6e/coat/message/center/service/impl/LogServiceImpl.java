package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.service.LogService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class LogServiceImpl implements LogService {


    @Override
    public Map<String, List<String>> create(List<String> recipients, TemplateMessageModel message) {
        return null;
    }

    @Override
    public void update(Map<String, List<String>> list, String result) {

    }


}
