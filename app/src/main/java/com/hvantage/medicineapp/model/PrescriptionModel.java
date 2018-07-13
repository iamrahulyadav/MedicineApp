package com.hvantage.medicineapp.model;

public class PrescriptionModel {
    String key, image_base64;

    public PrescriptionModel(String key, String image_base64) {
        this.key = key;
        this.image_base64 = image_base64;
    }

    public PrescriptionModel() {
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
}