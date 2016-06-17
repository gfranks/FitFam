package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class WCGymReview implements Parcelable, Type {

    public static final Parcelable.Creator<WCGymReview> CREATOR = new Parcelable.Creator<WCGymReview>() {
        public WCGymReview createFromParcel(Parcel in) {
            return new WCGymReview(in);
        }

        public WCGymReview[] newArray(int size) {
            return new WCGymReview[size];
        }
    };

    @SerializedName("aspects")
    private List<WCGymReviewAspect> aspects;
    @SerializedName("author_name")
    private String author_name;
    @SerializedName("author_url")
    private String author_url;
    @SerializedName("profile_photo_url")
    private String profile_photo_url;
    @SerializedName("language")
    private String language;
    @SerializedName("rating")
    private int rating;
    @SerializedName("text")
    private String text;
    @SerializedName("time")
    private long time;

    public WCGymReview() {
    }

    public WCGymReview(Parcel in) {
        readFromParcel(in);
    }

    public WCGymReview(Builder builder) {
        aspects = builder.aspects;
        author_name = builder.author_name;
        author_url = builder.author_url;
        profile_photo_url = builder.profile_photo_url;
        language = builder.language;
        rating = builder.rating;
        text = builder.text;
        time = builder.time;
    }

    public List<WCGymReviewAspect> getAspects() {
        return aspects;
    }

    public void setAspects(List<WCGymReviewAspect> aspects) {
        this.aspects = aspects;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(aspects);
        out.writeValue(author_name);
        out.writeValue(author_url);
        out.writeValue(profile_photo_url);
        out.writeValue(language);
        out.writeValue(rating);
        out.writeValue(text);
        out.writeValue(time);
    }

    private void readFromParcel(Parcel in) {
        aspects = in.readArrayList(WCGymReviewAspect.class.getClassLoader());
        author_name = (String) in.readValue(String.class.getClassLoader());
        author_url = (String) in.readValue(String.class.getClassLoader());
        profile_photo_url = (String) in.readValue(String.class.getClassLoader());
        language = (String) in.readValue(String.class.getClassLoader());
        rating = (int) in.readValue(Integer.class.getClassLoader());
        text = (String) in.readValue(String.class.getClassLoader());
        time = (long) in.readValue(Long.class.getClassLoader());
    }

    public static class Builder {

        private List<WCGymReviewAspect> aspects;
        private String author_name;
        private String author_url;
        private String profile_photo_url;
        private String language;
        private int rating;
        private String text;
        private long time;

        public Builder() {
        }

        public Builder setAspects(List<WCGymReviewAspect> aspects) {
            this.aspects = aspects;

            return this;
        }

        public Builder setAuthor_name(String author_name) {
            this.author_name = author_name;

            return this;
        }

        public Builder setAuthor_url(String author_url) {
            this.author_url = author_url;

            return this;
        }

        public Builder setProfile_photo_url(String profile_photo_url) {
            this.profile_photo_url = profile_photo_url;

            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;

            return this;
        }

        public Builder setRating(int rating) {
            this.rating = rating;

            return this;
        }

        public Builder setText(String text) {
            this.text = text;

            return this;
        }

        public Builder setTime(long time) {
            this.time = time;

            return this;
        }

        public WCGymReview build() {
            return new WCGymReview(this);
        }
    }
}
