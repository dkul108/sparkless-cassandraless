package org.cv.fis.common;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

public abstract class Dirs {

    private static final Path tmpDirPath = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath();

    public static Path createTmpDir() {
        Path path = tmpDirPath.resolve(UUID.randomUUID().toString());
        ensureCreateDir(path);
        return path;
    }

    public static void deleteDir(final Path path) {
        if (Files.notExists(path)) {
            return;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            throw new IllegalStateException(String.format("unable to delete the %s directory", path), ex);
        }
    }

    public static void ensureCreateDir(Path dirPath) {
        if (Files.exists(dirPath)) {
            if (!Files.isDirectory(dirPath)) {
                throw new IllegalStateException(
                        String.format("unable to create directory because the '%s' is existing file", dirPath)
                );
            }
        } else {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException ex) {
                throw new IllegalStateException(String.format("unable to create the '%s' directory", dirPath), ex);
            }
        }
    }
}
