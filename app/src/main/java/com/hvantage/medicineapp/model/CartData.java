package com.hvantage.medicineapp.model;

import java.io.Serializable;

public class CartData implements Serializable {
    String id = "";
    String item_id = "";
    String item = "";
    String image = "";
    int qty = 0;
    double item_price = 0.0, item_total_price = 0.0;
    boolean is_prescription_required = false;

    public CartData() {
    }

    public CartData(String item_id, String item, String image, int qty, double item_price, double item_total_price, boolean is_prescription_required) {
        this.item_id = item_id;
        this.item = item;
        this.image = image;
        this.qty = qty;
        this.item_price = item_price;
        this.item_total_price = item_total_price;
        this.is_prescription_required = is_prescription_required;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getItem_price() {
        return item_price;
    }

    public void setItem_price(double item_price) {
        this.item_price = item_price;
    }

    public double getItem_total_price() {
        return item_total_price;
    }

    public void setItem_total_price(double item_total_price) {
        this.item_total_price = item_total_price;
    }

    public boolean isIs_prescription_required() {
        return is_prescription_required;
    }

    public void setIs_prescription_required(boolean is_prescription_required) {
        this.is_prescription_required = is_prescription_required;
    }

    @Override
    public String toString() {
        return "CartData{" +
                "id='" + id + '\'' +
                "item_id='" + item_id + '\'' +
                ", item='" + item + '\'' +
                ", image='" + image + '\'' +
                ", qty=" + qty +
                ", item_price=" + item_price +
                ", item_total_price=" + item_total_price +
                ", is_prescription_required=" + is_prescription_required +
                '}';
    }
}
