package club.p6e.coat.message.center;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.message.center.service.TransmitterService;
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
    private final TransmitterService transmitterService;

    /**
     * 构造方法初始化
     *
     * @param transmitterService 发报机服务对象
     */
    public Controller(TransmitterService transmitterService) {
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
                    "fun push(...) >>> id/data/language/recipients.",
                    "Request parameter exception."
            );
        }
        final List<String> pRecipients;
        final Map<String, String> pData;
        final List<File> pFiles = new ArrayList<>();
        try {
            pData = new HashMap<>(JsonUtil.fromJsonToMap(data, String.class, String.class));
            pRecipients = new ArrayList<>(JsonUtil.fromJsonToList(recipients, String.class));
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
                    if (!FileUtil.checkFolderExist(out.getParentFile())) {
                        FileUtil.createFolder(out.getParentFile());
                    }
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
