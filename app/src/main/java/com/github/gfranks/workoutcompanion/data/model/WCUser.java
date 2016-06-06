package com.github.gfranks.workoutcompanion.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WCUser implements Parcelable, Type {

    public static final String EXTRA = "user";
    public static final String EXTRA_NEW = "new_user";
    public static final Parcelable.Creator<WCUser> CREATOR = new Parcelable.Creator<WCUser>() {
        public WCUser createFromParcel(Parcel in) {
            return new WCUser(in);
        }

        public WCUser[] newArray(int size) {
            return new WCUser[size];
        }
    };

    @SerializedName("id")
    private String id;
    @SerializedName("image")
    private String image;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("sex")
    private String sex;
    @SerializedName("weight")
    private int weight;
    @SerializedName("exercises")
    private List<String> exercises;
    @SerializedName("canSeeContactInfo")
    private boolean canSeeContactInfo;

    public WCUser() {
    }

    public WCUser(Parcel in) {
        readFromParcel(in);
    }

    public WCUser(Builder builder) {
        id = builder.id;
        image = builder.image;
        firstName = builder.firstName;
        lastName = builder.lastName;
        phoneNumber = builder.phoneNumber;
        birthday = builder.birthday;
        email = builder.email;
        password = builder.password;
        sex = builder.sex;
        weight = builder.weight;
        exercises = builder.exercises;
        canSeeContactInfo = builder.canSeeContactInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<String> getExercises() {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        return exercises;
    }

    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }

    public boolean isCanSeeContactInfo() {
        return canSeeContactInfo;
    }

    public void setCanSeeContactInfo(boolean canSeeContactInfo) {
        this.canSeeContactInfo = canSeeContactInfo;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getBirthdayMonth() {
        return Integer.parseInt(getBirthday().substring(0, 2));
    }

    public int getBirthdayDayOfMonth() {
        return Integer.parseInt(getBirthday().substring(3, 5));
    }

    public int getBirthdayYear() {
        return Integer.parseInt(getBirthday().substring(6));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WCUser) {
            return ((WCUser) o).getId().equals(getId());
        }
        return super.equals(o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(id);
        out.writeValue(image);
        out.writeValue(firstName);
        out.writeValue(lastName);
        out.writeValue(phoneNumber);
        out.writeValue(birthday);
        out.writeValue(email);
        out.writeValue(password);
        out.writeValue(sex);
        out.writeValue(weight);
        out.writeList(exercises);
        out.writeValue(canSeeContactInfo ? 1 : 0);
    }

    private void readFromParcel(Parcel in) {
        id = (String) in.readValue(String.class.getClassLoader());
        image = (String) in.readValue(String.class.getClassLoader());
        firstName = (String) in.readValue(String.class.getClassLoader());
        lastName = (String) in.readValue(String.class.getClassLoader());
        phoneNumber = (String) in.readValue(String.class.getClassLoader());
        birthday = (String) in.readValue(String.class.getClassLoader());
        email = (String) in.readValue(String.class.getClassLoader());
        password = (String) in.readValue(String.class.getClassLoader());
        sex = (String) in.readValue(String.class.getClassLoader());
        weight = (int) in.readValue(Integer.class.getClassLoader());
        exercises = in.readArrayList(String.class.getClassLoader());
        canSeeContactInfo = ((int) in.readValue(Integer.class.getClassLoader())) == 1;
    }

    public static class Builder {

        private final String id;
        private String image;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String birthday;
        private String email;
        private String password;
        private String sex;
        private int weight;
        private List<String> exercises;
        private boolean canSeeContactInfo;

        public Builder() {
            id = UUID.randomUUID().toString();
            exercises = new ArrayList<>();
        }

        public Builder setImage(String image) {
            this.image = image;

            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;

            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;

            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;

            return this;
        }

        public Builder setBirthday(String birthday) {
            this.birthday = birthday;

            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;

            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;

            return this;
        }

        public Builder setSex(String sex) {
            this.sex = sex;

            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;

            return this;
        }

        public Builder setExercises(List<String> exercises) {
            this.exercises = exercises;

            return this;
        }

        public Builder setCanSeeContactInfo(boolean canSeeContactInfo) {
            this.canSeeContactInfo = canSeeContactInfo;

            return this;
        }

        public WCUser build() {
            return new WCUser(this);
        }
    }
}
