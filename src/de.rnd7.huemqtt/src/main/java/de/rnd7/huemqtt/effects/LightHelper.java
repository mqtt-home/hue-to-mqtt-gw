package de.rnd7.huemqtt.effects;

import de.rnd7.huemqtt.hue.HueService;
import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.State;
import io.reactivex.Observable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
            final var state = light.getState();

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

    public static void processTasksWithPostDelay(final List<Runnable> tasks, final Duration delay) {
        if (tasks.isEmpty()) {
            return;
        }

        final var finalTasks = new ArrayList<>(tasks);
        finalTasks.add(() -> {});

        final var first = finalTasks.get(0);
        Observable.fromIterable(finalTasks)
            .concatMap(task -> Observable.just(task)
                .delay(task == first ? 0 : delay.toMillis(), TimeUnit.MILLISECONDS))
            .blockingForEach(Runnable::run);
    }
}
