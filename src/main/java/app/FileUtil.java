package app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.apache.log4j.Logger;

public class FileUtil {

    final static Logger log = Logger.getLogger(FileUtil.class);

    public static Path createFile(Path path) {
        if (Files.isRegularFile(path)) {
            log.debug("file already exists");
            return path;
        }
        try {
            return Files.createFile(path);
        } catch (IOException e) {
            log.error("error when trying to create file", e);
        }
        return null;
    }

    public static void deleteFile(Path path) {
        if (!Files.isRegularFile(path)) {
            log.debug("path does not match any files");
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("error when trying to delete file", e);
        }
    }

    public static void createDirectory(Path dir) {
        if (Files.isDirectory(dir)) {
            log.debug("directory already exists");
            return;
        }
        try {
            Files.createDirectory(dir);
        } catch (IOException e) {
            log.error("error when trying to create directory");
        }
    }

    public static Path writeLinesToFile(Path file, Set<String> lines) {
        if (lines == null || lines.size() == 0) {
            return null;
        }
        try {
            return Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("error when trying to write lines to the file");
        }
        return null;
    }

    public static void renameFile(Path file, String newFileName) {
        try {
            Files.move(file, file.resolveSibling(newFileName));
        } catch (IOException e) {
            log.error("error when trying to rename file");
        }
    }
}
