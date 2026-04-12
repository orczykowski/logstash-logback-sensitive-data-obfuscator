package io.github.orczykowski.logstash.logback.obfuscator;

public class RegexProcessingTimeoutException extends SensitiveDataObfuscatorException {

    RegexProcessingTimeoutException(final int timeoutMillis, final String regularExpression) {
        super("Regex processing timed out after %dms while evaluating pattern '%s'. "
                .formatted(timeoutMillis, regularExpression)
                + "Consider simplifying the pattern or increasing the regexTimeoutMillis value.");
    }
}
