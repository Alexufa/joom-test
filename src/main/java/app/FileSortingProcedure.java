package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

/**
 * Основная идея алгоритма: разбиваем входной файл на отсортированные файлы, после чего делается рекурсивное слияние
 * файлов
 */
public class FileSortingProcedure {

  final static Logger log = Logger.getLogger(FileSortingProcedure.class);

  private Path inputFile;
  private Path resultDir;
  private Queue<Path> queue;
  private double maxFileSize;
  private long fileNumber = 0;

  public FileSortingProcedure(Path inputFile) {
    this.inputFile = inputFile;
    this.resultDir = Paths.get(System.getProperty("user.dir") + File.separator + "test");
    this.queue = new LinkedBlockingQueue<>();
  }

  public Path run() {
    calculateSizeOfTmpFile();
    FileUtil.createDirectory(resultDir);
    splitMainFile();
    mergeFiles();
    Path result = queue.poll();
    renameFile(result);
    return result;
  }

  private void renameFile(Path result) {
    Path previous = Paths.get(resultDir.toAbsolutePath().toString(), "result.txt");
    if (Files.isRegularFile(previous)) {
      FileUtil.deleteFile(previous);
    }
    FileUtil.renameFile(result, "result.txt");
  }

  private void splitMainFile() {
    try (BufferedReader br = new BufferedReader(new FileReader(inputFile.toFile()))) {
      createSmallFile(br);
    } catch (Exception e) {
      log.error("error when creating file", e);
    }
  }

  private void calculateSizeOfTmpFile() {
    maxFileSize = Runtime.getRuntime().freeMemory() / 3.0;
  }

  private void mergeFiles() {
    RecursiveMerge task = new RecursiveMerge(queue, resultDir);
    ForkJoinPool pool = new ForkJoinPool();
    pool.invoke(task);
  }

  private void createSmallFile(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      Set<String> lines = new TreeSet<>();
      long currSize = 0;
      while (line != null) {
        lines.add(line);
        currSize += getSize(line);
        if (currSize >= maxFileSize) {
          break;
        }
        line = reader.readLine();
      }
      Path file = Paths.get(resultDir.toAbsolutePath().toString(), getFileName());
      Path created = FileUtil.writeLinesToFile(file, lines);
      if (created != null) {
        queue.add(created);
      }
    }
  }

  private long getSize(String line) {
    return 36 + line.length() * 2;
  }

  private String getFileName() {
    return "tmp_" +
        fileNumber++ +
        ".txt";
  }

  public void setResultDir(Path resultDir) {
    this.resultDir = resultDir;
  }
}
