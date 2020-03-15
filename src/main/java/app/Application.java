package app;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {

    public static void main(String[] args) {
        if (args == null || args.length == 0 || args.length == 1) {
            throw new RuntimeException(
                "pass max number of strings as the first arg and the maximum number of characters in the line");
        }
        FileSortingParams params = parseParams(args);
        FileGenerator generator = new FileGenerator(params);
        Path generated = generator.generateFile();
        if (generated == null) {
            throw new RuntimeException("file was not generated");
        }
        FileSortingProcedure procedure = new FileSortingProcedure(generated);
        if (params.getOutputDirectory() != null) {
            procedure.setResultDir(params.getOutputDirectory());
        }
        procedure.run();
    }

    private static FileSortingParams parseParams(String[] args) {
        long maxNumberOfStrings = Long.parseLong(args[0]);
        long maxCharactersInLine = Long.parseLong(args[1]);
        if (args.length == 3) {
            String outputDir = args[2];
            return new FileSortingParams(maxNumberOfStrings, maxCharactersInLine, Paths.get(outputDir));
        } else {
            return new FileSortingParams(maxNumberOfStrings, maxCharactersInLine);
        }
    }
}
