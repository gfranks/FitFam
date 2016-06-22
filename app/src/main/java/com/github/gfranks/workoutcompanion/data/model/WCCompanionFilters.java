package com.github.gfranks.workoutcompanion.data.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class WCCompanionFilters implements Parcelable, Type {

    public static final String EXTRA = "companion_filters";
    public static final Creator<WCCompanionFilters> CREATOR = new Creator<WCCompanionFilters>() {
        @Override
        public WCCompanionFilters createFromParcel(Parcel in) {
            return new WCCompanionFilters(in);
        }

        @Override
        public WCCompanionFilters[] newArray(int size) {
            return new WCCompanionFilters[size];
        }
    };

    @SerializedName("location")
    private WCLocation location;
    @SerializedName("gymId")
    private String gymId;
    @SerializedName("sex")
    private String sex;
    @SerializedName("age")
    private int age;
    @SerializedName("weight")
    private int weight;

    public WCCompanionFilters() {
    }

    public WCCompanionFilters(Parcel in) {
        readFromParcel(in);
    }

    public WCCompanionFilters(Builder builder) {
        location = builder.location;
        gymId = builder.gymId;
        sex = builder.sex;
        age = builder.age;
        weight = builder.weight;
    }

    public WCLocation getLocation() {
        return location;
    }

    public void setLocation(WCLocation location) {
        this.location = location;
    }

    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(location);
        out.writeValue(gymId);
        out.writeValue(sex);
        out.writeValue(age);
        out.writeValue(weight);
    }

    private void readFromParcel(Parcel in) {
        location = (WCLocation) in.readValue(Location.class.getClassLoader());
        gymId = (String) in.readValue(String.class.getClassLoader());
        sex = (String) in.readValue(String.class.getClassLoader());
        age = (int) in.readValue(Integer.class.getClassLoader());
        weight = (int) in.readValue(Integer.class.getClassLoader());
    }

    public static class Builder {

        private WCLocation location;
        private String gymId;
        private String sex;
        private int age;
        private int weight;

        public Builder() {
        }

        public Builder setLocation(WCLocation location) {
            this.location = location;

            return this;
        }

        public Builder setGymId(String gymId) {
            this.gymId = gymId;

            return this;
        }

        public Builder setSex(String sex) {
            this.sex = sex;

            return this;
        }

        public Builder setAge(int age) {
            this.age = age;

            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;

            return this;
        }

        public WCCompanionFilters build() {
            return new WCCompanionFilters(this);
        }
    }
}
