package de.rnd7.huemqtt.hue;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rnd7.mqttgateway.Message;
import de.rnd7.mqttgateway.TopicCleaner;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.Hue;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.MotionSensor;
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
    private final Hue hue;
    private String baseTopic;
    private final List<HueDevice> devices = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(HueService.class);
    private final Gson gson = new GsonBuilder().create();

    public HueService(final Hue hue, final String baseTopic) {
        hue.setCaching(true);
        this.hue = hue;
        this.baseTopic = baseTopic;

        scan();
    }

    @Subscribe
    public void onMessage(final Message message) {
        for (Device device : devices) {
            if (device.apply(message)) {
                return;
            }
        }
    }

    public HueService start() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleWithFixedDelay(this::poll, 1500, 500, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::scan, 1, 1, TimeUnit.HOURS);

        return this;
    }

    private void poll() {
        try {
            hue.refresh();
            devices.forEach(HueDevice::triggerUpdate);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void scan() {
        try {
            hue.getRooms();

            final List<HueDevice> nextDevices = new ArrayList<>();

            for (final Room room : hue.getRooms()) {
                for (Light light : room.getLights()) {
                    final String topic = baseTopic + "/light/" + TopicCleaner.clean(room.getName() + "/" + light.getName());
                    nextDevices.add(new LightDevice(light, topic, topic));
                }
            }

            for (final Light light : hue.getUnassignedLights()) {
                final String topic = baseTopic + "/light/" + TopicCleaner.clean(light.getName());
                nextDevices.add(new LightDevice(light, topic, topic));
            }

            for (final Switch hueSwitch : hue.getSwitches()) {
                final String topic = baseTopic + "/switch/" + TopicCleaner.clean(hueSwitch.getName());
                nextDevices.add(new SwitchDevice(hueSwitch, topic, hueSwitch.getId()));
            }

            for (final DaylightSensor sensor : hue.getDaylightSensors()) {
                final String topic = baseTopic + "/daylight/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new DaylightSensorDevice(sensor, topic, sensor.getId()));
            }

            for (final MotionSensor sensor : hue.getMotionSensors()) {
                final String topic = baseTopic + "/motion/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new MotionSensorDevice(sensor, topic, sensor.getId()));
            }

            for (final TemperatureSensor sensor : hue.getTemperatureSensors()) {
                final String topic = baseTopic + "/temperature/" + TopicCleaner.clean(sensor.getName());
                nextDevices.add(new TemperatureSensorDevice(sensor, topic, sensor.getId()));
            }

            devices.clear();
            devices.addAll(nextDevices);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
