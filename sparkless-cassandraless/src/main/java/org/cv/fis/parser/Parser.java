package org.cv.fis.parser;


import org.cv.fis.common.InputStreamProvider;

import java.io.IOException;

public interface Parser {
    void parse(InputStreamProvider inputStreamProvider, ParseResults results) throws IOException;

    default ParseResults parse(InputStreamProvider inputStreamProvider) {
        ParseResults result = ParseResults.inst();
        try {
            parse(inputStreamProvider, result);
        } catch (IOException e) {
            result.addError(e.getMessage());
        }
        return result;
    }
}
