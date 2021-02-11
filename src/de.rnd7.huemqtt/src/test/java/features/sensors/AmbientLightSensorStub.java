package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.AmbientLightSensor;
import io.github.zeroone3010.yahueapi.SensorType;

import java.util.Map;

public class AmbientLightSensorStub extends SensorStub implements AmbientLightSensor {

    private int level = 0;
    private boolean daylight = false;
    private boolean dark = false;

    public AmbientLightSensorStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public int getLightLevel() {
        return this.level;
    }

    @Override
    public boolean isDaylight() {
        return this.daylight;
    }

    @Override
    public boolean isDark() {
        return this.dark;
    }

    @Override
    public SensorType getType() {
        return SensorType.AMBIENT_LIGHT;
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        this.level = getInt(properties, "level", this.level);
        this.daylight = getBoolean(properties, "daylight", this.daylight);
        this.dark = getBoolean(properties, "dark", this.dark);
    }
}
