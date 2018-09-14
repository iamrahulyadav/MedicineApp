package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoctorData implements Parcelable {

    public static final Parcelable.Creator<DoctorData> CREATOR = new Parcelable.Creator<DoctorData>() {
        @Override
        public DoctorData createFromParcel(Parcel source) {
            return new DoctorData(source);
        }

        @Override
        public DoctorData[] newArray(int size) {
            return new DoctorData[size];
        }
    };
    @SerializedName("doctor_id")
    @Expose
    private String doctorId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("specialization")
    @Expose
    private String specialization;

    public DoctorData() {
    }

    protected DoctorData(Parcel in) {
        this.doctorId = in.readString();
        this.address = in.readString();
        this.email = in.readString();
        this.gender = in.readString();
        this.mobileNo = in.readString();
        this.name = in.readString();
        this.specialization = in.readString();
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.doctorId);
        dest.writeString(this.address);
        dest.writeString(this.email);
        dest.writeString(this.gender);
        dest.writeString(this.mobileNo);
        dest.writeString(this.name);
        dest.writeString(this.specialization);
    }

    @Override
    public String toString() {
        return "DoctorData{" +
                "doctorId='" + doctorId + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}