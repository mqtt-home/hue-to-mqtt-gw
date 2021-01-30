package de.rnd7.huemqtt.hue;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter out, final Duration value) throws IOException {
        out.value(value.toMillis());
    }

    @Override
    public Duration read(final JsonReader in) throws IOException {
        return Duration.ofMillis(in.nextLong());
    }
}
