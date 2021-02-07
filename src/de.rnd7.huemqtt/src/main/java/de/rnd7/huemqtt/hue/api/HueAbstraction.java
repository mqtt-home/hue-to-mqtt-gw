package de.rnd7.huemqtt.hue.api;

import io.github.zeroone3010.yahueapi.AmbientLightSensor;
import io.github.zeroone3010.yahueapi.DaylightSensor;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.PresenceSensor;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.Switch;
import io.github.zeroone3010.yahueapi.TemperatureSensor;

public interface HueAbstraction {

    void refresh();

    Iterable<Room> getRooms();

    Iterable<Light> getUnassignedLights();

    Iterable<Switch> getSwitches();

    Iterable<DaylightSensor> getDaylightSensors();

    Iterable<PresenceSensor> getPresenceSensors();

    Iterable<AmbientLightSensor> getAmbientLightSensors();

    Iterable<TemperatureSensor> getTemperatureSensors();

}
