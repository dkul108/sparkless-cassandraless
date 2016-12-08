package org.cv.fis;


import javafx.util.Pair;
import org.cv.fis.common.InputStreamProvider;
import org.cv.fis.files.provider.FileRepositoryConnection;
import org.cv.fis.files.provider.LocalFileConnection;
import org.cv.fis.files.provider.SourceException;
import org.cv.fis.parser.ParseResults;
import org.cv.fis.parser.Parser;
import org.cv.fis.parser.TextParser;
import org.cv.fis.pool.TagCounterExecutor;

import java.util.List;
import java.util.stream.Collectors;

public class TagCounter {

    //or apply Scanner
    public static void main(String ... args) throws SourceException {
        FileRepositoryConnection connection = LocalFileConnection.open(args[0]);

        List<Pair<Parser, InputStreamProvider>> parseables =
                        connection.
                        listFiles().
                        stream().
                        map(fd -> new Pair<Parser, InputStreamProvider>(TextParser.inst(), fd)).
                        collect(Collectors.toList());

        TagCounterExecutor executor = new TagCounterExecutor();
        ParseResults totals = ParseResults.inst();
        executor.execute(totals, parseables);
    }

}
