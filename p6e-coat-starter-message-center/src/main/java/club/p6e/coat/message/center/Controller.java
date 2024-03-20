package club.p6e.coat.message.center;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.message.center.service.TransmitterService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("")
public class Controller {

    public static String BASE_PATH = "./attachment";

    private final TransmitterService transmitterService;

    public Controller(TransmitterService transmitterService) {
        this.transmitterService = transmitterService;
    }

    @PostMapping("/push")
    public ResultContext push(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "language") String language,
            @RequestParam(value = "recipients") List<String> recipients,
            @RequestParam(value = "data") Map<String, String> data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        final List<File> list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                final MultipartFile file = files.get(i);
                String suffix = FileUtil.getSuffix(file.getOriginalFilename());
                suffix = suffix == null ? "" : suffix;
                final File out = new File(FileUtil.composePath(
                        BASE_PATH,
                        FileUtil.composeFile(String.valueOf(i), suffix)
                ));
                if (!FileUtil.checkFolderExist(out.getParentFile())) {
                    FileUtil.createFolder(out.getParentFile());
                }
                file.transferTo(out);
            } catch (Exception e) {
                throw new ParameterException(this.getClass(), "fun push(...) >>> " + e.getMessage() + ".", "Request parameter exception.");
            }
        }
        return ResultContext.build(transmitterService.push(id, language, recipients, data, list));
    }

}
