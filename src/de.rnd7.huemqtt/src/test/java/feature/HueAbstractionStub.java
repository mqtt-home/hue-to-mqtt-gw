package feature;
import de.rnd7.huemqtt.hue.api.HueAbstraction;
import io.github.zeroone3010.yahueapi.AmbientLightSensor;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.PresenceSensor;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.TemperatureSensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HueAbstractionStub implements HueAbstraction {

    private List<DaylightSensor> daylightSensors = new ArrayList<>();

    void addDaylightSensor(DaylightSensor sensor) {
        daylightSensors.add(sensor);
    }

    @Override
    public void refresh() {

    }

    public DaylightSensor getDevice(final String deviceId) {
        return daylightSensors.stream()
            .filter(d -> d.getId().equals(deviceId))
            .findFirst().orElseThrow(IllegalStateException::new);
    }

    @Override
    public Iterable<Room> getRooms() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Light> getUnassignedLights() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Switch> getSwitches() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<DaylightSensor> getDaylightSensors() {
        return daylightSensors;
    }

    @Override
    public Iterable<PresenceSensor> getPresenceSensors() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<AmbientLightSensor> getAmbientLightSensors() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<TemperatureSensor> getTemperatureSensors() {
        return Collections.emptyList();
    }

}
