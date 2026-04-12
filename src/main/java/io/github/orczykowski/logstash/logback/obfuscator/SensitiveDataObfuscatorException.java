package io.github.orczykowski.logstash.logback.obfuscator;

public class SensitiveDataObfuscatorException extends RuntimeException {
    SensitiveDataObfuscatorException(String message) {
        super(message);
    }
}
