package org.cv.fis.files.provider;

import java.io.IOException;

public class SourceException extends IOException {

    public SourceException(String message) {
        super(message);
    }

    public SourceException(String message, Throwable cause) {
        super(message, cause);
    }

}

