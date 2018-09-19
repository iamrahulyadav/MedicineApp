package com.hvantage.medicineapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("item_price")
    @Expose
    private String itemPrice;
    @SerializedName("is_prescription_required")
    @Expose
    private String isPrescriptionRequired;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("item_total_price")
    @Expose
    private String itemTotalPrice;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getIsPrescriptionRequired() {
        return isPrescriptionRequired;
    }

    public void setIsPrescriptionRequired(String isPrescriptionRequired) {
        this.isPrescriptionRequired = isPrescriptionRequired;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(String itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

}