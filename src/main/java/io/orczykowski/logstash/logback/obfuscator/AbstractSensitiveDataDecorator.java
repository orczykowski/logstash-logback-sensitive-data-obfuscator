package io.orczykowski.logstash.logback.obfuscator;

import com.fasterxml.jackson.core.JsonStreamContext;
import io.orczykowski.logstash.logback.obfuscator.SensitiveDataPatternFactory.SensitiveValuePatterns;
import net.logstash.logback.mask.ValueMasker;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.orczykowski.logstash.logback.obfuscator.IncorrectConfigurationException.incorrectPatternException;
import static io.orczykowski.logstash.logback.obfuscator.SensitiveDataPatternFactory.PROPERTY_NAME_MARKER;
import static java.util.Objects.isNull;

abstract class AbstractSensitiveDataDecorator implements ValueMasker {
    private static final SensitiveDataPatternFactory patternFactory = new SensitiveDataPatternFactory();
    protected Set<String> patterns = new HashSet<>();
    protected Set<Pattern> sensitiveFieldNamePatterns = new HashSet<>();

    public void addFieldName(final String fieldName) {
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
        if (isBlank(predefinedPatternName) || SensitiveValuePatterns.isValidName(predefinedPatternName)) {
            throw IncorrectConfigurationException.incorrectPredefinedPatternNameException();
        }
        final var pattern = SensitiveValuePatterns.valueOf(predefinedPatternName).getPatternTemplate();
        this.patterns.add(pattern);
    }

    public void addCustomPattern(final String pattern) {
        if (isBlank(pattern) || notContainMarker(pattern)) {
            throw incorrectPatternException();
        }
        this.patterns.add(pattern);
    }

    protected abstract String maskLogMessage(final String str);

    private Set<Pattern> asPropertyNamePatterns(final String propertyName) {
        return patterns.stream()
                .map(pattern -> patternFactory.create(propertyName, pattern))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean notContainMarker(final String pattern) {
        return !pattern.contains(PROPERTY_NAME_MARKER);
    }

    private static boolean isBlank(final String str) {
        return isNull(str) || str.isBlank();
    }

}
