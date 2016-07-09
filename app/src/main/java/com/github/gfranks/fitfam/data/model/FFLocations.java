package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class FFLocations implements Parcelable, Type {

    public static final String EXTRA = "locations";

    public static final Parcelable.Creator<FFLocations> CREATOR = new Parcelable.Creator<FFLocations>() {
        public FFLocations createFromParcel(Parcel in) {
            return new FFLocations(in);
        }

        public FFLocations[] newArray(int size) {
            return new FFLocations[size];
        }
    };

    @SerializedName("results")
    private List<FFLocation> results;

    public FFLocations() {
    }

    public FFLocations(Parcel in) {
        readFromParcel(in);
    }

    public List<FFLocation> getResults() {
        return results;
    }

    public void setResults(List<FFLocation> results) {
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
        results = in.readArrayList(FFLocation.class.getClassLoader());
    }
}
