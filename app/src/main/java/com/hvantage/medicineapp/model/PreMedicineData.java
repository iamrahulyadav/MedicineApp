package com.hvantage.medicineapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PreMedicineData implements Parcelable {

    @SerializedName("medicine_id")
    @Expose
    private String medicine_id = "0";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer = "";
    @SerializedName("doses")
    @Expose
    private String doses = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("quantity")
    @Expose
    private String quantity = "";
    @SerializedName("doses_id")
    @Expose
    private String doses_id = "";

    public String getPresc_medicine_id() {
        return medicine_id;
    }

    public void setPresc_medicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getDoses_id() {
        return doses_id;
    }

    public void setDoses_id(String doses_id) {
        this.doses_id = doses_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDoses() {
        return doses;
    }

    public void setDoses(String doses) {
        this.doses = doses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PreMedicineData{" +
                "medicine_id='" + medicine_id + '\'' +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", doses='" + doses + '\'' +
                ", description='" + description + '\'' +
                ", quantity='" + quantity + '\'' +
                ", doses_id='" + doses_id + '\'' +
                '}';
    }

    public PreMedicineData() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.medicine_id);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.manufacturer);
        dest.writeString(this.doses);
        dest.writeString(this.description);
        dest.writeString(this.quantity);
        dest.writeString(this.doses_id);
    }

    protected PreMedicineData(Parcel in) {
        this.medicine_id = in.readString();
        this.type = in.readString();
        this.name = in.readString();
        this.manufacturer = in.readString();
        this.doses = in.readString();
        this.description = in.readString();
        this.quantity = in.readString();
        this.doses_id = in.readString();
    }

    public static final Creator<PreMedicineData> CREATOR = new Creator<PreMedicineData>() {
        @Override
        public PreMedicineData createFromParcel(Parcel source) {
            return new PreMedicineData(source);
        }

        @Override
        public PreMedicineData[] newArray(int size) {
            return new PreMedicineData[size];
        }
    };
}
