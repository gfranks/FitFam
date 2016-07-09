package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class FFLocation implements Parcelable, Type {

    public static final String EXTRA = "location";
    public static final Parcelable.Creator<FFLocation> CREATOR = new Parcelable.Creator<FFLocation>() {
        public FFLocation createFromParcel(Parcel in) {
            return new FFLocation(in);
        }

        public FFLocation[] newArray(int size) {
            return new FFLocation[size];
        }
    };

    @SerializedName("formatted_address")
    private String formatted_address;
    @SerializedName("geometry")
    private FFGymGeometry geometry;

    public FFLocation() {
    }

    public FFLocation(Parcel in) {
        readFromParcel(in);
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public FFGymGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(FFGymGeometry geometry) {
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
        geometry = (FFGymGeometry) in.readValue(FFGymGeometry.class.getClassLoader());
    }
}
