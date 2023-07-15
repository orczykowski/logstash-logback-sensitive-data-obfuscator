package io.github.orczykowski.logstash.logback.obfuscator;

import com.fasterxml.jackson.core.JsonStreamContext;
import net.logstash.logback.mask.ValueMasker;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public abstract class AbstractSensitiveDataDecorator implements ValueMasker {
    private static final SensitiveDataPatternFactory patternFactory = new SensitiveDataPatternFactory();
    protected Set<String> patterns = new HashSet<>();
    protected Set<Pattern> sensitiveFieldNamePatterns = new HashSet<>();

    public void addFieldName(final String fieldName) {
        if (patterns.isEmpty()) {
            throw new IncorrectConfigurationException("""
                    There is no pattern to detecting sensitive data added yet.
                    Make sure the list of field names with sensitive fields is added after the patterns.""");
        }
        final var patterns = asPropertyNamePatterns(fieldName);
        sensitiveFieldNamePatterns.addAll(patterns);
    }

    @Override
    public Object mask(final JsonStreamContext jsonStreamContext, final Object obj) {
        if (obj instanceof CharSequence seq) {
            return maskLogMessage((String) seq);
        }
        return obj;
    }

    public void addPatternName(final String predefinedPatternName) {
        if (validatePatternName(predefinedPatternName)) {
            final var sensitivePatternsNames = String.join(",", SensitiveDataPatternFactory.SensitiveValuePatterns.getSensitivePatternsNames());
            throw new IncorrectConfigurationException("Unknown name. You can use the following predefined pattern names [%s]"
                    .formatted(sensitivePatternsNames));
        }
        final var pattern = SensitiveDataPatternFactory.SensitiveValuePatterns.valueOf(predefinedPatternName).getPatternTemplate();
        this.patterns.add(pattern);
    }

    public void addCustomPattern(final String pattern) {
        if (validatePattern(pattern)) {
            throw new IncorrectConfigurationException("""
                    Pattern have to be complies with java regexp and have to contains place holder
                    %s where in log is sensitive value. The sensitive value must be a group in the sense of regular 
                    expressions, it have to be  surrounded by parentheses""".formatted(SensitiveDataPatternFactory.PROPERTY_NAME_MARKER));
        }
        this.patterns.add(pattern);
    }

    protected abstract String maskLogMessage(final String str);

    private Set<Pattern> asPropertyNamePatterns(final String propertyName) {
        return patterns.stream()
                .map(pattern -> patternFactory.create(propertyName, pattern))
                .collect(Collectors.toUnmodifiableSet());
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
