package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class WCLocations implements Parcelable, Type {

    public static final Parcelable.Creator<WCLocations> CREATOR = new Parcelable.Creator<WCLocations>() {
        public WCLocations createFromParcel(Parcel in) {
            return new WCLocations(in);
        }

        public WCLocations[] newArray(int size) {
            return new WCLocations[size];
        }
    };

    @SerializedName("results")
    private List<WCLocation> results;

    public WCLocations(Parcel in) {
        readFromParcel(in);
    }

    public List<WCLocation> getResults() {
        return results;
    }

    public void setResults(List<WCLocation> results) {
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
        results = in.readArrayList(WCLocation.class.getClassLoader());
    }
}
