package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;
import de.rnd7.huemqtt.effects.ColorXY;
import de.rnd7.huemqtt.hue.LightDevice;
import io.github.zeroone3010.yahueapi.State;

import java.util.List;

public class LightMessage {
    public enum LightState {
        ON(),
        OFF();

        public static LightState fromValue(final Boolean value) {
            if (value == null) {
                return OFF;
            }
            return value ? ON : OFF;
        }
    }

    public static class Color {
        @SerializedName("x")
        private final float x;
        @SerializedName("y")
        private final float y;

        public Color(final ColorXY xy) {
            this(xy.getXY().get(0), xy.getXY().get(1));
        }

        public Color(final float x, final float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }
    }

    public static LightMessage fromState(final LightDevice.LightState next) {
        final State state = next.getState();
        final LightMessage message = new LightMessage();
        message.setColorTemp(state.getCt());
        message.setBrightness(state.getBri());
        message.setState(next.isReachable() ? LightState.fromValue(state.getOn()) : LightState.OFF);
        final List<Float> xy = state.getXy();
        if (xy != null && xy.size() == 2) {
            message.setColor(new Color(xy.get(0), xy.get(1)));
        }

        return message;
    }

    @SerializedName("state")
    private LightState state;

    @SerializedName("brightness")
    private Integer brightness;

    @SerializedName("color_temp")
    private Integer colorTemp;

    @SerializedName("color")
    private Color color;

    public LightMessage setBrightness(final Integer brightness) {
        this.brightness = brightness;
        return this;
    }

    public Integer getBrightness() {
        return this.brightness;
    }

    public LightMessage setState(final LightState state) {
        this.state = state;
        return this;
    }

    public LightState getState() {
        return this.state;
    }

    public LightMessage setColor(final Color color) {
        this.color = color;
        return this;
    }

    public Color getColor() {
        return this.color;
    }

    public LightMessage setColorTemp(final Integer colorTemp) {
        this.colorTemp = colorTemp;
        return this;
    }

    public Integer getColorTemp() {
        return this.colorTemp;
    }
}
