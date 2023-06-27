package io.orczykowski.logstash.logback.obfuscator;

import io.orczykowski.logstash.logback.obfuscator.helpers.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

class SensitiveDataAsMaskDecoratorIntTest extends BaseIntegrationTest {


    @Test
    void shouldMaskSensitiveData() {
        // given:
        final var log = "------->sadnasdinasd";

        // when:
        testService.testMethod(log);

        // expect:
        inMemoryLogAppender.hasLog(log);

    }
}