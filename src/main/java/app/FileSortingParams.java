package app;

import java.nio.file.Path;

public class FileSortingParams {

  private long maxNumberOfStrings;
  private long maxCharactersInLine;
  private Path outputDirectory;

  public FileSortingParams(long maxNumberOfStrings, long maxCharactersInLine, Path outputDirectory) {
    this.maxNumberOfStrings = maxNumberOfStrings;
    this.maxCharactersInLine = maxCharactersInLine;
    this.outputDirectory = outputDirectory;
  }

  public FileSortingParams(long maxNumberOfStrings, long maxCharactersInLine) {
    this.maxNumberOfStrings = maxNumberOfStrings;
    this.maxCharactersInLine = maxCharactersInLine;
  }

  public long getMaxNumberOfStrings() {
    return maxNumberOfStrings;
  }

  public long getMaxCharactersInLine() {
    return maxCharactersInLine;
  }

  public Path getOutputDirectory() {
    return outputDirectory;
  }
}
