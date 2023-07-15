package io.github.orczykowski.logstash.logback.obfuscator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

        private static final List<String> names = Arrays.stream(values())
                .map(Enum::name).toList();

        SensitiveValuePatterns(final String pattern) {
            this.patternTemplate = pattern;
        }

        static Boolean isValidName(final String str) {
            return !names.contains(str);
        }

        String getPatternTemplate() {
            return patternTemplate;
        }

        static List<String> getSensitivePatternsNames() {
            return names;
        }
    }
}
