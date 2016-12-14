package org.cv.fis.files.provider;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.cv.fis.files.FileData;
import org.cv.fis.files.LocalFileData;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LocalFileConnection implements FileRepositoryConnection {

    private Path dirPath;

    private LocalFileConnection(String path) {
        dirPath = Paths.get(path);
    }

    private LocalFileConnection(Path path) {
        dirPath = path;
    }

    public static LocalFileConnection open(String path) {
        Preconditions.checkState(!Strings.isNullOrEmpty(path));
        return new LocalFileConnection(path);
    }

    public static LocalFileConnection open(Path path) {
        Preconditions.checkNotNull(path);
        return new LocalFileConnection(path);
    }

    public List<? extends FileData> listFiles() throws SourceException {
        List<LocalFileData> processingLocalFile = new ArrayList<>();
        try {
            if (!Files.exists(dirPath)) {
                //Files.createDirectories(dirPath);
                throw new SourceException("No such directory found");
            }
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dirPath)) {
                for (Path path : ds) {
                    if(Files.isRegularFile(path)) {
                        LocalFileData fileData = new LocalFileData(path.toFile());
                        processingLocalFile.add(fileData);
                    }
                }
            }
        } catch (IOException e) {
            throw new SourceException(String.format("unable to create the '%s' directory ", dirPath));
        }
        return processingLocalFile;
    }

    @Override
    public void close() throws SourceException {
    }
}
