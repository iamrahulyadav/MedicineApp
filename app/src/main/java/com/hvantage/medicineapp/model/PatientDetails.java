package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientDetails implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("gender")
    @Expose
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.age);
        dest.writeString(this.weight);
        dest.writeString(this.gender);
    }

    public PatientDetails() {
    }

    protected PatientDetails(Parcel in) {
        this.name = in.readString();
        this.age = in.readString();
        this.weight = in.readString();
        this.gender = in.readString();
    }

    public static final Parcelable.Creator<PatientDetails> CREATOR = new Parcelable.Creator<PatientDetails>() {
        @Override
        public PatientDetails createFromParcel(Parcel source) {
            return new PatientDetails(source);
        }

        @Override
        public PatientDetails[] newArray(int size) {
            return new PatientDetails[size];
        }
    };

    @Override
    public String toString() {
        return "PatientDetails{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", weight='" + weight + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}