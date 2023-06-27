package io.orczykowski.logstash.logback.obfuscator.helpers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;

public class BaseIntegrationTest {
    protected TestService testService = new TestService();
    protected InMemoryLogAppender inMemoryLogAppender;

    @BeforeEach
    void setup() {
        var logger = (Logger) LoggerFactory.getLogger(TestService.class);
        inMemoryLogAppender = new InMemoryLogAppender();
        inMemoryLogAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.INFO);
        logger.addAppender(inMemoryLogAppender);
        inMemoryLogAppender.start();
    }
}
