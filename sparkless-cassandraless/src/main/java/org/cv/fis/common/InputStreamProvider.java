package org.cv.fis.common;


import java.io.IOException;
import java.io.InputStream;

public interface InputStreamProvider {
    InputStream getInputStream() throws IOException;
}
