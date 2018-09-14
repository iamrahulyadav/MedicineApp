package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubCategoryData implements Parcelable {

    public static final Creator<SubCategoryData> CREATOR = new Creator<SubCategoryData>() {
        @Override
        public SubCategoryData createFromParcel(Parcel source) {
            return new SubCategoryData(source);
        }

        @Override
        public SubCategoryData[] newArray(int size) {
            return new SubCategoryData[size];
        }
    };
    @SerializedName("sub_cat_id")
    @Expose
    private String subCatId;
    @SerializedName("sub_cat_name")
    @Expose
    private String subCatName;
    @SerializedName("sub_cat_image")
    @Expose
    private String subCatImage;

    public SubCategoryData() {
    }

    protected SubCategoryData(Parcel in) {
        this.subCatId = in.readString();
        this.subCatName = in.readString();
        this.subCatImage = in.readString();
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getSubCatImage() {
        return subCatImage;
    }

    public void setSubCatImage(String subCatImage) {
        this.subCatImage = subCatImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subCatId);
        dest.writeString(this.subCatName);
        dest.writeString(this.subCatImage);
    }
}
