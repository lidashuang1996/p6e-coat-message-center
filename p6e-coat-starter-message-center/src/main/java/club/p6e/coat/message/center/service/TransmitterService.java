package club.p6e.coat.message.center.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 发报机服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TransmitterService {

    /**
     * 发送消息
     *
     * @param id          编号
     * @param data        参数对象
     * @param language    语言
     * @param recipients  收件人
     * @param attachments 附件对象
     * @return 发送结果
     */
    Map<String, List<String>> push(Integer id, String language, List<String> recipients, Map<String, String> data, List<File> attachments);

}
