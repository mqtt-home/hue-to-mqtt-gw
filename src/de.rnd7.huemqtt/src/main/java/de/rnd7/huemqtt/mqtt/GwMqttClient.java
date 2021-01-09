package de.rnd7.huemqtt.mqtt;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rnd7.huemqtt.config.Config;
import de.rnd7.huemqtt.config.ConfigMqtt;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GwMqttClient {
    private static final int QOS = 2;
    private static final String CLIENT_ID = "hue-mqtt";

    private static final Logger LOGGER = LoggerFactory.getLogger(GwMqttClient.class);

    private final MemoryPersistence persistence = new MemoryPersistence();
    private final Object mutex = new Object();
    private final ConfigMqtt config;
    private final EventBus eventBus;

    private Optional<MqttClient> client;
    private final Gson gson = new GsonBuilder()
        .create();

    private final Map<String, String> messageCache = new HashMap<>();

    public GwMqttClient(final Config config, final EventBus eventBus) {
        this.config = config.getMqtt();
        this.eventBus = eventBus;
        this.client = this.connect();

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(this::reconnect, 30, 30, TimeUnit.SECONDS);
    }

    private Optional<MqttClient> connect() {
        try {
            LOGGER.info("Connecting MQTT client");
            final MqttClient result = new MqttClient(this.config.getUrl(),
                this.config.getClientId().orElse(CLIENT_ID),
                this.persistence);

            final MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            config.getUsername().ifPresent(connOpts::setUserName);
            config.getPassword().map(String::toCharArray).ifPresent(connOpts::setPassword);

            result.connect(connOpts);

            LOGGER.info("Connected MQTT");

            result.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(final Throwable cause) {
                    LOGGER.error(cause.getMessage(), cause);
                }

                @Override
                public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                    try {
                        eventBus.post(new Message(topic, new String(message.getPayload())));
                    } catch (Exception e) {
                        LOGGER.trace(e.getMessage(), e);
                    }
                }

                @Override
                public void deliveryComplete(final IMqttDeliveryToken token) {
                    // do nothing
                }
            });
            result.subscribe(config.getTopic() + "/light/#");

            return Optional.of(result);
        } catch (final MqttException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            } else {
                LOGGER.error(e.getMessage());
            }

            return Optional.empty();
        }
    }

    private void publish(final String topic, final String value) {
        synchronized (this.mutex) {
            LOGGER.debug("publishing {} = {}", topic, value);

            if (!this.client.filter(MqttClient::isConnected).isPresent()) {
                this.client = this.connect();
            }

            this.client.ifPresent(mqttClient -> {
                try {
                    final MqttMessage message = new MqttMessage(value.getBytes());
                    message.setQos(QOS);
                    message.setRetained(config.isRetain());
                    mqttClient.publish(topic, message);
                } catch (final MqttException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
        }
    }

    @Subscribe
    public void publish(final PublishMessage message) {
        final String topic = message.getTopic();
        final String valueString = message.getMessage();

        if (!publishPreCheck(message)) {
            return;
        }

        this.publish(topic, valueString);
    }

    private boolean publishPreCheck(final PublishMessage message) {
        final String topic = message.getTopic();
        final String valueString = message.getMessage();

        if (config.isDeduplicate()) {
            if (valueString.equals(messageCache.get(topic))) {
                return false;
            } else {
                messageCache.put(topic, valueString);
            }
        }

        return true;
    }

    private void reconnect() {
        synchronized (this.mutex) {
            if (!this.client.filter(MqttClient::isConnected).isPresent()) {
                LOGGER.info("Reconnecting MQTT");
                this.client = this.connect();
            }
        }
    }
}
