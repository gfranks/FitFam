package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.lang.reflect.Type;

public class WCDiscoverResult implements ClusterItem, Parcelable, Type {

    public static final Parcelable.Creator<WCDiscoverResult> CREATOR = new Parcelable.Creator<WCDiscoverResult>() {
        public WCDiscoverResult createFromParcel(Parcel in) {
            return new WCDiscoverResult(in);
        }

        public WCDiscoverResult[] newArray(int size) {
            return new WCDiscoverResult[size];
        }
    };

    @SerializedName("id")
    private String id;
    @SerializedName("place_id")
    private String place_id;
    @SerializedName("name")
    private String name;
    @SerializedName("icon")
    private String icon;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("geometry")
    private WCDiscoverResultGeometry geometry;

    public WCDiscoverResult() {
    }

    public WCDiscoverResult(Parcel in) {
        readFromParcel(in);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public WCDiscoverResultGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(WCDiscoverResultGeometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public LatLng getPosition() {
        return geometry.getLocation().getLatLng();
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
        out.writeValue(id);
        out.writeValue(place_id);
        out.writeValue(name);
        out.writeValue(icon);
        out.writeValue(vicinity);
        out.writeValue(geometry);
    }

    protected void readFromParcel(Parcel in) {
        id = (String) in.readValue(String.class.getClassLoader());
        place_id = (String) in.readValue(String.class.getClassLoader());
        name = (String) in.readValue(String.class.getClassLoader());
        icon = (String) in.readValue(String.class.getClassLoader());
        vicinity = (String) in.readValue(String.class.getClassLoader());
        geometry = (WCDiscoverResultGeometry) in.readValue(WCDiscoverResultGeometry.class.getClassLoader());
    }
}
