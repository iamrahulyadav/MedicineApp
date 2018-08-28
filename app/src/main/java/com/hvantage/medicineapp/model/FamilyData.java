package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FamilyData implements Parcelable {

    @SerializedName("family_member_id")
    @Expose
    private String familyMemberId;
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("known_allergies")
    @Expose
    private String knownAllergies;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("relation")
    @Expose
    private String relation;
    @SerializedName("weight")
    @Expose
    private String weight;

    public String getFamilyMemberId() {
        return familyMemberId;
    }

    public void setFamilyMemberId(String familyMemberId) {
        this.familyMemberId = familyMemberId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public void setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.familyMemberId);
        dest.writeString(this.bloodGroup);
        dest.writeString(this.email);
        dest.writeString(this.gender);
        dest.writeString(this.height);
        dest.writeString(this.image);
        dest.writeString(this.knownAllergies);
        dest.writeString(this.mobileNo);
        dest.writeString(this.name);
        dest.writeString(this.relation);
        dest.writeString(this.weight);
    }

    public FamilyData() {
    }

    protected FamilyData(Parcel in) {
        this.familyMemberId = in.readString();
        this.bloodGroup = in.readString();
        this.email = in.readString();
        this.gender = in.readString();
        this.height = in.readString();
        this.image = in.readString();
        this.knownAllergies = in.readString();
        this.mobileNo = in.readString();
        this.name = in.readString();
        this.relation = in.readString();
        this.weight = in.readString();
    }

    public static final Parcelable.Creator<FamilyData> CREATOR = new Parcelable.Creator<FamilyData>() {
        @Override
        public FamilyData createFromParcel(Parcel source) {
            return new FamilyData(source);
        }

        @Override
        public FamilyData[] newArray(int size) {
            return new FamilyData[size];
        }
    };

    @Override
    public String toString() {
        return "FamilyData{" +
                "familyMemberId='" + familyMemberId + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", height='" + height + '\'' +
                ", image='" + image + '\'' +
                ", knownAllergies='" + knownAllergies + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", name='" + name + '\'' +
                ", relation='" + relation + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}