package de.rnd7.huemqtt.effects;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;

public final class ColorXY {

    private final float x;
    private final float y;

    public ColorXY() {
        this(0.3687f, 0.371f);
    }

    public ColorXY(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public ColorXY(final List<Float> xy) {
        this(xy.get(0), xy.get(1));
    }

    public List<Float> getXY() {
        return Arrays.asList(this.x, this.y);
    }

    @Override
    public String toString() {
        return "ColorXY{" +
            "x=" + this.x +
            ", y=" + this.y +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ColorXY colorXY = (ColorXY) o;
        return Float.compare(colorXY.x, this.x) == 0 && Float.compare(colorXY.y, this.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.x, this.y);
    }
}
