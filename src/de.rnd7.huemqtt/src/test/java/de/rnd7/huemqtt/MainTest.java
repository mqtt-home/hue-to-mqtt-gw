package de.rnd7.huemqtt;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.google.common.eventbus.Subscribe;
import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;
import de.rnd7.huemqtt.config.Config;
import de.rnd7.huemqtt.hue.HueService;
import de.rnd7.huemqtt.hue.LightDevice;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.GwMqttClient;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.config.ConfigMqtt;
import io.github.zeroone3010.yahueapi.HueTestExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class MainTest {
    private ListAppender<ILoggingEvent> appender;
    private final Logger appLogger = (Logger) LoggerFactory.getLogger(Main.class);

    private final List<Message> messages = new ArrayList<>();

    @RegisterExtension
    public final HiveMQTestContainerExtension hiveMQ
        = new HiveMQTestContainerExtension("hivemq/hivemq-ce", "2020.6");

    @RegisterExtension
    public final HueTestExtension hue = new HueTestExtension();

    @BeforeEach
    public void setup() throws URISyntaxException {
        this.appender = new ListAppender<>();
        this.appender.start();
        this.appLogger.detachAndStopAllAppenders();
        this.appLogger.addAppender(this.appender);

        Events.getInstance().syncBus();
        Events.register(this);

        final GwMqttClient client = GwMqttClient.start(new ConfigMqtt()
            .setUrl(String.format("tcp://%s:%s",
                this.hiveMQ.getHost(),
                this.hiveMQ.getMqttPort())));
        awaitConnected(client);

        client.subscribe("#");
    }

    @AfterEach
    public void tearDown(){
        Events.unregister(this);
    }

    @Subscribe
    public void onMessage(final Message message) {
        this.messages.add(message);
    }

    private void awaitConnected(final GwMqttClient client) {
        await().atMost(Duration.ofSeconds(2)).until(client::isConnected);
        assertTrue(client.isConnected());
    }

    @Test
    void test_no_config() {
        Main.main(new String[]{});

        assertThat(this.appender.list, hasSize(1));
        final String message = this.appender.list.get(0).getFormattedMessage();
        assertEquals("Expected configuration file as argument", message);
    }

    @Test
    void test_start() throws InterruptedException {
        final Config config = new Config();

        config.getHue()
            .setHost("localhost")
            .setPort(this.hue.getPort())
            .setApiKey(HueTestExtension.API_KEY);

        config.getMqtt().setUrl(String.format("tcp://%s:%s",
            this.hiveMQ.getHost(),
            this.hiveMQ.getMqttPort()));

        new Main(config);

        final List<LightDevice> lights = HueService.get()
            .getDevices()
            .stream()
            .filter(LightDevice.class::isInstance)
            .map(LightDevice.class::cast)
            .collect(Collectors.toList());

        for (final LightDevice light : lights) {
            Events.post(new Message(light.getGetTopic(), "{}"));
        }

        await().atMost(Duration.ofSeconds(2))
            .until(() -> this.messages.size() > lights.size() * 2);
        System.out.println(this.messages);
    }
}
