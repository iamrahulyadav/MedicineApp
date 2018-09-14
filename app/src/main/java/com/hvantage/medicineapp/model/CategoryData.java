package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryData implements Parcelable {

    public static final Parcelable.Creator<CategoryData> CREATOR = new Parcelable.Creator<CategoryData>() {
        @Override
        public CategoryData createFromParcel(Parcel source) {
            return new CategoryData(source);
        }

        @Override
        public CategoryData[] newArray(int size) {
            return new CategoryData[size];
        }
    };
    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("cat_image")
    @Expose
    private String catImage;

    public CategoryData() {
    }

    protected CategoryData(Parcel in) {
        this.catId = in.readString();
        this.catName = in.readString();
        this.catImage = in.readString();
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String catImage) {
        this.catImage = catImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.catId);
        dest.writeString(this.catName);
        dest.writeString(this.catImage);
    }
}
