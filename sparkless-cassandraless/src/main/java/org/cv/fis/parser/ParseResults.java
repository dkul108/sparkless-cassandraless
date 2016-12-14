package org.cv.fis.parser;


import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.cv.fis.config.TagCounterConfig.NEW_LINE_CHAR_SEPARATOR;
import static org.cv.fis.config.TagCounterConfig.NUMBER_OF_THREADS;
import static org.cv.fis.config.TagCounterConfig.ParseStrategy.DELIMETER;

public class ParseResults {

    private volatile Map<String, Long> stats;
    private volatile Map<String, Long> immutableStats;

    private volatile Collection<String> parseErrors;
    private volatile Collection<String> immutableParseErrors;


    private ParseResults(Map<String, Long> stats, Set<String> parseErrors) {
        this.stats = stats;
        immutableStats = Collections.unmodifiableMap(stats);

        this.parseErrors = parseErrors;
        immutableParseErrors = Collections.unmodifiableSet(parseErrors);
    }

    public static ParseResults inst() {
        return new ParseResults(
                new ConcurrentHashMap<>(2000, 0.85f, NUMBER_OF_THREADS),
                Sets.newCopyOnWriteArraySet()
        );
    }

    public ParseResults appendResults(ParseResults results) {
        if (!results.getStats().isEmpty()) {
            results.getStats().forEach((word, rating) -> addStat(word, rating));
        }
        addErrors(results.getParseErrors());
        return this;
    }

    public ParseResults addStat(String word) {
        Long number = stats.get(word);
        number = getWordTotalCount(1L, number);
        stats.put(word, number);
        return this;
    }

    private ParseResults addStat(String word, Long rating) {
        Long number = stats.get(word);
        number = getWordTotalCount(rating, number);
        stats.put(word, number);
        return this;
    }

    private Long getWordTotalCount(Long rating, Long number) {
        if (number == null) {
            number = rating;
        } else {
            number += rating;
        }
        return number;
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

    private String toStatsString() {
        return immutableStats.
                entrySet().
                stream().
                sorted(Map.Entry.<String, Long>comparingByValue().reversed()).
                limit(10).
                map(entry -> entry.getKey() + " - " + entry.getValue()).
                collect(Collectors.joining(DELIMETER + NEW_LINE_CHAR_SEPARATOR));
    }

    private String toErrorsString() {
        return parseErrors.
                stream().
                collect(Collectors.joining(DELIMETER + NEW_LINE_CHAR_SEPARATOR));
    }

    public String toString() {
        StringBuilder report = new StringBuilder(300);
        report.
                append(NEW_LINE_CHAR_SEPARATOR + "Stats:" + DELIMETER + NEW_LINE_CHAR_SEPARATOR + NEW_LINE_CHAR_SEPARATOR).
                append(toStatsString()).
                append(NEW_LINE_CHAR_SEPARATOR + DELIMETER + NEW_LINE_CHAR_SEPARATOR + "Errors:" + NEW_LINE_CHAR_SEPARATOR).
                append(toErrorsString());
        return report.toString();
    }
}
