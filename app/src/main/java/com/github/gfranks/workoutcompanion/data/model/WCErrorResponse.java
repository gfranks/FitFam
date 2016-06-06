package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Type;

public class WCErrorResponse extends Exception implements Parcelable, Type {

    public static final String EXTRA = "error_response";
    public static final Creator<WCErrorResponse> CREATOR = new Creator<WCErrorResponse>() {
        public WCErrorResponse createFromParcel(Parcel in) {
            return new WCErrorResponse(in);
        }

        public WCErrorResponse[] newArray(int size) {
            return new WCErrorResponse[size];
        }
    };

    private String message;

    public WCErrorResponse() {
    }

    public WCErrorResponse(String message) {
        this.message = message;
    }

    public WCErrorResponse(Parcel in) {
        readFromParcel(in);
    }

    public WCErrorResponse(Builder builder) {
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

        public WCErrorResponse build() {
            return new WCErrorResponse(this);
        }
    }
}
