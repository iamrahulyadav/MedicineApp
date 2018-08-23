package com.hvantage.medicineapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PreMedicineData {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("doses")
    @Expose
    private String doses;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("quantity")
    @Expose
    private String quantity;

    public String getDoses_id() {
        return doses_id;
    }

    public void setDoses_id(String doses_id) {
        this.doses_id = doses_id;
    }

    @SerializedName("doses_id")
    @Expose
    private String doses_id;

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
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", doses='" + doses + '\'' +
                ", description='" + description + '\'' +
                ", quantity='" + quantity + '\'' +
                ", doses_id='" + doses_id + '\'' +
                '}';
    }
}

