package de.rnd7.huemqtt.effects;

import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;

import java.time.Duration;

public class NotifyAndTurnOffLights {
    private final Light light;
    private final ColorXY[] notificationColors;

    public NotifyAndTurnOffLights(final Light room, final ColorXY... notificationColors) {
        this.light = room;
        this.notificationColors = notificationColors;
    }

    public void notifiy(final Duration duration) {
        LightHelper.withOff(() -> {
            for (final ColorXY notificationColor : this.notificationColors) {
                turnOn(notificationColor);
                try {
                    Thread.sleep(duration.toMillis());
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
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
