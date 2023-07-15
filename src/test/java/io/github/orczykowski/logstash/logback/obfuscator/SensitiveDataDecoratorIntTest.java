package io.github.orczykowski.logstash.logback.obfuscator;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

class SensitiveDataDecoratorIntTest {

    private final Logger logger = LoggerFactory.getLogger(SensitiveDataDecoratorIntTest.class);
    private final OutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalPrintStream = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalPrintStream);
    }

    @Test
    public void allDefinedSensitiveFieldsInTheConfigurationShouldBeMasked() {
        //given:
        var payload = new JSONObject(Map.of("email", "test@github.io"));
        var logMessageWithSensitiveData = "Something firstName=[test] with payload: %s".formatted(payload);
        logger.info(logMessageWithSensitiveData);

        //when:
        var logOutput = outputStream.toString().trim();

        //then:
        var expectedLod = """
                {"msg":"Something firstName=[*SENSITIVE*DATA*] with payload: {\\"email\\":\\"*SENSITIVE*DATA*\\"}","level":"INFO"}""".trim();
        Assertions.assertEquals(expectedLod, logOutput);
    }
}