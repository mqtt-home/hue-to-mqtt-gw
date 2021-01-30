package de.rnd7.huemqtt.effects;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;

public class ColorXY {

    private float x;
    private float y;

    public ColorXY() {
        this(0.3687f, 0.371f);
    }

    public ColorXY(final float x, final float y) {
        this.x = x;
        this.y = y;
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
        return Float.compare(colorXY.x, x) == 0 && Float.compare(colorXY.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }
}
