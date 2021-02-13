package integration;

import com.google.common.eventbus.Subscribe;
import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;
import de.rnd7.mqttgateway.Events;
import de.rnd7.mqttgateway.GwMqttClient;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.config.ConfigMqtt;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MqttSubscriptionExtension implements BeforeEachCallback, AfterEachCallback {
    private final List<Message> messages = new ArrayList<>();
    private final Set<String> topics = new HashSet<>();

    private final HiveMQTestContainerExtension hiveMQ;

    public MqttSubscriptionExtension(final HiveMQTestContainerExtension hiveMQ) {
        this.hiveMQ = hiveMQ;
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        Events.getInstance().syncBus();
        Events.register(this);

        final GwMqttClient client = GwMqttClient.start(new ConfigMqtt()
            .setUrl(String.format("tcp://%s:%s",
                this.hiveMQ.getHost(),
                this.hiveMQ.getMqttPort())));
        awaitConnected(client);

        client.subscribe("#");
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        Events.unregister(this);
    }

    @Subscribe
    public void onMessage(final Message message) {
        this.messages.add(message);
        this.topics.add(message.getTopic());
    }

    private void awaitConnected(final GwMqttClient client) {
        await().atMost(Duration.ofSeconds(2)).until(client::isConnected);
        assertTrue(client.isConnected());
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public Message awaitTopic(final String topic) {
        await().atMost(Duration.ofSeconds(2))
            .until(() -> this.topics.contains(topic));

        return this.messages.stream()
            .filter(m -> m.getTopic().equals(topic))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }
}
