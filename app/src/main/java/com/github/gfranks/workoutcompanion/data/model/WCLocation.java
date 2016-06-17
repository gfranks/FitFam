package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class WCLocation implements Parcelable, Type {

    public static final Parcelable.Creator<WCLocation> CREATOR = new Parcelable.Creator<WCLocation>() {
        public WCLocation createFromParcel(Parcel in) {
            return new WCLocation(in);
        }

        public WCLocation[] newArray(int size) {
            return new WCLocation[size];
        }
    };

    @SerializedName("formatted_address")
    private String formatted_address;
    @SerializedName("geometry")
    private WCGymGeometry geometry;

    public WCLocation(Parcel in) {
        readFromParcel(in);
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public WCGymGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(WCGymGeometry geometry) {
        this.geometry = geometry;
    }

    public LatLng getPosition() {
        return geometry.getLocation().getLatLng();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(formatted_address);
        out.writeValue(geometry);
    }

    private void readFromParcel(Parcel in) {
        formatted_address = (String) in.readValue(String.class.getClassLoader());
        geometry = (WCGymGeometry) in.readValue(WCGymGeometry.class.getClassLoader());
    }
}
