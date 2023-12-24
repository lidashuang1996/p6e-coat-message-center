package club.p6e.coat.message.center;

import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.model.TemplateModel;
import club.p6e.coat.message.center.service.TemplateParserService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class TemplateFactory {

    private final TemplateParserService parser;
    private final DataSourceRepository dataSourceFactory;
    private final Map<String, TemplateModel> cache = new ConcurrentHashMap<>();

    public TemplateFactory(TemplateParserService parser, DataSourceRepository dataSourceFactory) {
        this.parser = parser;
        this.dataSourceFactory = dataSourceFactory;
    }

    public TemplateModel executeReadData(Integer id) {
        TemplateModel result = executeReadCache(id);
        if (result == null) {
            result = executeReadDatabase(id);
            if (result != null) {
                cache.put("ID:" + id, result);
            }
        }
        return result;
    }

    public TemplateModel executeReadData(String mark, String language) {
        TemplateModel result = executeReadCache(mark, language);
        if (result == null) {
            result = executeReadDatabase(mark, language);
            if (result != null) {
                cache.put("ML:" + mark + "_" + language, result);
            }
        }
        return result;
    }

    public TemplateModel executeReadCache(Integer id) {
        return cache.get("ID:" + id);
    }

    public TemplateModel executeReadCache(String mark, String language) {
        return cache.get("ML:" + mark + "_" + language);
    }

    public TemplateModel executeReadDatabase(Integer id) {
        return dataSourceFactory.getTemplateData(id);
    }

    public TemplateModel executeReadDatabase(String mark, String language) {
        return dataSourceFactory.getTemplateData(mark, language);
    }

    public TemplateModel get(Integer id) {
        final TemplateModel model = executeReadData(id);
        if (model == null) {
            throw new RuntimeException();
        } else {
            return model;
        }
    }

    public TemplateModel get(String mark, String language) {
        final TemplateModel model = executeReadData(mark, language);
        if (model == null) {
            throw new RuntimeException();
        } else {
            return model;
        }
    }

    public TemplateMessageModel getModel(String mark) {
        return getModel(get(mark, null), null, null);
    }

    public TemplateMessageModel getModel(String mark, String language) {
        return getModel(get(mark, language), null, null);
    }

    public TemplateMessageModel getModel(Integer id, Map<String, String> data) {
        return getModel(get(id), data, null);
    }

    public TemplateMessageModel getModel(String mark, String language, Map<String, String> data) {
        return getModel(get(mark, language), data, null);
    }

    public TemplateMessageModel getModel(Integer id, Map<String, String> data, List<File> files) {
        return getModel(get(id), data, files);
    }

    public TemplateMessageModel getModel(String mark, String language, Map<String, String> data, List<File> files) {
        return getModel(get(mark, language), data, files);
    }

    public TemplateMessageModel getModel(TemplateModel model, Map<String, String> data, List<File> files) {
        final TemplateMessageModel ctm = parser.execute(model, data);
        if (files != null) {
            ctm.setAttachment(files);
        }
        return ctm;
    }

}
