package de.rnd7.huemqtt.effects;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LightEffectData {
    private LightEffect effect = LightEffect.notify_restore;
    private final List<ColorXY> colors = new ArrayList<>();
    private Duration duration = Duration.ofSeconds(1);

    public LightEffectData setEffect(final LightEffect effect) {
        this.effect = effect;
        return this;
    }

    public LightEffect getEffect() {
        return this.effect;
    }

    public List<ColorXY> getColors() {
        return this.colors;
    }

    public LightEffectData addColor(final ColorXY color) {
        this.colors.add(color);
        return this;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public LightEffectData setDuration(final Duration duration) {
        this.duration = duration;
        return this;
    }
}
