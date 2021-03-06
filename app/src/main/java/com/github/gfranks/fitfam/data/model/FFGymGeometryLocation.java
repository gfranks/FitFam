package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class FFGymGeometryLocation implements Parcelable, Type {

    public static final Parcelable.Creator<FFGymGeometryLocation> CREATOR = new Parcelable.Creator<FFGymGeometryLocation>() {
        public FFGymGeometryLocation createFromParcel(Parcel in) {
            return new FFGymGeometryLocation(in);
        }

        public FFGymGeometryLocation[] newArray(int size) {
            return new FFGymGeometryLocation[size];
        }
    };

    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    public FFGymGeometryLocation() {
    }

    public FFGymGeometryLocation(Parcel in) {
        readFromParcel(in);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
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
        out.writeValue(lat);
        out.writeValue(lng);
    }

    protected void readFromParcel(Parcel in) {
        lat = (double) in.readValue(Double.class.getClassLoader());
        lng = (double) in.readValue(Double.class.getClassLoader());
    }
}