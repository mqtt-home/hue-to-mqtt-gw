package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.Sensor;

public class SensorFactory {
    private SensorFactory() {

    }

    public static Sensor createSensor(final DeviceDescriptor device) {
        switch (device.getType()) {
            case daylight:
                return new DaylightSensorStub(device);
            case ambient:
                return new AmbientLightSensorStub(device);
            case temperature:
                return new TemperatureSensorStub(device);
            case presence:
                return new PresenceSensorStub(device);
            case button:
                return new SwitchSensorStub(device);
            default:
                return null;
        }
    }

    public static Light createLight(final DeviceDescriptor device) {
        switch (device.getType()) {
            case color_light:
                return new ColorLightStub(device);
            case ct_light:
                return new ColorTemperatureLightStub(device);
            default:
                return null;
        }
    }
}
