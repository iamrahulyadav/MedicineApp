package com.hvantage.medicineapp.model;

import java.io.Serializable;

public class DoctorModel implements Serializable {
    String key, name, email, mobile_no, gender, specialization, address;

    public DoctorModel() {
    }

    public DoctorModel(String key, String name, String email, String mobile_no, String gender, String specialization, String address) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.mobile_no = mobile_no;
        this.gender = gender;
        this.specialization = specialization;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "DoctorModel{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile_no='" + mobile_no + '\'' +
                ", gender='" + gender + '\'' +
                ", specialization='" + specialization + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
