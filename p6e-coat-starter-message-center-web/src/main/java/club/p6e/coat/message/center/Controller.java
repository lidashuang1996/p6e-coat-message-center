package club.p6e.coat.message.center;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息中心接口
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("")
public class Controller {

    /**
     * 基础的临时附件目录
     */
    public static String BASE_PATH = "./attachment";

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /**
     * 发报机服务对象
     */
    private final MessageCenterService transmitterService;

    /**
     * 构造方法初始化
     *
     * @param transmitterService 发报机服务对象
     */
    public Controller(MessageCenterService transmitterService) {
        this.transmitterService = transmitterService;
    }

    @PostMapping("/push")
    public ResultContext push(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "recipients", required = false) String recipients,
            @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) {
        if (id == null || data == null || language == null || recipients == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun push(...) >>> Request parameter [id/data/language/recipients] exception .",
                    "Request parameter [id/data/language/recipients] exception."
            );
        }
        LOGGER.info("REQUEST PARAM ID >>> {}", id);
        LOGGER.info("REQUEST PARAM DATA >>> {}", data);
        LOGGER.info("REQUEST PARAM LANGUAGE >>> {}", language);
        LOGGER.info("REQUEST PARAM RECIPIENTS >>> {}", recipients);
        LOGGER.info("REQUEST PARAM FILES >>> {}", files);
        final List<String> pRecipients;
        final Map<String, String> pData;
        final List<File> pFiles = new ArrayList<>();
        try {
            final List<String> l = JsonUtil.fromJsonToList(recipients, String.class);
            final Map<String, Object> m = JsonUtil.fromJsonToMap(data, String.class, Object.class);
            pData = new HashMap<>();
            pRecipients = new ArrayList<>();
            if (l != null) {
                pRecipients.addAll(l);
            }
            if (m != null) {
                for (final String key : m.keySet()) {
                    pData.put(key, String.valueOf(m.get(key)));
                }
            }
            if (files != null) {
                int attachmentIndex = 0;
                final String num = GeneratorUtil.uuid();
                for (final MultipartFile file : files) {
                    final String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
                    String name = FileUtil.getName(filename);
                    name = name == null ? "" : name;
                    String suffix = FileUtil.getSuffix(filename);
                    suffix = suffix == null ? "" : suffix;
                    final File out = new File(FileUtil.composePath(
                            BASE_PATH,
                            FileUtil.composePath(
                                    num,
                                    FileUtil.composeFile(name.startsWith("embedded-") ? name : "attachment"
                                            + (attachmentIndex++ == 0 ? "" : ("-" + (attachmentIndex - 1))), suffix)
                            )
                    ));
                    FileUtil.createFolder(out.getParentFile());
                    file.transferTo(out);
                    pFiles.add(out);
                }
            }
        } catch (Exception e) {
            throw new ParameterException(
                    this.getClass(),
                    "fun push(...) >>> " + e.getMessage() + ".",
                    "Request parameter exception."
            );
        }
        LOGGER.info(">>>>> push() <<<<<");
        LOGGER.info("id ::: {}", id);
        LOGGER.info("data ::: {}", pData);
        LOGGER.info("files ::: {}", pFiles);
        LOGGER.info("language ::: {}", language);
        LOGGER.info("recipients ::: {}", pRecipients);
        LOGGER.info(">>>>> push() <<<<<");
        return ResultContext.build(transmitterService.push(id, language, pRecipients, pData, pFiles));
    }

}
