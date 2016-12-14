package org.cv.fis;


import org.cv.fis.common.Dirs;
import org.cv.fis.files.provider.FileRepositoryConnection;
import org.cv.fis.files.provider.LocalFileConnection;
import org.cv.fis.parser.ParseResults;
import org.cv.fis.parser.impl.TextParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import static org.cv.fis.config.TagCounterConfig.NEW_LINE_CHAR_SEPARATOR;
import static org.cv.fis.config.TagCounterConfig.ParseStrategy.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class ParserTest {

    private Path rootPath;

    @Before
    public void setUp() throws IOException {
        rootPath = Dirs.createTmpDir();
        Path file = Files.createFile(Paths.get(rootPath.toAbsolutePath() + "/test.txt"));
        writeTestDataToFile(file);
    }

    @After
    public void tearDown() {
        Dirs.deleteDir(rootPath);
    }

    @Test
    public void parseTestEntryTest() throws IOException, ParseException {
        try (FileRepositoryConnection connection = LocalFileConnection.open(rootPath)) {
            connection.listFiles().forEach(
                    f -> {
                        TextParser parser = TextParser.inst();
                        ParseResults results = parser.parse(f);
                        assertParseResults(results, 3L, 2L);
                    }
            );
        }
    }

    public void assertParseResults(ParseResults results, Long firstCount, Long secondCount) {
        System.out.println(results.toString());
        results.getStats().forEach(
                (word, stat) -> {
                    assertThat(EXCLUDED_CHARS_PATTERN.matcher(word).find()).isFalse();
                    assertThat(word.length() >= MIN_WORD_LENGTH).isTrue();
                    assertThat(word.contains(DELIMETER)).isFalse();
                    assertThat(word.contains(NEW_LINE_CHAR_SEPARATOR)).isFalse();
                    assertThat(stat > 0);
                }
        );
        assertThat(results.getStats().containsKey("smart") && results.getStats().containsValue(firstCount)).isTrue();
        assertThat(results.getStats().containsKey("ofcourse") && results.getStats().containsValue(secondCount)).isTrue();
    }

    public void writeTestDataToFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        lines.add("Smart.\n , Smart!, Smart?"); //smart - 3
        lines.add(" ");
        lines.add("Of-course., Ofcourse"); //ofcourse - 2
        lines.add("“if”, “the”, “and” etc)");
        lines.add("12313, abc123, 66-route, 66route,).");
        lines.add("    ");
        lines.add("Bottle and Bottles");
        Files.write(file, lines);
    }
}
