package de.rnd7.huemqtt;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class LogExtension implements BeforeEachCallback, AfterEachCallback {
    private ListAppender<ILoggingEvent> appender;
    private final Logger logger;

    public LogExtension(final Class<?> type) {
        this.logger = (Logger) LoggerFactory.getLogger(type);
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) {
        this.appender = new ListAppender<>();
        this.appender.start();
        this.logger.detachAndStopAllAppenders();
        this.logger.addAppender(this.appender);
    }
    
    @Override
    public void afterEach(final ExtensionContext extensionContext) {
        this.logger.detachAndStopAllAppenders();
    }

    public List<ILoggingEvent> getMessages() {
        return this.appender.list;
    }

    public void assertMessages(final String... messages) {
        await().atMost(Duration.ofSeconds(2))
            .until(() -> getMessages().size() == messages.length);
        
        final String[] actual = getMessages().stream()
            .map(Object::toString)
            .toArray(String[]::new);

        assertArrayEquals(messages, actual);
    }
}
