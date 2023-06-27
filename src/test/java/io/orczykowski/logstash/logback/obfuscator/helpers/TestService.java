package io.orczykowski.logstash.logback.obfuscator.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestService {
    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    public void testMethod(final String someMessage) {
        log.info(someMessage);
    }
}
