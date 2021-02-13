package de.rnd7.huemqtt;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @RegisterExtension
    public final LogExtension logs = new LogExtension(Main.class);

    @Test
    void test_no_config() {
        Main.main(new String[]{});

        final List<ILoggingEvent> messages = this.logs.getMessages();
        assertThat(messages, hasSize(1));
        final String message = messages.get(0).getFormattedMessage();
        assertEquals("Expected configuration file as argument", message);
    }

}
