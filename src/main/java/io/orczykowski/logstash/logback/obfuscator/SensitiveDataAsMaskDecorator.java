package io.orczykowski.logstash.logback.obfuscator;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class SensitiveDataAsMaskDecorator extends AbstractSensitiveDataDecorator {
    private String mask = "********";

    public void addMask(final String mask) {
        if (Objects.isNull(mask)) {
            throw new IncorrectConfigurationException("Mask cannot be set as null");
        }
        this.mask = mask;
    }

    protected String maskLogMessage(final String logMessage) {
        var maskedMessage = logMessage;
        for (final Pattern pattern : sensitiveFieldNamePatterns) {
            var matcher = pattern.matcher(maskedMessage);
            maskedMessage = matcher.replaceAll(this::maskSensitiveData);
        }
        return maskedMessage;
    }

    private String maskSensitiveData(final MatchResult matchResult) {
        return matchResult.group(0).replace(matchResult.group(2), mask);
    }

}
