package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressData implements Parcelable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("contact_no")
    @Expose
    private String contactNo;
    @SerializedName("is_default")
    @Expose
    private Boolean isDefault;
    @SerializedName("address_id")
    @Expose
    private String addressId;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("state")
    @Expose
    private String state;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AddressData{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", isDefault=" + isDefault +
                ", addressId='" + addressId + '\'' +
                ", landmark='" + landmark + '\'' +
                ", name='" + name + '\'' +
                ", pincode='" + pincode + '\'' +
                ", state='" + state + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.contactNo);
        dest.writeValue(this.isDefault);
        dest.writeString(this.addressId);
        dest.writeString(this.landmark);
        dest.writeString(this.name);
        dest.writeString(this.pincode);
        dest.writeString(this.state);
    }

    public AddressData() {
    }

    protected AddressData(Parcel in) {
        this.address = in.readString();
        this.city = in.readString();
        this.contactNo = in.readString();
        this.isDefault = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.addressId = in.readString();
        this.landmark = in.readString();
        this.name = in.readString();
        this.pincode = in.readString();
        this.state = in.readString();
    }

    public static final Parcelable.Creator<AddressData> CREATOR = new Parcelable.Creator<AddressData>() {
        @Override
        public AddressData createFromParcel(Parcel source) {
            return new AddressData(source);
        }

        @Override
        public AddressData[] newArray(int size) {
            return new AddressData[size];
        }
    };
}