package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class WCLocationResponse implements Parcelable, Type {

    public static final Parcelable.Creator<WCLocationResponse> CREATOR = new Parcelable.Creator<WCLocationResponse>() {
        public WCLocationResponse createFromParcel(Parcel in) {
            return new WCLocationResponse(in);
        }

        public WCLocationResponse[] newArray(int size) {
            return new WCLocationResponse[size];
        }
    };

    @SerializedName("results")
    private List<WCLocationResult> results;

    public WCLocationResponse(Parcel in) {
        readFromParcel(in);
    }

    public List<WCLocationResult> getResults() {
        return results;
    }

    public void setResults(List<WCLocationResult> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(results);
    }

    private void readFromParcel(Parcel in) {
        results = in.readArrayList(WCLocationResult.class.getClassLoader());
    }
}
