package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class WCGymPhoto implements Parcelable, Type {

    public static final Parcelable.Creator<WCGymPhoto> CREATOR = new Parcelable.Creator<WCGymPhoto>() {
        public WCGymPhoto createFromParcel(Parcel in) {
            return new WCGymPhoto(in);
        }

        public WCGymPhoto[] newArray(int size) {
            return new WCGymPhoto[size];
        }
    };

    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("html_attributions")
    private List<String> html_attributions;
    @SerializedName("photo_reference")
    private String photo_reference;

    public WCGymPhoto() {
    }

    public WCGymPhoto(Parcel in) {
        readFromParcel(in);
    }

    public WCGymPhoto(Builder builder) {
        width = builder.width;
        height = builder.height;
        html_attributions = builder.html_attributions;
        photo_reference = builder.photo_reference;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<String> getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(width);
        out.writeValue(height);
        out.writeList(html_attributions);
        out.writeValue(photo_reference);
    }

    private void readFromParcel(Parcel in) {
        width = (int) in.readValue(Integer.class.getClassLoader());
        height = (int) in.readValue(Integer.class.getClassLoader());
        html_attributions = in.readArrayList(String.class.getClassLoader());
        photo_reference = (String) in.readValue(String.class.getClassLoader());
    }

    public static class Builder {

        private int width;
        private int height;
        private List<String> html_attributions;
        private String photo_reference;

        public Builder() {
        }

        public Builder setWidth(int width) {
            this.width = width;

            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;

            return this;
        }

        public Builder setHtml_attributions(List<String> html_attributions) {
            this.html_attributions = html_attributions;

            return this;
        }

        public Builder setPhoto_reference(String photo_reference) {
            this.photo_reference = photo_reference;

            return this;
        }

        public WCGymPhoto build() {
            return new WCGymPhoto(this);
        }

    }
}
