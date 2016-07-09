package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class FFGymGeometry implements Parcelable, Type {

    public static final Parcelable.Creator<FFGymGeometry> CREATOR = new Parcelable.Creator<FFGymGeometry>() {
        public FFGymGeometry createFromParcel(Parcel in) {
            return new FFGymGeometry(in);
        }

        public FFGymGeometry[] newArray(int size) {
            return new FFGymGeometry[size];
        }
    };

    @SerializedName("location")
    private FFGymGeometryLocation location;

    public FFGymGeometry() {
    }

    public FFGymGeometry(Parcel in) {
        readFromParcel(in);
    }

    public FFGymGeometryLocation getLocation() {
        return location;
    }

    public void setLocation(FFGymGeometryLocation location) {
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
        location = (FFGymGeometryLocation) in.readValue(FFGymGeometryLocation.class.getClassLoader());
    }
}