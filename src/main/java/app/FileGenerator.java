package app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.log4j.Logger;

public class FileGenerator {

  final static Logger log = Logger.getLogger(FileGenerator.class);
  final static String SPACE = " ";
  private final static String words[] = {"the", "a", "one", "some", "any", "man", "woman", "fish", "elephant",
      "unicorn", "loves", "hates", "sees", "knows", "looks for"};
  private final static String FILE_NAME = "input.txt";
  private FileSortingParams params;

  public FileGenerator(FileSortingParams params) {
    this.params = params;
  }

  static int rand() {
    return ThreadLocalRandom.current().nextInt(0, words.length - 1);
  }

  public Path generateFile() {
    Path input;
    Path directory = getDirectory();
    FileUtil.createDirectory(directory);
    if (params.getOutputDirectory() != null) {
      input = Paths.get(params.getOutputDirectory().toAbsolutePath().toString(), FILE_NAME);
    } else {
      input = Paths.get(System.getProperty("user.dir") + File.separator + "test", FILE_NAME);
    }
    if (Files.exists(input)) {
      return input;
    }
    Path created = FileUtil.createFile(input);
    if (created == null) {
      return null;
    }
    fillFileWithData(created);
    return created;
  }

  private Path getDirectory() {
    if (params.getOutputDirectory() != null) {
      return params.getOutputDirectory();
    } else {
      return Paths.get(System.getProperty("user.dir") + File.separator + "test");
    }
  }

  private void fillFileWithData(Path created) {
    try (FileWriter fw = new FileWriter(created.toFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)) {
      long countLines = params.getMaxNumberOfStrings();
      while (countLines > 0) {
        StringBuilder line = new StringBuilder();
        while (line.length() < params.getMaxCharactersInLine()) {
          line.append(words[rand()]);
          line.append(SPACE);
        }
        out.println(line);
        countLines--;
      }
    } catch (Exception e) {
      log.error("error when generating file", e);
    }
  }
}
