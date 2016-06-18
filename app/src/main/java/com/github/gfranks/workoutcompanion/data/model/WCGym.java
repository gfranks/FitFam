package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.lang.reflect.Type;
import java.util.List;

public class WCGym implements ClusterItem, Parcelable, Type {

    public static final String EXTRA = "discover_result";

    public static final Parcelable.Creator<WCGym> CREATOR = new Parcelable.Creator<WCGym>() {
        public WCGym createFromParcel(Parcel in) {
            return new WCGym(in);
        }

        public WCGym[] newArray(int size) {
            return new WCGym[size];
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
    private WCGymGeometry geometry;
    @SerializedName("formatted_address")
    private String formatted_address;
    @SerializedName("formatted_phone_number")
    private String formatted_phone_number;
    @SerializedName("international_phone_number")
    private String international_phone_number;
    @SerializedName("url")
    private String url;
    @SerializedName("website")
    private String website;
    @SerializedName("opening_hours")
    private WCGymHours opening_hours;
    @SerializedName("rating")
    private float rating;
    @SerializedName("photos")
    private List<WCGymPhoto> photos;
    @SerializedName("reviews")
    private List<WCGymReview> reviews;

    public WCGym() {
    }

    public WCGym(Builder builder) {
        id = builder.id;
        place_id = builder.place_id;
        name = builder.name;
        icon = builder.icon;
        vicinity = builder.vicinity;
        geometry = builder.geometry;
        formatted_address = builder.formatted_address;
        formatted_phone_number = builder.formatted_phone_number;
        international_phone_number = builder.international_phone_number;
        url = builder.url;
        website = builder.website;
        opening_hours = builder.opening_hours;
        rating = builder.rating;
        photos = builder.photos;
        reviews = builder.reviews;
    }

    public WCGym(Parcel in) {
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

    public WCGymGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(WCGymGeometry geometry) {
        this.geometry = geometry;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public WCGymHours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(WCGymHours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<WCGymPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<WCGymPhoto> photos) {
        this.photos = photos;
    }

    public List<WCGymReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<WCGymReview> reviews) {
        this.reviews = reviews;
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
        out.writeValue(formatted_address);
        out.writeValue(formatted_phone_number);
        out.writeValue(international_phone_number);
        out.writeValue(url);
        out.writeValue(website);
        out.writeValue(opening_hours);
        out.writeValue(rating);
        out.writeList(photos);
        out.writeList(reviews);
    }

    protected void readFromParcel(Parcel in) {
        id = (String) in.readValue(String.class.getClassLoader());
        place_id = (String) in.readValue(String.class.getClassLoader());
        name = (String) in.readValue(String.class.getClassLoader());
        icon = (String) in.readValue(String.class.getClassLoader());
        vicinity = (String) in.readValue(String.class.getClassLoader());
        geometry = (WCGymGeometry) in.readValue(WCGymGeometry.class.getClassLoader());
        formatted_address = (String) in.readValue(String.class.getClassLoader());
        formatted_phone_number = (String) in.readValue(String.class.getClassLoader());
        international_phone_number = (String) in.readValue(String.class.getClassLoader());
        url = (String) in.readValue(String.class.getClassLoader());
        website = (String) in.readValue(String.class.getClassLoader());
        opening_hours = (WCGymHours) in.readValue(WCGymHours.class.getClassLoader());
        rating = (float) in.readValue(Float.class.getClassLoader());
        photos = in.readArrayList(WCGymPhoto.class.getClassLoader());
        reviews = in.readArrayList(WCGymReview.class.getClassLoader());
    }

    public static class Builder {

        private String id;
        private String place_id;
        private String name;
        private String icon;
        private String vicinity;
        private WCGymGeometry geometry;
        private String formatted_address;
        private String formatted_phone_number;
        private String international_phone_number;
        private String url;
        private String website;
        private WCGymHours opening_hours;
        private float rating;
        private List<WCGymPhoto> photos;
        private List<WCGymReview> reviews;

        public Builder() {
        }

        public Builder setId(String id) {
            this.id = id;

            return this;
        }

        public Builder setPlace_id(String place_id) {
            this.place_id = place_id;

            return this;
        }

        public Builder setName(String name) {
            this.name = name;

            return this;
        }

        public Builder setIcon(String icon) {
            this.icon = icon;

            return this;
        }

        public Builder setVicinity(String vicinity) {
            this.vicinity = vicinity;

            return this;
        }

        public Builder setGeometry(WCGymGeometry geometry) {
            this.geometry = geometry;

            return this;
        }

        public Builder setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;

            return this;
        }

        public Builder setFormatted_phone_number(String formatted_phone_number) {
            this.formatted_phone_number = formatted_phone_number;

            return this;
        }

        public Builder setInternational_phone_number(String international_phone_number) {
            this.international_phone_number = international_phone_number;

            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;

            return this;
        }

        public Builder setWebsite(String website) {
            this.website = website;

            return this;
        }

        public Builder setOpening_hours(WCGymHours opening_hours) {
            this.opening_hours = opening_hours;

            return this;
        }

        public Builder setRating(float rating) {
            this.rating = rating;

            return this;
        }

        public Builder setPhotos(List<WCGymPhoto> photos) {
            this.photos = photos;

            return this;
        }

        public Builder setReviews(List<WCGymReview> reviews) {
            this.reviews = reviews;

            return this;
        }

        public WCGym build() {
            return new WCGym(this);
        }
    }
}
