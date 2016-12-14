package org.cv.fis.parser.impl;


import org.cv.fis.config.TagCounterConfig;
import org.cv.fis.parser.ParseResults;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class TextReader implements Iterator<String[]>, Closeable, TagCounterConfig.ParseStrategy {

    private final BufferedReader reader;
    private final ParseResults results;
    private String[] nextEntry;

    private TextReader(BufferedReader reader, ParseResults results) {
        this.reader = reader;
        this.results = results;
    }

    public static TextReader of(BufferedReader reader, ParseResults results) {
        return new TextReader(reader, results);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private String[] readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        line = line.trim();
        if (line.length() == 0) {
            return readNext();
        }

        line = preProcessLine(line);
        String[] entry = line.split(DELIMETER);
        return postProcessEntry(entry);
    }


    protected String preProcessLine(String line) {
        if (EXCLUDED_CHARS_PATTERN != null) {
            line = EXCLUDED_CHARS_PATTERN.matcher(line).replaceAll("");
        }
        if (TO_LOWER_CASE) {
            line = line.toLowerCase();
        }
        return line;
    }

    protected String[] postProcessEntry(String[] entry) {
        if (MIN_WORD_LENGTH > 0) {
            return Arrays.
                    stream(entry).
                    filter((word) -> word.length() >= MIN_WORD_LENGTH).
                    collect(Collectors.toList()).
                    toArray(new String[0]);
        }
        return entry;
    }

    @Override
    public boolean hasNext() {
        try {
            nextEntry = readNext();
        } catch (IOException ioe) {
            results.addError(ioe.getMessage());
        }
        return nextEntry != null;
    }

    @Override
    public String[] next() {
        String[] entry = null;
        if (nextEntry != null) {
            entry = nextEntry;
            nextEntry = null;
        } else {
            try {
                entry = readNext();
            } catch (IOException ioe) {
                results.addError(ioe.getMessage());
            }
        }
        return entry;
    }

}
