package de.rnd7.huemqtt.effects;

import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;

import java.time.Duration;

public class NotifyAndRestoreLights {
    private final Light light;
    private final ColorXY[] notificationColors;

    public NotifyAndRestoreLights(final Light light, final ColorXY... notificationColors) {
        this.light = light;
        this.notificationColors = notificationColors;
    }

    public void notifiy(final Duration duration) {
        LightHelper.withState(() -> {
            for (final ColorXY notificationColor : this.notificationColors) {
                turnOn(notificationColor);
                LightHelper.sleep(duration);
            }
        }, this.light);
    }

    private void turnOn(final ColorXY notificationColor) {
        this.light.setState(State.builder()
            .xy(notificationColor.getXY())
            .brightness(254)
            .on());
    }

}
