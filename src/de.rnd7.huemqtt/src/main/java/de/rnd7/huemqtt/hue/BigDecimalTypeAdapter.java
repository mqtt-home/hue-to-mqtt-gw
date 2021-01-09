package de.rnd7.huemqtt.hue;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalTypeAdapter extends TypeAdapter<BigDecimal> {
    @Override
    public void write(final JsonWriter out, final BigDecimal value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public BigDecimal read(final JsonReader in) throws IOException {
        return new BigDecimal(in.nextString());
    }
}
