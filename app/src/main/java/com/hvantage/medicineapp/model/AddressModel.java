package com.hvantage.medicineapp.model;

public class AddressModel {
    public AddressModel(String key, String pincode, String address, String landmark, String city, String state, String is_default) {
        this.key = key;
        this.pincode = pincode;
        this.address = address;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.is_default = is_default;
    }

    String key, pincode, address, landmark, city, state, is_default;

    public AddressModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "key='" + key + '\'' +
                ", pincode='" + pincode + '\'' +
                ", address='" + address + '\'' +
                ", landmark='" + landmark + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", is_default='" + is_default + '\'' +
                '}';
    }
}
