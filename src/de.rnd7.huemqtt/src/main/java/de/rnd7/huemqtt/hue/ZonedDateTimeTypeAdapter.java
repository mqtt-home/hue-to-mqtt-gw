package de.rnd7.huemqtt.hue;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;

public class ZonedDateTimeTypeAdapter extends TypeAdapter<ZonedDateTime> {
    @Override
    public void write(final JsonWriter out, final ZonedDateTime value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public ZonedDateTime read(final JsonReader in) throws IOException {
        return ZonedDateTime.parse(in.nextString());
    }
}
