package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class WCGymReviewAspect implements Parcelable, Type {

    public static final Parcelable.Creator<WCGymReviewAspect> CREATOR = new Parcelable.Creator<WCGymReviewAspect>() {
        public WCGymReviewAspect createFromParcel(Parcel in) {
            return new WCGymReviewAspect(in);
        }

        public WCGymReviewAspect[] newArray(int size) {
            return new WCGymReviewAspect[size];
        }
    };

    @SerializedName("rating")
    private int rating;
    @SerializedName("type")
    private String type;

    public WCGymReviewAspect() {
    }

    public WCGymReviewAspect(Builder builder) {
        rating = builder.rating;
        type = builder.type;
    }

    public WCGymReviewAspect(Parcel in) {
        readFromParcel(in);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(rating);
        out.writeValue(type);
    }

    private void readFromParcel(Parcel in) {
        rating = (int) in.readValue(Integer.class.getClassLoader());
        type = (String) in.readValue(String.class.getClassLoader());
    }

    public static class Builder {

        private int rating;
        private String type;

        public Builder() {
        }

        public Builder setType(String type) {
            this.type = type;

            return this;
        }

        public Builder setRating(int rating) {
            this.rating = rating;

            return this;
        }

        public WCGymReviewAspect build() {
            return new WCGymReviewAspect(this);
        }
    }
}
