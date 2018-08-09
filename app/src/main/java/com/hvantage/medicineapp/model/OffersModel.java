package com.hvantage.medicineapp.model;

public class OffersModel {
    String coupon, title, validity, desciption, image;

    public OffersModel(String coupon, String title, String desciption, String validity, String image) {
        this.coupon = coupon;
        this.title = title;
        this.desciption = desciption;
        this.validity = validity;
        this.image = image;
    }

    public OffersModel() {
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
