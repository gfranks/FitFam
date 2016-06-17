package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class WCGymGeometry implements Parcelable, Type {

    public static final Parcelable.Creator<WCGymGeometry> CREATOR = new Parcelable.Creator<WCGymGeometry>() {
        public WCGymGeometry createFromParcel(Parcel in) {
            return new WCGymGeometry(in);
        }

        public WCGymGeometry[] newArray(int size) {
            return new WCGymGeometry[size];
        }
    };

    @SerializedName("location")
    private WCGymGeometryLocation location;

    public WCGymGeometry() {
    }

    public WCGymGeometry(Parcel in) {
        readFromParcel(in);
    }

    public WCGymGeometryLocation getLocation() {
        return location;
    }

    public void setLocation(WCGymGeometryLocation location) {
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
        location = (WCGymGeometryLocation) in.readValue(WCGymGeometryLocation.class.getClassLoader());
    }
}