package com.hvantage.medicineapp.model;

public class ItemModel {

    String product_id, product_name, product_price, product_qty, product_img;

    public ItemModel(String product_id, String product_name, String product_price, String product_qty, String product_img) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_qty = product_qty;
        this.product_img = product_img;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    @Override
    public String toString() {
        return "ItemModel{" +
                "product_id='" + product_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_price='" + product_price + '\'' +
                ", product_qty='" + product_qty + '\'' +
                ", product_img='" + product_img + '\'' +
                '}';
    }
}
