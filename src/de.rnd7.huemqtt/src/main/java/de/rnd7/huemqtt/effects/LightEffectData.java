package de.rnd7.huemqtt.effects;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LightEffectData {
    private LightEffect effect = LightEffect.notify_restore;
    private List<ColorXY> colors = new ArrayList<>();
    private Duration duration = Duration.ofSeconds(1);

    public LightEffectData setEffect(final LightEffect effect) {
        this.effect = effect;
        return this;
    }

    public LightEffect getEffect() {
        return effect;
    }


    public List<ColorXY> getColors() {
        return colors;
    }

    public LightEffectData addColor(final ColorXY color) {
        this.colors.add(color);
        return this;
    }

    public Duration getDuration() {
        return duration;
    }

    public LightEffectData setDuration(final Duration duration) {
        this.duration = duration;
        return this;
    }
}
