package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoctorDetails implements Parcelable {

    public static final Parcelable.Creator<DoctorDetails> CREATOR = new Parcelable.Creator<DoctorDetails>() {
        @Override
        public DoctorDetails createFromParcel(Parcel source) {
            return new DoctorDetails(source);
        }

        @Override
        public DoctorDetails[] newArray(int size) {
            return new DoctorDetails[size];
        }
    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("email")
    @Expose
    private String email;

    public DoctorDetails() {
    }

    protected DoctorDetails(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.phoneNo = in.readString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.phoneNo);
    }

    @Override
    public String toString() {
        return "DoctorDetails{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
