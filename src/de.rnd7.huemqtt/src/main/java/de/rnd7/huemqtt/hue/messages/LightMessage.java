package de.rnd7.huemqtt.hue.messages;

import com.google.gson.annotations.SerializedName;
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

    public static LightMessage fromState(final State next) {
        final LightMessage message = new LightMessage();
        message.setColorTemp(next.getCt());
        message.setBrightness(next.getBri());
        message.setState(LightState.fromValue(next.getOn()));
        final List<Float> xy = next.getXy();
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

    public void setBrightness(final Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getBrightness() {
        return this.brightness;
    }

    public void setState(final LightState state) {
        this.state = state;
    }

    public LightState getState() {
        return this.state;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColorTemp(final Integer colorTemp) {
        this.colorTemp = colorTemp;
    }

    public Integer getColorTemp() {
        return this.colorTemp;
    }
}
