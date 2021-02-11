package de.rnd7.huemqtt.hue.api;

import io.github.zeroone3010.yahueapi.AmbientLightSensor;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.Hue;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.PresenceSensor;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.TemperatureSensor;

public class HueAbstractionImpl implements HueAbstraction {
    private final Hue hue;

    public HueAbstractionImpl(final Hue hue) {
        this.hue = hue;
        this.hue.setCaching(true);
    }

    public void refresh() {
        this.hue.refresh();
    }

    public Iterable<Room> getRooms() {
        return hue.getRooms();
    }

    public Iterable<Light> getUnassignedLights() {
        return hue.getUnassignedLights();
    }

    public Iterable<Switch> getSwitches() {
        return hue.getSwitches();
    }

    public Iterable<DaylightSensor> getDaylightSensors() {
        return hue.getDaylightSensors();
    }

    public Iterable<PresenceSensor> getPresenceSensors() {
        return hue.getPresenceSensors();
    }

    public Iterable<AmbientLightSensor> getAmbientLightSensors() {
        return hue.getAmbientLightSensors();
    }

    public Iterable<TemperatureSensor> getTemperatureSensors() {
        return hue.getTemperatureSensors();
    }
}
