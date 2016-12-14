package org.cv.fis.files.provider;


import org.cv.fis.files.FileData;

import java.util.List;

public interface FileRepositoryConnection extends AutoCloseable {

    @Override
    void close() throws SourceException;

    List<? extends FileData> listFiles() throws SourceException;
}
