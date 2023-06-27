package io.orczykowski.logstash.logback.obfuscator;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class SensitiveDataPatternFactory {
    static final String PROPERTY_NAME_MARKER = "[PROPERTY_NAME]";

    Pattern create(final String propertyName, final String regexpTemplate) {
        final var stringRegexp = regexpTemplate.replace(PROPERTY_NAME_MARKER, "(%s)".formatted(propertyName));
        return Pattern.compile(stringRegexp);
    }

    enum SensitiveValuePatterns {
        JSON("\"[PROPERTY_NAME]\":\"([^\"]*)\""),
        EQUAL_AND_SQUARE_BRACKETS("[PROPERTY_NAME]=\\[([^\\]^\\[]+)\\]"),
        EQUAL_AND_BRACKETS("[PROPERTY_NAME]=\\(([^\\)^\\(]+)\\)"),
        EQUAL_AND_DOUBLE_QUOTES("[PROPERTY_NAME]=\"([^\"]+)\"");
        private final String patternTemplate;

        private static final Set<String> names = Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());

        SensitiveValuePatterns(final String pattern) {
            this.patternTemplate = pattern;
        }

        static Boolean isValidName(final String str) {
            return !names.contains(str);
        }

        String getPatternTemplate() {
            return patternTemplate;
        }
    }
}
