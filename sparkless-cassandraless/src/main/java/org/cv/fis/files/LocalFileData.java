package org.cv.fis.files;


import com.google.common.base.Preconditions;

import java.io.*;
import java.nio.file.Files;

public class LocalFileData implements FileData {

    private File file;

    public LocalFileData(File file) {
        Preconditions.checkNotNull(file);
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throwIfFileNotExist();
        return new FileInputStream(file);
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getContentType() throws IOException {
        throwIfFileNotExist();
        String contentType = Files.probeContentType(file.toPath());
        return contentType == null ? APPLICATION_OCTET_STREAM_VALUE : contentType;
    }

    @Override
    public long getContentLength() throws IOException {
        throwIfFileNotExist();
        return file.length();
    }

    @Override
    public long getLastModifiedTime() {
        return file.lastModified();
    }

    private void throwIfFileNotExist() throws IOException {
        if (!file.exists()) {
            throw new IOException(String.format("the %s file does not exist", file.getName()));
        }
    }

}
