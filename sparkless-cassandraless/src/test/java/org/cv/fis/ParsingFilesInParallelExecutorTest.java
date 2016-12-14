package org.cv.fis;


import javafx.util.Pair;
import org.cv.fis.common.Dirs;
import org.cv.fis.common.InputStreamProvider;
import org.cv.fis.files.provider.FileRepositoryConnection;
import org.cv.fis.files.provider.LocalFileConnection;
import org.cv.fis.files.provider.SourceException;
import org.cv.fis.parser.ParseResults;
import org.cv.fis.parser.Parser;
import org.cv.fis.parser.impl.TextParser;
import org.cv.fis.pool.TagCounterExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ParsingFilesInParallelExecutorTest {

    private Path rootPath;
    private ParserTest anotherTest;

    @Before
    public void setUp() throws IOException {
        rootPath = Dirs.createTmpDir();
        anotherTest = new ParserTest();
        Path file = Files.createFile(Paths.get(rootPath.toAbsolutePath() + "/test.txt"));
        anotherTest.writeTestDataToFile(file);
        Path file2 = Files.createFile(Paths.get(rootPath.toAbsolutePath() + "/test2.csv"));
        anotherTest.writeTestDataToFile(file2);
    }

    @After
    public void tearDown() {
        Dirs.deleteDir(rootPath);
    }

    @Test
    public void testExecutor() throws SourceException, InterruptedException {
        ParseResults totals = ParseResults.inst();
        processLocalFilesParsingWithExecutor(rootPath, totals);
        Thread.sleep(1000);
        System.out.println(totals.toString());
        anotherTest.assertParseResults(totals, 6L, 4L);
    }

    public void processLocalFilesParsingWithExecutor(Path rootPath, ParseResults totals) throws SourceException {
        try (FileRepositoryConnection connection = LocalFileConnection.open(rootPath)) {
            List<Pair<Parser, InputStreamProvider>> parseables =
                    connection.
                            listFiles().
                            stream().
                            map(fd -> new Pair<Parser, InputStreamProvider>(TextParser.inst(), fd)).
                            collect(Collectors.toList());

            TagCounterExecutor.
                    completionAware(new CountDownLatch(parseables.size())).
                    execute(totals, parseables);
        }
    }

    public Path getRootPath() {
        return rootPath;
    }
}
