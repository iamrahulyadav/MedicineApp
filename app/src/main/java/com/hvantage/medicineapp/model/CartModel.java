package com.hvantage.medicineapp.model;

import java.io.Serializable;

public class CartModel implements Serializable {
    String key;
    int qty_no = 1;
    String item;
    String image = "";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String qty;
    String item_price = "0", item_total_price = "0";

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public String getItem_total_price() {
        return item_total_price;
    }

    public void setItem_total_price(String item_total_price) {
        this.item_total_price = item_total_price;
    }

    public CartModel(String key, int qty_no) {
        this.key = key;
        this.qty_no = qty_no;
    }

    public CartModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getQty_no() {
        return qty_no;
    }

    public void setQty_no(int qty_no) {
        this.qty_no = qty_no;
    }

    @Override
    public String toString() {
        return "CartModel{" +
                "key='" + key + '\'' +
                ", qty_no=" + qty_no +
                ", item='" + item + '\'' +
                ", image='" + image + '\'' +
                ", qty='" + qty + '\'' +
                ", item_price=" + item_price +
                ", item_total_price=" + item_total_price +
                '}';
    }
}
