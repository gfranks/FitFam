package com.github.gfranks.fitfam.data.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FFCompanionFilters implements Parcelable, Type {

    public static final String EXTRA = "companion_filters";
    public static final Creator<FFCompanionFilters> CREATOR = new Creator<FFCompanionFilters>() {
        @Override
        public FFCompanionFilters createFromParcel(Parcel in) {
            return new FFCompanionFilters(in);
        }

        @Override
        public FFCompanionFilters[] newArray(int size) {
            return new FFCompanionFilters[size];
        }
    };

    @SerializedName("location")
    private FFLocation location;
    @SerializedName("gym")
    private FFGym gym;
    @SerializedName("sex")
    private String sex;
    @SerializedName("age")
    private int age;
    @SerializedName("weight")
    private int weight;
    @SerializedName("exercises")
    private List<String> exercises;

    public FFCompanionFilters() {
    }

    public FFCompanionFilters(Parcel in) {
        readFromParcel(in);
    }

    public FFCompanionFilters(Builder builder) {
        location = builder.location;
        gym = builder.gym;
        sex = builder.sex;
        age = builder.age;
        weight = builder.weight;
        exercises = builder.exercises;
    }

    public FFLocation getLocation() {
        return location;
    }

    public void setLocation(FFLocation location) {
        this.location = location;
    }

    public FFGym getGym() {
        return gym;
    }

    public void setGym(FFGym gym) {
        this.gym = gym;
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

    public List<String> getExercises() {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        return exercises;
    }

    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(location);
        out.writeValue(gym);
        out.writeValue(sex);
        out.writeValue(age);
        out.writeValue(weight);
        out.writeList(exercises);
    }

    private void readFromParcel(Parcel in) {
        location = (FFLocation) in.readValue(Location.class.getClassLoader());
        gym = (FFGym) in.readValue(FFGym.class.getClassLoader());
        sex = (String) in.readValue(String.class.getClassLoader());
        age = (int) in.readValue(Integer.class.getClassLoader());
        weight = (int) in.readValue(Integer.class.getClassLoader());
        exercises = in.readArrayList(String.class.getClassLoader());
    }

    public static class Builder {

        private FFLocation location;
        private FFGym gym;
        private String sex;
        private int age;
        private int weight;
        private List<String> exercises;

        public Builder() {
        }

        public Builder setLocation(FFLocation location) {
            this.location = location;

            return this;
        }

        public Builder setGym(FFGym gym) {
            this.gym = gym;

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

        public Builder setExercises(List<String> exercises) {
            this.exercises = exercises;

            return this;
        }

        public FFCompanionFilters build() {
            return new FFCompanionFilters(this);
        }
    }
}
