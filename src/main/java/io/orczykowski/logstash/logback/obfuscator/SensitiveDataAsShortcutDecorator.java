package io.orczykowski.logstash.logback.obfuscator;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Stream;

public class SensitiveDataAsShortcutDecorator extends AbstractSensitiveDataDecorator {

    protected String maskLogMessage(final String logMessage) {
        var stringBuilder = new StringBuilder(logMessage);
        sensitiveFieldNamePatterns.stream()
                .map(pattern -> pattern.matcher(stringBuilder))
                .map(Matcher::results)
                .map(Stream::toList)
                .flatMap(Collection::stream)
                .forEach((match) -> maskSensitiveData(stringBuilder, match));
        return stringBuilder.toString();
    }
    private String createShortcutFromValue(final String sensitiveData) {
        if (Objects.isNull(sensitiveData) || sensitiveData.isBlank()) {
            return sensitiveData;
        }
        return String.format("%s-%d-%s", sensitiveData.charAt(0), sensitiveData.length(), sensitiveData.charAt(sensitiveData.length() - 1));
    }

    private void maskSensitiveData(final StringBuilder stringBuilder, final MatchResult match) {
        final var sensitiveData = match.group(2);
        final var maskedData = createShortcutFromValue(sensitiveData);

        final var startIndex = match.start(2);
        final var endIndex = match.end(2);

        stringBuilder.replace(startIndex, endIndex, maskedData);
    }
}
