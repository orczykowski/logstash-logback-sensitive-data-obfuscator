package io.orczykowski.logstash.logback.obfuscator.helpers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;

public class InMemoryLogAppender extends ListAppender<ILoggingEvent> {

    public void hasLog(final String msg) {
        var loggerName = TestService.class.getCanonicalName();
        final var hasLog = this.list.stream()
                .anyMatch(event -> event.getLoggerName()
                        .equals(loggerName) && event.getLevel().equals(Level.INFO) && event.getMessage().equals(msg));
        Assertions.assertTrue(hasLog);
    }
}

