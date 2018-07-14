package com.hvantage.medicineapp.model;

import java.io.Serializable;

public class FamilyModel implements Serializable {
    String key, name, email, mobile_no, relation, gender, blood_group, height, weight, known_allergies, image;

    public FamilyModel() {
    }

    public FamilyModel(String key, String name, String email, String mobile_no, String relation, String gender, String blood_group, String height, String weight, String known_allergies, String image) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.mobile_no = mobile_no;
        this.relation = relation;
        this.gender = gender;
        this.blood_group = blood_group;
        this.height = height;
        this.weight = weight;
        this.known_allergies = known_allergies;
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "FamilyModel{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile_no='" + mobile_no + '\'' +
                ", relation='" + relation + '\'' +
                ", gender='" + gender + '\'' +
                ", blood_group='" + blood_group + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", known_allergies='" + known_allergies + '\'' +
                ", image='" + image + '\'' +
                '}';
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

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getKnown_allergies() {
        return known_allergies;
    }

    public void setKnown_allergies(String known_allergies) {
        this.known_allergies = known_allergies;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
