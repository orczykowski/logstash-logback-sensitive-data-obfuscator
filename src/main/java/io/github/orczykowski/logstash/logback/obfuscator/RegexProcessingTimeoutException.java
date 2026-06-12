package io.github.orczykowski.logstash.logback.obfuscator;

public class RegexProcessingTimeoutException extends SensitiveDataObfuscatorException {
    static final String REGEX_TIMED_OUT_FMT = """
                    Regex processing timed out after %dms while evaluating pattern '%s'.
                    Consider simplifying the pattern or increasing the regexTimeoutMillis value.""";

    RegexProcessingTimeoutException(final int timeoutMillis, final String regularExpression) {
        super(REGEX_TIMED_OUT_FMT.formatted(timeoutMillis, regularExpression));
    }
}
