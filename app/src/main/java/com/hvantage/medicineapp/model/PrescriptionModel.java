package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PrescriptionModel implements Parcelable {
    public static final Parcelable.Creator<PrescriptionModel> CREATOR = new Parcelable.Creator<PrescriptionModel>() {
        @Override
        public PrescriptionModel createFromParcel(Parcel source) {
            return new PrescriptionModel(source);
        }

        @Override
        public PrescriptionModel[] newArray(int size) {
            return new PrescriptionModel[size];
        }
    };
    String key;
    String image_base64;
    String title = "";
    String description = "";
    String date_time = "";
    ArrayList<CartModel> item_list = new ArrayList<CartModel>();

    public PrescriptionModel(String key, String image_base64) {
        this.key = key;
        this.image_base64 = image_base64;
    }


    public PrescriptionModel(String key, String image_base64, String title, String description, String date_time) {
        this.key = key;
        this.image_base64 = image_base64;
        this.title = title;
        this.description = description;
        this.date_time = date_time;
    }

    public PrescriptionModel() {
    }

    protected PrescriptionModel(Parcel in) {
        this.key = in.readString();
        this.image_base64 = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.date_time = in.readString();
    }

    public ArrayList<CartModel> getItem_list() {
        return item_list;
    }

    public void setItem_list(ArrayList<CartModel> item_list) {
        this.item_list = item_list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImage_base64() {
        return image_base64;
    }

    public void setImage_base64(String image_base64) {
        this.image_base64 = image_base64;
    }

    @Override
    public String toString() {
        return "PrescriptionModel{" +
                "key='" + key + '\'' +
                ", image_base64='" + image_base64 + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.image_base64);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.date_time);
    }
}
