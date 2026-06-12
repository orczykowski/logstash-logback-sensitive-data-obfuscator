package io.github.orczykowski.logstash.logback.obfuscator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractSensitiveDataDecoratorTest {

    TestImplementationSensitiveDataTextDecoratorTest subject;

    @BeforeEach
    void init() {
        subject = new TestImplementationSensitiveDataTextDecoratorTest();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenTrySetCustomPatternAsNullOrBlank(final String prefix) {
        //given:
        final var expectedMessage = AbstractSensitiveDataDecorator.INVALID_CUSTOM_PATERN_MSG;
        //expect:
        var ex = assertThrows(IncorrectConfigurationException.class, () -> subject.addCustomPattern(prefix));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"PROPERTY_NAME", "NAME", "[NAME]", "[PROPERTYNAME]", "[]"})
    void shouldThrowExceptionWhenPlaceholderIsIncorrect(final String incorrectPlaceHolder) {
        //given:
        final var expectedMessage = AbstractSensitiveDataDecorator.INVALID_CUSTOM_PATERN_MSG;
        //expect:
        var ex = assertThrows(IncorrectConfigurationException.class, () -> subject.addCustomPattern(incorrectPlaceHolder));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTryAddFieldNamesBeforePatterns() {
        //given:
        final var expectedMessage = AbstractSensitiveDataDecorator.MISSING_PROPERTY_PATERNS_MSG;
        //expect:
        var ex = assertThrows(IncorrectConfigurationException.class, () -> subject.addFieldName("something"));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @ParameterizedTest
    @EnumSource
    void shouldAddAllPredefinedPatterns(SensitiveDataPatternFactory.SensitiveValuePatterns patternName) {
        //expect:
        Assertions.assertDoesNotThrow(() -> subject.addPatternName(patternName.name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SOMETHING"})
    @NullAndEmptySource
    void shouldThrowExceptionWhenTryAddNonExistingPredefinedPatternName(String str) {
        //expect:
        var ex = assertThrows(IncorrectConfigurationException.class, () -> subject.addPatternName(str));
        Assertions.assertEquals(AbstractSensitiveDataDecorator.UNKNOWN_PATTERN_NAME_FMT.formatted(
                        SensitiveDataPatternFactory.SensitiveValuePatterns.getSensitivePatternsNames()), ex.getMessage());
    }

    @Test
    void shouldHaveDefaultRegexTimeout() {
        assertEquals(AbstractSensitiveDataDecorator.DEFAULT_REGEX_TIMEOUT_MILLIS, subject.getRegexTimeoutMillis());
    }

    @Test
    void shouldSetCustomRegexTimeout() {
        subject.addRegexTimeoutMillis(1000);

        assertEquals(1000, subject.getRegexTimeoutMillis());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -2, -100})
    void shouldThrowExceptionWhenRegexTimeoutIsInvalid(int timeout) {
        var ex = assertThrows(IncorrectConfigurationException.class, () -> subject.addRegexTimeoutMillis(timeout));
        assertEquals(AbstractSensitiveDataDecorator.INVALID_REGEX_TIMEOUT_FMT.formatted(timeout), ex.getMessage());
    }

    @Test
    void shouldAcceptMinusOneAsNoTimeout() {
        subject.addRegexTimeoutMillis(-1);

        assertEquals(-1, subject.getRegexTimeoutMillis());
    }

    static class TestImplementationSensitiveDataTextDecoratorTest extends AbstractSensitiveDataDecorator {
        @Override
        protected String maskLogMessage(final String str) {
            //Not important for this test suite
            return null;
        }
    }
}