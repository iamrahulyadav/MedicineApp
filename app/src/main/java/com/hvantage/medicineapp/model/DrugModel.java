package com.hvantage.medicineapp.model;

public class DrugModel {
    String key, name, manufacturer, product_type, category_name, power, qty, price, desciption;
    boolean prescription_required;

    public DrugModel(String key, String name, String manufacturer, String product_type, String category_name, String power, String qty, String price, String desciption, boolean prescription_required) {
        this.key = key;
        this.name = name;
        this.manufacturer = manufacturer;
        this.product_type = product_type;
        this.category_name = category_name;
        this.power = power;
        this.qty = qty;
        this.price = price;
        this.desciption = desciption;
        this.prescription_required = prescription_required;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public boolean getPrescription_required() {
        return prescription_required;
    }

    public void setPrescription_required(boolean prescription_required) {
        this.prescription_required = prescription_required;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "DrugModel{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", product_type='" + product_type + '\'' +
                ", category_name='" + category_name + '\'' +
                ", power='" + power + '\'' +
                ", qty='" + qty + '\'' +
                ", price='" + price + '\'' +
                ", desciption='" + desciption + '\'' +
                ", prescription_required='" + prescription_required + '\'' +
                '}';
    }
}
