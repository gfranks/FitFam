package com.github.gfranks.workoutcompanion.data.adapter;

import com.github.gfranks.workoutcompanion.util.Utils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.IOException;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(Utils.getDateTimeFormatter().print(value.toDateTime(LocalTime.now())));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String next = in.nextString();
        if (next != null && next.length() > 0) {
            return new LocalDate(DateTime.parse(next).toDateTime(DateTimeZone.getDefault()).getMillis());
        }
        return null;
    }
}
