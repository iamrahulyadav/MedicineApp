package com.hvantage.medicineapp.model;

import java.io.Serializable;

public class ProductModel implements Serializable {
    String key, name, manufacturer, product_type, category_name = "", sub_category_name = "", power, qty, description, image = "";
    double price;
    boolean prescription_required;
    private int total_available = 0;

    public ProductModel(String key, String name, String manufacturer, String product_type, String category_name, String sub_category_name, String power, String qty, String description, String image, double price, boolean prescription_required, int total_available) {
        this.key = key;
        this.name = name;
        this.manufacturer = manufacturer;
        this.product_type = product_type;
        this.category_name = category_name;
        this.sub_category_name = sub_category_name;
        this.power = power;
        this.qty = qty;
        this.description = description;
        this.image = image;
        this.price = price;
        this.prescription_required = prescription_required;
        this.total_available = total_available;
    }

    public ProductModel() {
    }

    public int getTotal_available() {
        return total_available;
    }

    public void setTotal_available(int total_available) {
        this.total_available = total_available;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPrescription_required() {
        return prescription_required;
    }

    public void setPrescription_required(boolean prescription_required) {
        this.prescription_required = prescription_required;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", product_type='" + product_type + '\'' +
                ", category_name='" + category_name + '\'' +
                ", sub_category_name='" + sub_category_name + '\'' +
                ", power='" + power + '\'' +
                ", qty='" + qty + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", prescription_required=" + prescription_required +
                ", total_available=" + total_available +
                '}';
    }
}
