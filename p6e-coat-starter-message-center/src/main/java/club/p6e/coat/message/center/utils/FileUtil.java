package club.p6e.coat.message.center.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 文件帮助类
 *
 * @author lidashuang
 * @version 1.0
 */
public final class FileUtil {

    /**
     * 文件连接符号
     */
    private static final String FILE_CONNECT_CHAR = ".";

    /**
     * 路径连接符号
     */
    private static final String PATH_CONNECT_CHAR = "/";
    private static final String PATH_OPPOSE_CONNECT_CHAR = "\\\\";

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 验证文件夹是否存在
     *
     * @param folder 文件夹对象
     * @return 文件夹是否存在结果
     */
    @SuppressWarnings("ALL")
    public static boolean checkFolderExist(File folder) {
        return folder != null && folder.exists() && folder.isDirectory();
    }

    /**
     * @param folderPath 文件夹路径
     * @return 文件夹是否存在结果
     */
    @SuppressWarnings("ALL")
    public static boolean checkFolderExist(String folderPath) {
        return folderPath != null && !folderPath.isEmpty() && checkFolderExist(new File(folderPath));
    }

    /**
     * 创建文件夹
     *
     * @param folder 文件夹对象
     */
    @SuppressWarnings("ALL")
    public static boolean createFolder(File folder) {
        return createFolder(folder, false);
    }

