package org.cv.fis.parser;


import org.cv.fis.common.InputStreamProvider;

import java.io.IOException;

public class TextParser implements Parser {

    private TextParser(){}

    public static TextParser  inst() {
        return new TextParser();
    }

    @Override
    public void parse(InputStreamProvider inputStreamProvider, ParseResults results) throws IOException {

    }
}
