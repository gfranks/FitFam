package com.github.gfranks.workoutcompanion.data.adapter;

import com.github.gfranks.workoutcompanion.util.Utils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;

public class DateTimeAdapter extends TypeAdapter<DateTime> {

    @Override
    public void write(JsonWriter out, DateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(Utils.getDateTimeFormatter().print(value));
    }

    @Override
    public DateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String next = in.nextString();
        if (next != null && next.length() > 0) {
            return DateTime.parse(next).toDateTime(DateTimeZone.getDefault());
        }
        return null;
    }
}
