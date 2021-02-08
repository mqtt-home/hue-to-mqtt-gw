package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.PresenceSensor;
import io.github.zeroone3010.yahueapi.SensorType;

import java.util.Map;

public class PresenceSensorStub extends SensorStub implements PresenceSensor {
    private boolean presence = false;

    public PresenceSensorStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public SensorType getType() {
        return SensorType.PRESENCE;
    }

    @Override
    public boolean isPresence() {
        return this.presence;
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        this.presence = getBoolean(properties, "presence", this.presence);
    }

}
