package com.example.haus.util;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    private FileUtil() {
    }

    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));

    private static final Path RESOURCES_PATH = CURRENT_FOLDER.resolve(Paths.get("src/main/resources"));

    /**
     * Lấy ra dữ liệu file ở trong thư mục resources theo đường dẫn Example:
     * getBytesFileByPath("upload/xxx/fileName.xxx")
     *
     * @param pathFile
     *            - đường dẫn file cần lấy (trong phạm vi folder resources)
     *
     * @return byte[]
     */
    @SneakyThrows
    public static byte[] getBytesFileByPath(String pathFile) throws IOException {
        Path path = RESOURCES_PATH.resolve(pathFile);
        return Files.readAllBytes(path);
    }
}