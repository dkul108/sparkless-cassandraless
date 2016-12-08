package org.cv.fis.parser;


import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.cv.fis.config.TagCounterConfig.NUMBER_OF_THREADS;

public class ParseResults {

    //TODO: testme!!!
    private volatile Map<String, Long> stats;//TODO: where the f//ck java CopyOnWriteMap ?!
    private Map<String, Long> immutableStats;

    private Collection<String> parseErrors;
    private Collection<String> immutableParseErrors;


    private ParseResults(Map<String, Long> stats, Set<String> parseErrors) {
        this.stats = stats;
        immutableStats = Collections.unmodifiableMap(stats);

        this.parseErrors = parseErrors;
        immutableParseErrors = Collections.unmodifiableSet(parseErrors);
    }

    public static ParseResults inst() {
        return new ParseResults(
                new ConcurrentHashMap<>(2000, 0.85f, NUMBER_OF_THREADS),//TODO : add ConcurrentSkipListMap for sorting
                Sets.newHashSet()
        );
    }

    public ParseResults appendResults(ParseResults results) {
        https://ria101.wordpress.com/2011/12/12/concurrenthashmap-avoid-a-common-misuse/
        if(!results.getStats().isEmpty()) {
            results.getStats().forEach((word, rating) -> addStat(word, rating));
        }
        addErrors(results.getParseErrors());
        return this;
    }

    public ParseResults addStat(String word) {
        Long number = stats.get(word);
        if (number == null) {
            number = 1L;
        }
        addStat(word, number);return this;
    }

    public ParseResults addStat(String word, Long rating) {
        stats.put(word, rating);
        return this;
    }

    public ParseResults addError(String error) {
        parseErrors.add(error);
        return this;
    }

    public ParseResults addErrors(Collection<String> errors) {
        parseErrors.addAll(errors);
        return this;
    }

    public Map<String, Long> getStats() {
        return immutableStats;
    }

    public Collection<String> getParseErrors() {
        return immutableParseErrors;
    }
}
