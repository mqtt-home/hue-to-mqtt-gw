package de.rnd7.huemqtt.effects;

import de.rnd7.huemqtt.hue.HueService;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;

import java.util.Objects;

public class LightHelper {
    private static final Object MUTEX = new Object();

    private LightHelper() {

    }

    public static void turnOff(final Light light) {
        light.turnOff();
        light.setState(State.builder()
            .xy(new ColorXY().getXY())
            .brightness(0)
            .transitionTime(0)
            .off());
    }

    public static void withState(final Runnable runnable, final Light light) {
        synchronized (MUTEX) {
            HueService.refresh();
            final State state = light.getState();

            runnable.run();

            if (Objects.equals(Boolean.FALSE, state.getOn())) {
                // Turn the light off first, to avoid different color transition
                light.turnOff();
            }

            light.setState(state);
        }
    }

    public static void withOff(final Runnable runnable, final Light light) {
        synchronized (MUTEX) {
            HueService.refresh();

            runnable.run();

            turnOff(light);
        }
    }
}
