package de.rnd7.huemqtt.hue;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import de.rnd7.huemqtt.hue.api.HueAbstraction;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.TopicCleaner;
import io.github.zeroone3010.yahueapi.AmbientLightSensor;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.PresenceSensor;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.TemperatureSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HueService {
    private static HueService instance;
    private final HueAbstraction hue;
    private final String baseTopic;
    private ImmutableList<HueDevice> devices = ImmutableList.of();
    private static final Logger LOGGER = LoggerFactory.getLogger(HueService.class);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private HueService(final HueAbstraction hue, final String baseTopic) {
        this.hue = hue;
        this.baseTopic = baseTopic;

        scan();
    }

    @Subscribe
    public void onMessage(final Message message) {
        for (final Device device : this.devices) {
            if (device.apply(message)) {
                return;
            }
        }
    }

    public static void shutdown() {
        if (instance != null) {
            instance.devices = ImmutableList.of();
            instance.executor.shutdown();
            instance = null;
        }
    }

    public static HueService start(final HueAbstraction hue, final String baseTopic) {
        if (instance != null) {
            throw new IllegalStateException("Hue service cannot be started twice");
        }

        instance = new HueService(hue, baseTopic);

        instance.executor.scheduleWithFixedDelay(instance::poll, 1500, 500, TimeUnit.MILLISECONDS);
        instance.executor.scheduleAtFixedRate(instance::scan, 1, 1, TimeUnit.HOURS);

        return instance;
    }

    public static void refresh() {
        if (instance != null) {
            instance.hue.refresh();
        }
    }

    public void poll() {
        try {
            this.hue.refresh();

            this.devices.forEach(HueDevice::triggerUpdate);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void scan() {
        try {
            final List<HueDevice> nextDevices = new ArrayList<>();

            for (final Room room : this.hue.getRooms()) {
                for (final Light light : room.getLights()) {
                    final String topic = this.baseTopic + "/light/" + TopicCleaner.clean(room.getName() + "/" + light.getName());
                    nextDevices.add(new LightDevice(light, topic, topic));
                }
            }

            for (final Light light : this.hue.getUnassignedLights()) {
                final String topic = this.baseTopic + "/light/" + TopicCleaner.clean(light.getName());
                nextDevices.add(new LightDevice(light, topic, topic));
            }

            for (final Switch hueSwitch : this.hue.getSwitches()) {
                final String topic = this.baseTopic + "/switch/" + TopicCleaner.clean(hueSwitch.getName());
                nextDevices.add(new SwitchDevice(hueSwitch, topic, hueSwitch.getId()));
            }

            for (final DaylightSensor sensor : this.hue.getDaylightSensors()) {
                final String topic = this.baseTopic + "/daylight/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new DaylightSensorDevice(sensor, topic, sensor.getId()));
            }

            for (final PresenceSensor sensor : this.hue.getPresenceSensors()) {
                final String topic = this.baseTopic + "/presence/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new PresenceSensorDevice(sensor, topic, sensor.getId()));
            }

            for (final AmbientLightSensor sensor : this.hue.getAmbientLightSensors()) {
                final String topic = this.baseTopic + "/ambient/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new AmbientLightSensorDevice(sensor, topic, sensor.getId()));
            }

            for (final TemperatureSensor sensor : this.hue.getTemperatureSensors()) {
                final String topic = this.baseTopic + "/temperature/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new TemperatureSensorDevice(sensor, topic, sensor.getId()));
            }

            this.devices = ImmutableList.copyOf(nextDevices);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public <T extends HueDevice> T getDevice(final String id, final Class<T> type) {
        return this.devices.stream().filter(d -> d.getId().equals(id))
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    public static HueService get() {
        return instance;
    }
}
