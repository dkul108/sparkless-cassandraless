package org.cv.fis.files;

import org.cv.fis.common.InputStreamProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

//TODO - remove unused methods
public interface FileData extends Serializable, InputStreamProvider {

    String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    String getName();

    @Override
    InputStream getInputStream()  throws IOException;

    String getAbsolutePath();

    String getContentType() throws IOException;

    long getContentLength() throws IOException;

    long getLastModifiedTime();

}
