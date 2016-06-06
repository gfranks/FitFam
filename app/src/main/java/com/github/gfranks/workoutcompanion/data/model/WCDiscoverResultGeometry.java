package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class WCDiscoverResultGeometry implements Parcelable, Type {

    public static final Parcelable.Creator<WCDiscoverResultGeometry> CREATOR = new Parcelable.Creator<WCDiscoverResultGeometry>() {
        public WCDiscoverResultGeometry createFromParcel(Parcel in) {
            return new WCDiscoverResultGeometry(in);
        }

        public WCDiscoverResultGeometry[] newArray(int size) {
            return new WCDiscoverResultGeometry[size];
        }
    };

    @SerializedName("location")
    private WCDiscoverResultGeometryLocation location;

    public WCDiscoverResultGeometry() {
    }

    public WCDiscoverResultGeometry(Parcel in) {
        readFromParcel(in);
    }

    public WCDiscoverResultGeometryLocation getLocation() {
        return location;
    }

    public void setLocation(WCDiscoverResultGeometryLocation location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(location);
    }

    protected void readFromParcel(Parcel in) {
        location = (WCDiscoverResultGeometryLocation) in.readValue(WCDiscoverResultGeometryLocation.class.getClassLoader());
    }
}