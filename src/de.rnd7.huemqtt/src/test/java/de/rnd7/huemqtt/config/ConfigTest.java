package de.rnd7.huemqtt.config;

import de.rnd7.mqttgateway.config.ConfigParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {
    @Test
    void test_parse() throws IOException {
        try (final InputStream in = ConfigTest.class.getResourceAsStream("config.json")) {
            final Config config = ConfigParser.parse(in, Config.class);
            assertEquals("api-key-here", config.getHue().getApiKey());
            assertEquals("192.168.2.99", config.getHue().getHost());
            assertEquals("tcp://192.168.2.98:1883", config.getMqtt().getUrl());
        }
    }
}
