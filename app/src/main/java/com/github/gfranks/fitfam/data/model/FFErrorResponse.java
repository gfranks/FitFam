package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Type;

public class FFErrorResponse extends Exception implements Parcelable, Type {

    public static final String EXTRA = "error_response";
    public static final Creator<FFErrorResponse> CREATOR = new Creator<FFErrorResponse>() {
        public FFErrorResponse createFromParcel(Parcel in) {
            return new FFErrorResponse(in);
        }

        public FFErrorResponse[] newArray(int size) {
            return new FFErrorResponse[size];
        }
    };

    private String message;

    public FFErrorResponse() {
    }

    public FFErrorResponse(String message) {
        this.message = message;
    }

    public FFErrorResponse(Parcel in) {
        readFromParcel(in);
    }

    public FFErrorResponse(Builder builder) {
        this.message = builder.message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(message);
    }

    private void readFromParcel(Parcel in) {
        message = (String) in.readValue(String.class.getClassLoader());
    }

    public static class Builder {

        private String message;

        public Builder() {
        }

        public Builder setMessage(String message) {
            this.message = message;

            return this;
        }

        public FFErrorResponse build() {
            return new FFErrorResponse(this);
        }
    }
}
