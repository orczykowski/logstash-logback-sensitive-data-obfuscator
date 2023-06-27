package io.orczykowski.logstash.logback.obfuscator;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.orczykowski.logstash.logback.obfuscator.SensitiveDataPatternFactory.PROPERTY_NAME_MARKER;

public class IncorrectConfigurationException extends RuntimeException {
    private static final String INCORRECT_PATTERN_MESSAGE = """
            Pattern have to be complies with java regexp and have to contains place holder
            %s where in log is sensitive value. The sensitive value must be a group in the sense of regular 
            expressions, i.e. it is surrounded by parentheses""".formatted(PROPERTY_NAME_MARKER);

    private static final String INCORRECT_PREDEFINED_PATTERN_MESSAGE = """
            Unknown name. You can use the following predefined pattern names [%s]"""
            .formatted(Arrays.stream(SensitiveDataPatternFactory.SensitiveValuePatterns.values()).map(Enum::name).collect(Collectors.joining(",")));

    IncorrectConfigurationException(String msg) {
        super(msg);
    }

    static IncorrectConfigurationException incorrectPatternException() {
        return new IncorrectConfigurationException(INCORRECT_PATTERN_MESSAGE);
    }

    static IncorrectConfigurationException incorrectPredefinedPatternNameException() {
        return new IncorrectConfigurationException(INCORRECT_PREDEFINED_PATTERN_MESSAGE);
    }
}
