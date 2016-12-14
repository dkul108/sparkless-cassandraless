package org.cv.fis.pool;


import javafx.util.Pair;
import org.cv.fis.common.InputStreamProvider;
import org.cv.fis.parser.ParseResults;
import org.cv.fis.parser.Parser;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.cv.fis.config.TagCounterConfig.NUMBER_OF_THREADS;

public class TagCounterExecutor {

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private CountDownLatch finishSignal;

    private TagCounterExecutor(CountDownLatch finishSignal) {
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        this.finishSignal = finishSignal;
    }

    public static TagCounterExecutor completionAware(CountDownLatch finishSignal) {
        return new TagCounterExecutor(finishSignal);
    }


    public void execute(ParseResults totalResult, Collection<Pair<Parser, InputStreamProvider>> parseables) {
        parseables.forEach(
                parseable ->
                        supplyAsync(
                                parseable.getKey(),
                                parseable.getValue()
                        ).
                        thenApplyAsync(
                                totalResult::appendResults
                        ).thenAccept(
                                (parseResults)->finishSignal.countDown()
                        )
        );
    }

    private CompletableFuture<ParseResults> supplyAsync(Parser parser, InputStreamProvider inputStreamProvider) {
        return CompletableFuture.supplyAsync(
                () -> parser.parse(inputStreamProvider),
                executorService
        ).exceptionally(
                throwable -> {
                    throwable.printStackTrace(System.err);
                    return ParseResults.inst().addError(throwable.getMessage());
                }
        );
    }

}