    /**
     * 创建文件夹
     *
     * @param folder      文件夹对象
     * @param deleteExist 是否删除存在的文件夹
     */
    @SuppressWarnings("ALL")
    public static boolean createFolder(File folder, boolean deleteExist) {
        if (folder == null) {
            return false;
        }
        final String absolutePath = folder.getAbsolutePath();
        if (folder.exists()) {
            LOGGER.debug("[ CreateFolder ] => " + absolutePath + " exists !");
            if (deleteExist) {
                LOGGER.debug("[ CreateFolder ] => " + absolutePath + " exists >>> need delete !");
                if (deleteFolder(folder)) {
                    LOGGER.debug("[ CreateFolder ] => " + absolutePath + " delete >>> success !");
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        if (folder.mkdirs()) {
            LOGGER.debug("[ CreateFolder ] => " + absolutePath + " mkdirs >>> success !");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建文件夹
     *
     * @param folderPath 文件夹路径
     */
    @SuppressWarnings("ALL")
    public static boolean createFolder(String folderPath) {
        return createFolder(folderPath, false);
    }

    /**
     * 创建文件夹
     *
     * @param folderPath  文件夹路径
     * @param deleteExist 是否删除存在的文件夹
     */
    @SuppressWarnings("ALL")
    public static boolean createFolder(String folderPath, boolean deleteExist) {
        if (folderPath == null) {
            return false;
        } else {
            return createFolder(new File(folderPath), deleteExist);
        }
    }

    /**
     * 删除文件夹
     *
     * @param folder 文件夹对象
     */
    @SuppressWarnings("ALL")
    public static boolean deleteFolder(File folder) {
        if (folder == null) {
            return false;
        }
        if (folder.isDirectory()) {
            boolean result = true;
            LOGGER.debug("[ DeleteFolder ] => " + folder.getAbsolutePath() + " >>> [START]");
            final File[] files = folder.listFiles();
            if (files != null) {
                LOGGER.debug("[ DeleteFolder ] => " + folder.getAbsolutePath() + " catalogue (" + files.length + ")");
                for (final File f : files) {
                    if (f.isFile()) {
                        if (!deleteFile(f)) {
                            result = false;
                        }
                    } else if (f.isDirectory()) {
                        if (!deleteFolder(f)) {
                            result = false;
                        }
                    }
                }
                LOGGER.debug("[ DeleteFolder ] => " + folder.getAbsolutePath() + " delete >>> " + folder.delete());
            }
            LOGGER.debug("[ DeleteFolder ] => " + folder.getAbsolutePath() + " >>> [END]");
            return result;
        } else {
            LOGGER.debug("[ DeleteFolder ] ERROR => " + folder.getAbsolutePath() + " is not folder.");
            return false;
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹路径
     */
    @SuppressWarnings("ALL")
    public static boolean deleteFolder(String folderPath) {
        if (folderPath == null) {
            return false;
        } else {
            return deleteFolder(new File(folderPath));
        }
    }

    /**
     * 读取文件夹内容
     *
     * @param folderPath 文件夹路径
     * @return 读取文件夹的内容
     */
    @SuppressWarnings("ALL")
    public static File[] readFolder(String folderPath) {
        return readFolder(folderPath, null);
    }

    /**
     * 读取文件夹内容
     *
     * @param folderPath 文件夹路径
     * @param predicate  过滤器断言
     * @return 读取文件夹的内容
     */
    @SuppressWarnings("ALL")
    public static File[] readFolder(String folderPath, Predicate<? super File> predicate) {
        if (checkFolderExist(folderPath)) {
            return readFolder(new File(folderPath), predicate);
        } else {
            return new File[0];
        }
    }

    /**
     * 读取文件夹
     *
     * @param folder 文件夹对象
     * @return 文件列表
     */
    @SuppressWarnings("ALL")
    public static File[] readFolder(File folder) {
        return readFolder(folder, null);
    }

    /**
     * 读取文件夹
     *
     * @param folder    文件夹对象
     * @param predicate 过滤器断言
     * @return 文件列表
     */
    @SuppressWarnings("ALL")
    public static File[] readFolder(File folder, Predicate<? super File> predicate) {
        if (folder != null && folder.isDirectory()) {
            final File[] files = folder.listFiles();
            if (files != null && files.length > 0) {
                if (predicate == null) {
                    return files;
                } else {
                    return Arrays.stream(files).filter(predicate).toList().toArray(new File[0]);
                }
            }
        }
        return new File[0];
    }


    /**
     * 验证文件是否存在
     *
     * @param file 文件对象
     * @return 文件是否存在结果
     */
    @SuppressWarnings("ALL")
    public static boolean checkFileExist(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * @param filePath 文件路径
     * @return 文件是否存在结果
     */
    @SuppressWarnings("ALL")
    public static boolean checkFileExist(String filePath) {
        return filePath != null && !filePath.isEmpty() && checkFileExist(new File(filePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件对象
     * @return 删除操作结果
     */
    @SuppressWarnings("ALL")
    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        } else {
            if (file.isFile()) {
                final boolean result = file.delete();
                LOGGER.debug("[ DeleteFile ] => " + file.getAbsolutePath() + " delete >>> " + result);
                return result;
            } else {
                LOGGER.debug("[ DeleteFile ] ERROR => " + file.getAbsolutePath() + " is not file.");
                return false;
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 删除操作结果
     */
    @SuppressWarnings("ALL")
    public static boolean deleteFile(String filePath) {
        return filePath != null && !filePath.isEmpty() && deleteFile(new File(filePath));
    }

    /**
     * 文件拼接
     *
     * @param left  文件名称
     * @param right 文件后缀
     * @return 拼接后的文件
     */
    @SuppressWarnings("ALL")
    public static String composeFile(String name, String suffix) {
        if (name == null
                || suffix == null
                || name.isEmpty()
                || suffix.isEmpty()) {
            return null;
        } else {
            return name + FILE_CONNECT_CHAR + suffix;
        }
    }

    /**
     * 文件路径拼接
     *
     * @param left  拼接左边
     * @param right 拼接右边
     * @return 拼接后的文件路径
     */
    @SuppressWarnings("ALL")
    public static String composePath(String left, String right) {
        if (left == null || right == null) {
            return null;
        } else {
            final StringBuilder result = new StringBuilder();
            if (left.endsWith(PATH_CONNECT_CHAR)) {
                result.append(left, 0, left.length() - 1);
            } else {
                result.append(left);
            }
            result.append(PATH_CONNECT_CHAR);
            if (right.startsWith(PATH_CONNECT_CHAR)) {
                result.append(right, 1, right.length());
            } else {
                result.append(right);
            }
            return result.toString();
        }
    }

    /**
     * 路径转换为绝对路径
     *
     * @param path 待转换路径
     * @return 转换为绝对路径
     */
    @SuppressWarnings("ALL")
    public static String convertAbsolutePath(String path) {
        if (path == null) {
            return null;
        } else {
            if (path.startsWith(PATH_CONNECT_CHAR)) {
                return path;
            } else {
                return PATH_CONNECT_CHAR + path;
            }
        }
    }

    /**
     * 生成唯一的文件名称
     *
     * @return 文件名称
     */
    @SuppressWarnings("ALL")
    public static String generateName() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(6, true, false);
    }

    /**
     * 获取文件后缀
     *
     * @param content 文件名称
     * @return 文件后缀
     */
    @SuppressWarnings("ALL")
    public static String getSuffix(String content) {
        if (content != null && !content.isEmpty()) {
            final StringBuilder suffix = new StringBuilder();
            for (int i = content.length() - 1; i >= 0; i--) {
                final String ch = String.valueOf(content.charAt(i));
                if (FILE_CONNECT_CHAR.equals(ch)) {
                    return suffix.toString();
                } else {
                    suffix.insert(0, ch);
                }
            }
        }
        return null;
    }

    /**
     * 获取文件的长度
     *
     * @param files 文件对象
     * @return 文件的长度
     */
    @SuppressWarnings("ALL")
    public static long obtainFileLength(File... files) {
        long length = 0;
        if (files == null || files.length == 0) {
            return 0L;
        } else {
            for (final File file : files) {
                if (checkFileExist(file)) {
                    length += file.length();
                }
            }
        }
        return length;
    }

    /**
     * 获取文件名称
     *
     * @param content 内容
     * @return 文件名称
     */
    @SuppressWarnings("ALL")
    public static String name(String content) {
        boolean bool = false;
        final StringBuilder sb = new StringBuilder();
        for (int j = content.length() - 1; j >= 0; j--) {
            final String ch = String.valueOf(content.charAt(j));
            if (FILE_CONNECT_CHAR.equals(ch)) {
                bool = true;
                sb.insert(0, ch);
            } else if (PATH_CONNECT_CHAR.equals(ch)
                    || PATH_OPPOSE_CONNECT_CHAR.equals(ch)) {
                return sb.toString();
            } else {
                sb.insert(0, ch);
            }
        }
        return bool && sb.length() > 0 && !FILE_CONNECT_CHAR.equals(String.valueOf(sb.charAt(0))) ? sb.toString() : null;
    }


    /**
     * 获取文件路径
     *
     * @param content 内容
     * @return 文件路径
     */
    @SuppressWarnings("ALL")
    public static String path(String content) {
        content = content
                .replaceAll(FILE_CONNECT_CHAR + FILE_CONNECT_CHAR + PATH_CONNECT_CHAR, "")
                .replaceAll(FILE_CONNECT_CHAR + FILE_CONNECT_CHAR + PATH_OPPOSE_CONNECT_CHAR, "")
                .replaceAll(FILE_CONNECT_CHAR + PATH_CONNECT_CHAR, "")
                .replaceAll(FILE_CONNECT_CHAR + PATH_OPPOSE_CONNECT_CHAR, "")
                .replaceAll(PATH_CONNECT_CHAR + PATH_CONNECT_CHAR, "")
                .replaceAll(PATH_OPPOSE_CONNECT_CHAR + PATH_OPPOSE_CONNECT_CHAR, "");
        boolean bool = (name(content) == null);
        final StringBuilder sb = new StringBuilder();
        for (int j = content.length() - 1; j >= 0; j--) {
            final String ch = String.valueOf(content.charAt(j));
            if (!bool && PATH_CONNECT_CHAR.equals(ch)) {
                bool = true;
            } else if (bool) {
                sb.insert(0, ch);
            }
        }
        return sb.toString();
    }

}
