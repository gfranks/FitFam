package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class WCCompanionFilterOptions implements Parcelable, Type {

    public static final Creator<WCCompanionFilterOptions> CREATOR = new Creator<WCCompanionFilterOptions>() {
        @Override
        public WCCompanionFilterOptions createFromParcel(Parcel in) {
            return new WCCompanionFilterOptions(in);
        }
        @Override
        public WCCompanionFilterOptions[] newArray(int size) {
            return new WCCompanionFilterOptions[size];
        }
    };

    @SerializedName("gymName")
    private String gymName;
    @SerializedName("sex")
    private String sex;
    @SerializedName("age")
    private int age;
    @SerializedName("weight")
    private int weight;

    public WCCompanionFilterOptions() {
    }

    public WCCompanionFilterOptions(Parcel in) {
        readFromParcel(in);
    }

    public WCCompanionFilterOptions(Builder builder) {
        gymName = builder.gymName;
        sex = builder.sex;
        age = builder.age;
        weight = builder.weight;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
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
        out.writeValue(gymName);
        out.writeValue(sex);
        out.writeValue(age);
        out.writeValue(weight);
    }

    private void readFromParcel(Parcel in) {
        gymName = (String) in.readValue(String.class.getClassLoader());
        sex = (String) in.readValue(String.class.getClassLoader());
        age = (int) in.readValue(Integer.class.getClassLoader());
        weight = (int) in.readValue(Integer.class.getClassLoader());
    }

    public static class Builder {

        private String gymName;
        private String sex;
        private int age;
        private int weight;

        public Builder() {
        }

        public Builder setGymName(String gymName) {
            this.gymName = gymName;

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

        public WCCompanionFilterOptions build() {
            return new WCCompanionFilterOptions(this);
        }
    }
}
