package com.github.gfranks.fitfam.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class FFGyms implements Parcelable, Type {

    public static final Parcelable.Creator<FFGyms> CREATOR = new Parcelable.Creator<FFGyms>() {
        public FFGyms createFromParcel(Parcel in) {
            return new FFGyms(in);
        }

        public FFGyms[] newArray(int size) {
            return new FFGyms[size];
        }
    };

    @SerializedName("status")
    private String status;
    @SerializedName("result")
    private FFGym result;
    @SerializedName("results")
    private List<FFGym> results;
    @SerializedName("error_message")
    private String error_message;
    @SerializedName("html_attributions")
    private List<String> html_attributions;

    public FFGyms() {
    }

    public FFGyms(Parcel in) {
        readFromParcel(in);
    }

    public List<String> getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public FFGym getResult() {
        return result;
    }

    public void setResult(FFGym result) {
        this.result = result;
    }

    public List<FFGym> getResults() {
        return results;
    }

    public void setResults(List<FFGym> results) {
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
        out.writeValue(result);
        out.writeList(results);
        out.writeValue(error_message);
        out.writeValue(html_attributions);
    }

    protected void readFromParcel(Parcel in) {
        status = (String) in.readValue(String.class.getClassLoader());
        result = (FFGym) in.readValue(FFGym.class.getClassLoader());
        results = in.readArrayList(FFGym.class.getClassLoader());
        error_message = (String) in.readValue(String.class.getClassLoader());
        html_attributions = in.readArrayList(String.class.getClassLoader());
    }
}
