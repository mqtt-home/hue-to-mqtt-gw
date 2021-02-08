package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Sensor;

public class SensorFactory {
    private SensorFactory() {

    }

    public static Sensor create(final DeviceDescriptor device) {
        switch (device.getType()) {
            case daylight:
                return new DaylightSensorStub(device);
            case ambient:
                return new AmbientLightSensorStub(device);
            case temperature:
                return new TemperatureSensorStub(device);
            case presence:
                return new PresenceSensorStub(device);
            default:
                throw new IllegalStateException("Unknown sensor type: " + device);
        }
    }
}
