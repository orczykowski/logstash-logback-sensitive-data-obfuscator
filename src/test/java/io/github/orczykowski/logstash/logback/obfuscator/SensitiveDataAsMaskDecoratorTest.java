package io.github.orczykowski.logstash.logback.obfuscator;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SensitiveDataAsMaskDecoratorTest {
    private static final Set<String> SENSITIVE_FIELDS = Set.of("firstName", "idCardNumber", "mobilePhone", "other");

    SensitiveDataAsMaskDecorator subject;

    @Test
    void shouldThrowExceptionWhenTrySetMaskAsNull() {
        //given
        subject = new SensitiveDataAsMaskDecorator();
        //expect:
        Assertions.assertThrows(IncorrectConfigurationException.class, () -> subject.addMask(null));
    }

    @Test
    void shouldMaskSensitiveDataWhenValuesAreInSquareBrackets() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_SQUARE_BRACKETS.name());
        addSensitiveFields();

        var log = """
                This is log with sensitive data: 
                firstName=[Gustaw], 
                idCardNumber=[CC123456]
                mobilePhone=[+48123123123]
                description=[something]
                other=(sth)""";

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = """
                This is log with sensitive data: 
                firstName=[********], 
                idCardNumber=[********]
                mobilePhone=[********]
                description=[something]
                other=(sth)""";
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldMaskSensitiveDataWhenValuesAreInNormalBrackets() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_BRACKETS.name());
        addSensitiveFields();
        var log = """
                This is log with sensitive data: 
                firstName=(Gustaw) 
                idCardNumber=(CC123456)
                mobilePhone=(+48123123123)
                description=(something)
                other=[sth]""";

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = """
                This is log with sensitive data: 
                firstName=(********)
                idCardNumber=(********)
                mobilePhone=(********)
                description=(something)
                other=[sth]""";
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldMaskSensitiveDataWhenValuesAreInDoubleQuotes() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_DOUBLE_QUOTES.name());
        addSensitiveFields();
        var log = """
                This is log with sensitive data: 
                firstName="Gustaw" 
                idCardNumber="CC123456"
                mobilePhone="+48123123123"
                description="something"
                other=[sth]""";

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = """
                This is log with sensitive data: 
                firstName="********"
                idCardNumber="********"
                mobilePhone="********"
                description="something"
                other=[sth]""";
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldMaskSensitiveDataValuesInJson() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.JSON.name());
        addSensitiveFields();
        var log = asJson(Map.of(
                "firstName", "Gustaw",
                "idCardNumber", "CC123456",
                "mobilePhone", "+48123123123",
                "nonSensitive", "test"));

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = asJson(Map.of(
                "firstName", "********",
                "idCardNumber", "********",
                "mobilePhone", "********",
                "nonSensitive", "test"));
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldUseCustomMask() {
        // given:
        var customMask = "xxxxxx";
        subject = new SensitiveDataAsMaskDecorator();
        subject.addMask(customMask);
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.JSON.name());
        addSensitiveFields();
        var log = asJson(Map.of(
                "firstName", "Gustaw"));

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = asJson(Map.of(
                "firstName", customMask));
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldMaskNestedSensitiveDataValuesInJson() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.JSON.name());
        addSensitiveFields();
        var log = asJson(Map.of(
                "personalData", Map.of(
                        "firstName", "Gustaw",
                        "idCardNumber", "CC123456"),
                "contactInfo", Map.of(
                        "mobilePhone", "+48123123123"),
                "nonSensitive", "test"));

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = asJson(Map.of(
                "personalData", Map.of(
                        "firstName", "********",
                        "idCardNumber", "********"),
                "contactInfo", Map.of(
                        "mobilePhone", "********"),
                "nonSensitive", "test"));
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldIgnoreValueWhenHasNoDecoration() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_SQUARE_BRACKETS.name());
        addSensitiveFields();

        var logMessage = """
                This is some log:
                firstName=(Gustaw), 
                idCardNumber=CC123456""";
        // when:
        var computedMaskLog = (String) subject.mask(null, logMessage);

        // then:
        assertEquals(logMessage, computedMaskLog);
    }

    @Test
    void shouldMaskDataUsingCustomPattern() {
        // given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addCustomPattern("[PROPERTY_NAME]==>'([^']+)'");
        addSensitiveFields();

        var log = """
                This is log with sensitive data: 
                firstName==>'Gustaw'
                idCardNumber==>'CC123456'
                mobilePhone==>'+48123123123'
                description==>'something'
                other=[sth]""";

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = """
                This is log with sensitive data: 
                firstName==>'********'
                idCardNumber==>'********'
                mobilePhone==>'********'
                description==>'something'
                other=[sth]""";
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @Test
    void shouldMaskSensitiveDataWithAllAddedPatterns() {
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.JSON.name());
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_SQUARE_BRACKETS.name());
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_BRACKETS.name());
        addSensitiveFields();

        //and:
        var log = """
                payload={"firstName":"Gustaw"}
                mobilePhone=(+-12-3)
                description="something"
                other=[s-3-h]""";

        // when:
        var computedMaskLog = (String) subject.mask(null, log);

        // then:
        var expectedLogWithMaskedSensitiveData = """
                payload={"firstName":"********"}
                mobilePhone=(********)
                description="something"
                other=[********]""";
        assertEquals(expectedLogWithMaskedSensitiveData, computedMaskLog);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldIgnoreNullEndEmptyString(String str) {
        //given:
        subject = new SensitiveDataAsMaskDecorator();
        subject.addPatternName(SensitiveDataPatternFactory.SensitiveValuePatterns.EQUAL_AND_SQUARE_BRACKETS.name());
        addSensitiveFields();

        //when:
        var result = (String) subject.mask(null, str);

        //then:
        assertEquals(str, result);
    }

    private void addSensitiveFields() {
        SENSITIVE_FIELDS.forEach(subject::addFieldName);
    }

    protected static String asJson(Map<String, Object> map) {
        return new JSONObject(map).toString();
    }
}