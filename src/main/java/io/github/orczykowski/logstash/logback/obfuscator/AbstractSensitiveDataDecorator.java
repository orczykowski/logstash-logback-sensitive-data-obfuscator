package io.github.orczykowski.logstash.logback.obfuscator;

import net.logstash.logback.mask.ValueMasker;
import tools.jackson.core.TokenStreamContext;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

public abstract class AbstractSensitiveDataDecorator implements ValueMasker {
    static final int DEFAULT_REGEX_TIMEOUT_MILLIS = 500;
    static final String INVALID_REGEX_TIMEOUT_FMT = "Regex timeout must be a positive value or -1 (no timeout), got: %d";
    static final String UNKNOWN_PATTERN_NAME_FMT  = "Unknown pattern name. You can use the following predefined pattern names %s";
    static final String INVALID_CUSTOM_PATERN_MSG = """
            Pattern have to be complies with java regexp and have to contains place holder
            %s where in log is sensitive value. The sensitive value must be a group in the sense of regular
            expressions, it has to be surrounded by parentheses.""".formatted(SensitiveDataPatternFactory.PROPERTY_NAME_MARKER);
    static final String MISSING_PROPERTY_PATERNS_MSG = """
            There is no pattern to detecting sensitive data added yet.
            Make sure the list of field names with sensitive fields is added after the patterns.""";

    private static final SensitiveDataPatternFactory patternFactory = new SensitiveDataPatternFactory();

    protected final Set<String> patterns = new LinkedHashSet<>();
    protected final Set<Pattern> sensitiveFieldNamePatterns = new LinkedHashSet<>();
    private int regexTimeoutMillis = DEFAULT_REGEX_TIMEOUT_MILLIS;

    public void addRegexTimeoutMillis(final int timeoutMillis) {
        if (timeoutMillis == 0 || timeoutMillis < TimeoutRegexCharSequence.NO_TIMEOUT) {
            throw new IncorrectConfigurationException(INVALID_REGEX_TIMEOUT_FMT.formatted(timeoutMillis));
        }
        this.regexTimeoutMillis = timeoutMillis;
    }

    /**
     * Alternate configuration method for {@link #addRegexTimeoutMillis(int)}
     * to work around Spring Boot classloader issues where Logback's Joran
     * fails to convert String to int.
     * @param  timeoutMillis   regex timeout in milliseconds
     */
    public void addRegexTimeoutSpec(final String timeoutMillis) {
        this.addRegexTimeoutMillis(Integer.parseInt(timeoutMillis));
    }

    protected int getRegexTimeoutMillis() {
        return regexTimeoutMillis;
    }

    protected Matcher matcherWithTimeout(final Pattern pattern, final CharSequence input) {
        if (regexTimeoutMillis == TimeoutRegexCharSequence.NO_TIMEOUT) {
            return pattern.matcher(input);
        }
        return new TimeoutRegexCharSequence(input, regexTimeoutMillis, pattern).matcher();
    }

    public void addFieldName(final String fieldName) {
        if (patterns.isEmpty()) {
            throw new IncorrectConfigurationException(MISSING_PROPERTY_PATERNS_MSG);
        }
        final var patterns = asPropertyNamePatterns(fieldName);
        sensitiveFieldNamePatterns.addAll(patterns);
    }

    @Override
    public Object mask(final TokenStreamContext jsonStreamContext, final Object obj) {
        if (obj instanceof CharSequence seq) {
            return maskLogMessage((String) seq);
        }
        return obj;
    }

    public void addPatternName(final String predefinedPatternName) {
        if (validatePatternName(predefinedPatternName)) {
            throw new IncorrectConfigurationException(UNKNOWN_PATTERN_NAME_FMT.formatted(
                                SensitiveDataPatternFactory.SensitiveValuePatterns.getSensitivePatternsNames()));
        }
        final var pattern = SensitiveDataPatternFactory.SensitiveValuePatterns.valueOf(predefinedPatternName).getPatternTemplate();
        this.patterns.add(pattern);
    }

    public void addCustomPattern(final String pattern) {
        if (validatePattern(pattern)) {
            throw new IncorrectConfigurationException(INVALID_CUSTOM_PATERN_MSG);
        }
        this.patterns.add(pattern);
    }

    protected abstract String maskLogMessage(final String str);

    private Collection<Pattern> asPropertyNamePatterns(final String propertyName) {
        return patterns.stream().map(pattern -> patternFactory.create(propertyName, pattern)).toList();
    }

    private boolean validatePattern(final String pattern) {
        return isBlank(pattern) || notContainMarker(pattern);
    }

    private boolean validatePatternName(final String predefinedPatternName) {
        return isBlank(predefinedPatternName) || SensitiveDataPatternFactory.SensitiveValuePatterns.isValidName(predefinedPatternName);
    }

    private static boolean notContainMarker(final String pattern) {
        return !pattern.contains(SensitiveDataPatternFactory.PROPERTY_NAME_MARKER);
    }

    private static boolean isBlank(final String str) {
        return isNull(str) || str.isBlank();
    }
}
