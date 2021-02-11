package features;
import de.rnd7.huemqtt.hue.api.HueAbstraction;
import io.github.zeroone3010.yahueapi.AmbientLightSensor;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.PresenceSensor;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.Sensor;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.TemperatureSensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HueAbstractionStub implements HueAbstraction {

    private final List<Sensor> sensors = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();

    void addSensor(final Sensor sensor) {
        this.sensors.add(sensor);
    }

    void addLight(final Light light) {
        this.lights.add(light);
    }

    @Override
    public void refresh() {

    }

    public <T extends Light> Optional<T> getLight(final String name, final Class<T> type) {
        return this.lights.stream()
            .filter(d -> d.getName().equals(name))
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst();
    }

    public <T extends Sensor> T getSensor(final String deviceId, final Class<T> type) {
        return this.sensors.stream()
            .filter(d -> d.getId().equals(deviceId))
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    @Override
    public Iterable<Room> getRooms() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Light> getUnassignedLights() {
        return this.lights;
    }

    @Override
    public Iterable<Switch> getSwitches() {
        return getSensors(Switch.class);
    }

    @Override
    public Iterable<DaylightSensor> getDaylightSensors() {
        return getSensors(DaylightSensor.class);
    }

    @Override
    public Iterable<PresenceSensor> getPresenceSensors() {
        return getSensors(PresenceSensor.class);
    }

    @Override
    public Iterable<AmbientLightSensor> getAmbientLightSensors() {
        return getSensors(AmbientLightSensor.class);
    }

    @Override
    public Iterable<TemperatureSensor> getTemperatureSensors() {
        return getSensors(TemperatureSensor.class);
    }

    private <T extends Sensor> Iterable<T> getSensors(final Class<T> type) {
        return this.sensors.stream()
            .filter(type::isInstance)
            .map(type::cast)::iterator;
    }

}
