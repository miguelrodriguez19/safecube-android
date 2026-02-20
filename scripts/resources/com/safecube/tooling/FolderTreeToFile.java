package com.safecube.tooling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.safecube.tooling.Logger.logError;
import static com.safecube.tooling.Logger.logInfo;

/**
 * Command line usage: <br>
 * FolderTreeToFile [rootDir] [outputPath]
 *    [printFiles=true]
 *    [printExcludedFiles=false]
 *    [printExcludedFolders=false]
 */
public final class FolderTreeToFile {

    // Defaults
    private static boolean printFiles = true;
    private static boolean printExcludedFiles = false;
    private static boolean printExcludedFolders = false;

    private static File rootDir;
    private static File outputFile;

    private static final Set<String> EXCLUDED_FOLDERS =
            Set.of(".git", ".idea",
                    "safecube-android/build",
                    "app/build",
                    "core/\\w+/build",
                    "feature/\\w+/build",
                    ".gradle",
                    ".kotlin",
                    "gradle/wrapper",
                    "scripts/.build");

    private static final Set<String> EXCLUDED_FILES =
            Set.of(".+.properties", ".DS_Store");

    private FolderTreeToFile() {
        // utility class
    }

    public static void main(String[] args) {
        parseArgs(args);
        validateRootDir();
        writeTree();
    }

    // ---------------------------------------------------------------------------
    // Argument parsing
    // ---------------------------------------------------------------------------

    private static void parseArgs(String[] args) {
        if (args.length < 2) {
            exitWithUsage("ERR :: Missing required arguments");
        }

        rootDir = new File(args[0]).getAbsoluteFile();
        outputFile = resolveOutputFile(args[1]);

        if (args.length > 2) printFiles = Boolean.parseBoolean(args[2]);
        if (args.length > 3) printExcludedFiles = Boolean.parseBoolean(args[3]);
        if (args.length > 4) printExcludedFolders = Boolean.parseBoolean(args[4]);

        if (args.length > 5) {
            logError("WARN :: %d arguments provided; only 5 are used%n".formatted(args.length));
        }
    }

    private static File resolveOutputFile(String path) {
        File file = new File(path);
        return file.isAbsolute() ? file : new File(rootDir, path);
    }

    private static void exitWithUsage(String message) {
        logError(message);
        logError(
                "Usage:\n"
                        + "  FolderTreeToFile <rootDir> <outputPath> [printFiles=true] "
                        + "[printExcludedFiles=false] [printExcludedFolders=false]");
        System.exit(1);
    }

    // ---------------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------------

    private static void validateRootDir() {
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            logError("ERR :: Invalid root directory: " + rootDir);
            System.exit(2);
        }
    }

    // ---------------------------------------------------------------------------
    // Execution
    // ---------------------------------------------------------------------------

    private static void writeTree() {
        outputFile.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"));

            writer.println("# Package Structure");
            writer.println("Updated: %s%n".formatted(now));

            writer.println("```");
            writer.println(rootDir.getName() + "/");
            printFolderTree(rootDir, "", writer);
            writer.println("```");

            logInfo("Folder tree written to: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            logError("ERR :: Failed to write output file: " + e.getMessage());
            System.exit(3);
        }
    }

    // ---------------------------------------------------------------------------
    // Tree printing
    // ---------------------------------------------------------------------------

    private static void printFolderTree(File folder, String prefix, PrintWriter writer) {
        File[] items = folder.listFiles();
        if (items == null) return;

        Arrays.sort(items, FolderTreeToFile::compareFiles);

        List<File> visibleItems = filterVisible(items);

        for (int i = 0; i < visibleItems.size(); i++) {
            File item = visibleItems.get(i);
            boolean isLast = i == visibleItems.size() - 1;

            writer.print(prefix);
            String levelIndicator = isLast ? "└── " : "├── ";

            if (item.isDirectory()) {
                handleDirectory(item, prefix, writer, isLast, levelIndicator);
            } else {
                handleFile(item, writer, levelIndicator);
            }
        }
    }

    private static int compareFiles(File a, File b) {
        if (a.isDirectory() && !b.isDirectory()) return -1;
        if (!a.isDirectory() && b.isDirectory()) return 1;

        return a.getName().compareToIgnoreCase(b.getName());
    }

    private static List<File> filterVisible(File[] items) {
        List<File> visible = new ArrayList<>();
        for (File item : items) {
            if (item.isDirectory() || printFiles) {
                visible.add(item);
            }
        }
        return visible;
    }

    private static void handleDirectory(
            File dir, String prefix, PrintWriter writer, boolean isLast, String levelIndicator) {

        final boolean isExcluded = EXCLUDED_FOLDERS.stream()
                .anyMatch(suffix -> dir.getAbsolutePath().matches(".*" + suffix + "$"));

        if (isExcluded) {
            if (printExcludedFolders) {
                writer.println(levelIndicator + dir.getName() + "/... # Skipped Content");
            }
            return;
        }

        String compactName = buildCompactPath(dir);
        File deepest = getDeepestCompactDir(dir);

        writer.println(levelIndicator + compactName + "/");

        String newPrefix = prefix + (isLast ? "    " : "│   ");
        printFolderTree(deepest, newPrefix, writer);
    }

    private static void handleFile(File file, PrintWriter writer, String levelIndicator) {
        final boolean isExcluded = EXCLUDED_FILES.stream()
                .anyMatch(pattern -> file.getName().matches(pattern));

        if (isExcluded) {
            if (printExcludedFiles) {
                writer.println(levelIndicator + file.getName());
            }
        } else {
            writer.println(levelIndicator + file.getName());
        }
    }

    private static File getSingleVisibleDirectory(File dir) {
        File[] children = dir.listFiles();
        if (children == null) return null;

        List<File> visible = filterVisible(children);

        if (visible.size() != 1) return null;

        File only = visible.get(0);
        if (!only.isDirectory()) return null;

        boolean excluded = EXCLUDED_FOLDERS.stream()
                .anyMatch(suffix -> only.getAbsolutePath().matches(".*" + suffix + "$"));

        return excluded ? null : only;
    }

    private static String buildCompactPath(File dir) {
        StringBuilder name = new StringBuilder(dir.getName());
        File current = dir;

        while (true) {
            File next = getSingleVisibleDirectory(current);
            if (next == null) break;

            name.append("/").append(next.getName());
            current = next;
        }
        return name.toString();
    }

    private static File getDeepestCompactDir(File dir) {
        File current = dir;

        while (true) {
            File next = getSingleVisibleDirectory(current);
            if (next == null) return current;
            current = next;
        }
    }

}

// ---------------------------------------------------------------------------
// Logger
// ---------------------------------------------------------------------------

class Logger {

    private Logger() {}

    static void logInfo(String message) {
        System.out.println(message);
    }

    static void logError(String message) {
        System.err.println(message);
    }
}




