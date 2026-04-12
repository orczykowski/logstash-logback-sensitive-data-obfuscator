package io.github.orczykowski.logstash.logback.obfuscator;

public class IncorrectConfigurationException extends SensitiveDataObfuscatorException {
    IncorrectConfigurationException(String msg) {
        super(msg);
    }
}
