package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class WCDiscoverResponse implements Parcelable, Type {

    public static final Parcelable.Creator<WCDiscoverResponse> CREATOR = new Parcelable.Creator<WCDiscoverResponse>() {
        public WCDiscoverResponse createFromParcel(Parcel in) {
            return new WCDiscoverResponse(in);
        }

        public WCDiscoverResponse[] newArray(int size) {
            return new WCDiscoverResponse[size];
        }
    };

    @SerializedName("status")
    private String status;
    @SerializedName("results")
    private List<WCDiscoverResult> results;
    @SerializedName("error_message")
    private String error_message;
    @SerializedName("html_attributions")
    private List<String> html_attributions;

    public WCDiscoverResponse() {
    }

    public WCDiscoverResponse(Parcel in) {
        readFromParcel(in);
    }

    public List<String> getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public List<WCDiscoverResult> getResults() {
        return results;
    }

    public void setResults(List<WCDiscoverResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
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
        out.writeValue(status);
        out.writeList(results);
        out.writeValue(error_message);
        out.writeValue(html_attributions);
    }

    protected void readFromParcel(Parcel in) {
        status = (String) in.readValue(String.class.getClassLoader());
        results = in.readArrayList(WCDiscoverResult.class.getClassLoader());
        error_message = (String) in.readValue(String.class.getClassLoader());
        html_attributions = in.readArrayList(String.class.getClassLoader());
    }
}
