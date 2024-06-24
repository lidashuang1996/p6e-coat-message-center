package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.TemplateMessageModel;

import java.util.List;
import java.util.Map;

/**
 * 日志服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LogService {

    /**
     * 创建
     *
     * @param message    模板消息模型
     * @param recipients 收件人列表
     * @return 执行结果
     */
    Map<String, List<String>> create(List<String> recipients, TemplateMessageModel message);


    /**
     * 修改
     *
     * @param list   日志对象列表
     * @param result 日志回调结果
     */
    void update(Map<String, List<String>> list, String result);

}
