package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class FFGymHours implements Parcelable, Type {

    public static final Parcelable.Creator<FFGymHours> CREATOR = new Parcelable.Creator<FFGymHours>() {
        public FFGymHours createFromParcel(Parcel in) {
            return new FFGymHours(in);
        }

        public FFGymHours[] newArray(int size) {
            return new FFGymHours[size];
        }
    };

    @SerializedName("open_now")
    private boolean open_now;
    @SerializedName("weekday_text")
    private List<String> weekday_text;

    public FFGymHours() {
    }

    public FFGymHours(Parcel in) {
        readFromParcel(in);
    }

    public FFGymHours(Builder builder) {
        open_now = builder.open_now;
        weekday_text = builder.weekday_text;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public List<String> getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(List<String> weekday_text) {
        this.weekday_text = weekday_text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(open_now ? "Status: Open" : "Status: Closed");
        sb.append("\n");
        for (String text : weekday_text) {
            sb.append("\n");
            sb.append(text.substring(0, text.indexOf(":") + 1));
            if (text.contains("Mon")) {
                sb.append("\t\t\t\t\t");
            } else if (text.contains("Tue")) {
                sb.append("\t\t\t\t\t");
            } else if (text.contains("Wed")) {
                sb.append("\t\t");
            } else if (text.contains("Thur")) {
                sb.append("\t\t\t\t");
            } else if (text.contains("Fri")) {
                sb.append("\t\t\t\t\t\t\t");
            } else if (text.contains("Sat")) {
                sb.append("\t\t\t\t\t");
            } else if (text.contains("Sun")) {
                sb.append("\t\t\t\t\t\t");
            }
            sb.append(text.substring(text.indexOf(":") + 1, text.length()));
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(open_now ? 1 : 0);
        out.writeList(weekday_text);
    }

    private void readFromParcel(Parcel in) {
        open_now = ((int) in.readValue(Integer.class.getClassLoader())) == 1;
        weekday_text = in.readArrayList(String.class.getClassLoader());
    }

    public static class Builder {

        private boolean open_now;
        private List<String> weekday_text;

        public Builder() {
        }

        public Builder setOpen_now(boolean open_now) {
            this.open_now = open_now;

            return this;
        }

        public Builder setWeekday_text(List<String> weekday_text) {
            this.weekday_text = weekday_text;

            return this;
        }

        public FFGymHours build() {
            return new FFGymHours(this);
        }
    }
}
