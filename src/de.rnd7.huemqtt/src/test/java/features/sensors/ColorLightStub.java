package features.sensors;

import features.DeviceDescriptor;
import io.github.zeroone3010.yahueapi.Color;
import io.github.zeroone3010.yahueapi.LightType;
import io.github.zeroone3010.yahueapi.State;

import java.util.Locale;
import java.util.Map;

public class ColorLightStub extends LightStub {

    private String color = "#FFFFFF";
    private boolean state = true;

    public ColorLightStub(final DeviceDescriptor device) {
        super(device);
    }

    @Override
    public LightType getType() {
        return LightType.EXTENDED_COLOR;
    }

    @Override
    public void setState(final State state) {

    }

    @Override
    public State getState() {
        return State.builder()
            .color(Color.of(this.color))
            .on(this.state);
    }

    @Override
    public void setProperties(final Map<String, String> properties) {
        super.setProperties(properties);

        this.color = getString(properties, "color", this.color);

        if (properties.containsKey("state")) {
            this.state = properties.get("state").toLowerCase(Locale.ROOT).equals("on");
        }
    }
}
