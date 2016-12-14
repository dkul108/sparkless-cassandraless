package org.cv.fis.parser.impl;


import org.cv.fis.common.InputStreamProvider;
import org.cv.fis.parser.ParseResults;
import org.cv.fis.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.cv.fis.config.TagCounterConfig.ParseStrategy.DEFAULT_UTF_8_CHARSET;


public class TextParser implements Parser {

    private String charset;

    private TextParser(String charset) {
        this.charset = charset;
    }

    public static TextParser inst() {
        return new TextParser(DEFAULT_UTF_8_CHARSET);
    }

    @Override
    public void parse(InputStreamProvider inputStreamProvider, ParseResults results) throws IOException {
        /**
         * didn't used following approaches commented because we may get some file eg from JMS and may parse with no persisting
         */
        //try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
        //try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

        try (InputStream is = inputStreamProvider.getInputStream()) {
            TextReader rows = TextReader.of(
                    new BufferedReader(new InputStreamReader(is, charset)),
                    results
            );
            while (rows.hasNext()) {
                String[] words = rows.next();
                for(String word : words) {
                    results.addStat(word);
                }
            }
        }
    }

}
