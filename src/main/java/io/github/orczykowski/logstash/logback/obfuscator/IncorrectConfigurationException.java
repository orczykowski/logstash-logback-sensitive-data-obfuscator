package io.github.orczykowski.logstash.logback.obfuscator;

public class IncorrectConfigurationException extends RuntimeException {
    IncorrectConfigurationException(String msg) {
        super(msg);
    }
}
