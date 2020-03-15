package app;

import static java.util.Objects.hash;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import org.apache.log4j.Logger;

public class RecursiveMerge extends RecursiveAction {

    final static Logger log = Logger.getLogger(RecursiveMerge.class);

    private Queue<Path> files;
    private Path tmpDir;

    public RecursiveMerge(Queue<Path> queue, Path tmpDir) {
        this.files = queue;
        this.tmpDir = tmpDir;
    }

    @Override
    protected void compute() {
        if (files.size() == 1) {
            return;
        }
        Path first = files.poll();
        if (first == null) {
            return;
        } else {
            Path second = files.poll();
            if (second == null) {
                files.add(first);
                return;
            }
            File result = mergeFiles(first, second);
            files.add(result.toPath());
            FileUtil.deleteFile(first);
            FileUtil.deleteFile(second);
            int processors = Runtime.getRuntime().availableProcessors();
            List<ForkJoinTask> tasks = new ArrayList<>();
            while (processors > 0) {
                tasks.add(new RecursiveMerge(files, tmpDir));
                processors--;
            }
            invokeAll(tasks);
        }
    }

    private File mergeFiles(Path first, Path second) {
        File result;
        try(Scanner sc1 = new Scanner(first, StandardCharsets.UTF_8.name());
            Scanner sc2 = new Scanner(second, StandardCharsets.UTF_8.name())) {
            result = createResultFile(first, second);
            mergeScannersIntoFile(sc1, sc2, result);
        } catch (Exception e) {
            log.error("unable to obtain scanners from file", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    private void mergeScannersIntoFile(Scanner sc1, Scanner sc2, File result) {
        try (FileWriter fw = new FileWriter(result, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            String str1 = null, str2 = null;
            if (sc1.hasNextLine()) {
                str1 = sc1.nextLine();
            }
            if (sc2.hasNextLine()) {
                str2 = sc2.nextLine();
            }
            while (str1 != null || str2 != null) {
                if (str1 == null) {
                    while (str2 != null) {
                        out.println(str2);
                        str2 = sc2.hasNextLine() ? sc2.nextLine() : null;
                    }
                } else if (str2 == null) {
                    while (str1 != null) {
                        out.println(str1);
                        str1 = sc1.hasNextLine() ? sc1.nextLine() : null;
                    }
                } else {
                    int cmp = str1.compareTo(str2);
                    if (cmp < 0) {
                        out.println(str1);
                        str1 = sc1.hasNextLine() ? sc1.nextLine() : null;
                    } else if (cmp > 0) {
                        out.println(str2);
                        str2 = sc2.hasNextLine() ? sc2.nextLine() : null;
                    } else {
                        out.println(str1);
                        out.println(str2);
                        str1 = sc1.hasNextLine() ? sc1.nextLine() : null;
                        str2 = sc2.hasNextLine() ? sc2.nextLine() : null;
                    }
                }
            }
        } catch (IOException e) {
            log.error("error when trying to merge files", e);
            throw new RuntimeException("error when trying to merge files");
        }
    }

    private File createResultFile(Path first, Path second) {
        File result = new File(tmpDir + File.separator + hash(first, second) + ".txt");
        try {
            result.createNewFile();
            return result;
        } catch (IOException e) {
            log.error("error when creating result file", e);
            throw new RuntimeException("error when creating result file");
        }
    }
}
