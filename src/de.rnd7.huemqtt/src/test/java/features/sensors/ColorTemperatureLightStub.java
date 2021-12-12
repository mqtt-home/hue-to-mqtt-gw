package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.LightType;
import io.github.zeroone3010.yahueapi.State;

import java.util.Locale;
import java.util.Map;

public class ColorTemperatureLightStub extends LightStub {

    private int color_temp;
    private int brightness;
    private boolean state = true;

    public ColorTemperatureLightStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public LightType getType() {
        return LightType.COLOR_TEMPERATURE;
    }

    @Override
    public Integer getMaxLumens() {
        return 100;
    }

    @Override
    public void setState(final State state) {

    }

    @Override
    public State getState() {
        return State.builder()
            .colorTemperatureInMireks(this.color_temp)
            .brightness(this.brightness)
            .on(this.state);
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        this.color_temp = getInt(properties, "color_temp", this.color_temp);
        this.brightness = getInt(properties, "brightness", this.brightness);

        if (properties.containsKey("state")) {
            this.state = properties.get("state").toLowerCase(Locale.ROOT).equals("on");
        }
    }
}
