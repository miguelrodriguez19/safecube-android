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
 * FolderTreeToFile root=<dir> output=<file>
 *                  [printFiles=true]
 *                  [printExcludedFiles=false]
 *                  [printSkippedContentLabel=true]
 *                  [printExcludedFolders=false]
 */
public final class FolderTreeToFile {

    // Defaults
    private static boolean printFiles = true;
    private static boolean printExcludedFiles = false;
    private static boolean printSkippedContentLabel = true;
    private static boolean printExcludedFolders = false;

    private static File rootDir;
    private static File outputFile;

    private static final Set<String> EXCLUDED_FOLDERS =
            Set.of(".git", ".idea",
                    "safecube-android/build",
                    "app/build",
                    "core/(?!network)\\w+/build",
                    "core/network/build/(?!generated($|/openapi($|/src($|.*)))).*",
                    "feature/\\w+/build",
                    ".gradle",
                    "\\.kotlin",
                    "gradle/wrapper",
                    "scripts/.build");

    private static final Set<String> EXCLUDED_FILES =
            Set.of(".+.properties", ".DS_Store", "ApiClient.kt", "HttpBearerAuth.kt");

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

        Map<String, String> params = parseKeyValueArgs(args);

        rootDir = Optional.ofNullable(params.get("root"))
                .map(File::new)
                .map(File::getAbsoluteFile)
                .orElseThrow(() -> new IllegalArgumentException("Missing root parameter"));

        outputFile = Optional.ofNullable(params.get("output"))
                .map(FolderTreeToFile::resolveOutputFile)
                .orElseThrow(() -> new IllegalArgumentException("Missing output parameter"));

        printFiles = Boolean.parseBoolean(params.getOrDefault("printFiles", "true"));
        printExcludedFiles = Boolean.parseBoolean(params.getOrDefault("printExcludedFiles", "false"));
        printSkippedContentLabel = Boolean.parseBoolean(params.getOrDefault("printSkippedContentLabel", "true"));
        printExcludedFolders = Boolean.parseBoolean(params.getOrDefault("printExcludedFolders", "false"));
    }

    private static Map<String, String> parseKeyValueArgs(String[] args) {
        Map<String, String> map = new java.util.HashMap<>();

        for (String arg : args) {
            if (!arg.contains("=")) {
                logError("Invalid argument: " + arg + " (expected key=value)");
                continue;
            }

            String[] parts = arg.split("=", 2);
            map.put(parts[0], parts[1]);
        }

        return map;
    }

    private static File resolveOutputFile(String path) {
        File file = new File(path);
        return file.isAbsolute() ? file : new File(rootDir, path);
    }

    private static void exitWithUsage(String message) {
        logError(message);
        logError("""
                Usage:
                  FolderTreeToFile root=<dir> output=<file>
                                   [printFiles=true]
                                   [printExcludedFiles=false]
                                   [printSkippedContentLabel=true]
                                   [printExcludedFolders=false]
                """);
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

            String levelIndicator = isLast ? "└── " : "├── ";

            if (item.isDirectory()) {
                handleDirectory(item, prefix, writer, isLast, levelIndicator);
            } else {
                handleFile(item, prefix, writer, levelIndicator);
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

        if (isExcluded && !printExcludedFolders) {
            if (printSkippedContentLabel) {
                writer.println(prefix + levelIndicator + dir.getName() + "/... # Skipped Content");
            }
            return;
        }

        String compactName = buildCompactPath(dir);
        File deepest = getDeepestCompactDir(dir);

        writer.println(prefix + levelIndicator + compactName + "/");

        String newPrefix = prefix + (isLast ? "    " : "│   ");
        printFolderTree(deepest, newPrefix, writer);
    }

    private static void handleFile(File file, String prefix, PrintWriter writer, String levelIndicator) {
        final boolean isExcluded = EXCLUDED_FILES.stream()
                .anyMatch(pattern -> file.getName().matches(pattern));

        if (!isExcluded || printExcludedFiles) {
            writer.println(prefix + levelIndicator + file.getName());
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

    private Logger() {
    }

    static void logInfo(String message) {
        System.out.println(message);
    }

    static void logError(String message) {
        System.err.println(message);
    }
}




