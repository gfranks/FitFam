package com.github.gfranks.fitfam.data.adapter;

import com.github.gfranks.fitfam.util.Utils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import java.io.IOException;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(Utils.getDateTimeFormatter().print(value.toDateTimeToday()));
    }

    @Override
    public LocalTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String next = in.nextString();
        if (next != null && next.length() > 0) {
            return new LocalTime(DateTime.parse(next).toDateTime(DateTimeZone.getDefault()).getMillis());
        }
        return null;
    }
}
