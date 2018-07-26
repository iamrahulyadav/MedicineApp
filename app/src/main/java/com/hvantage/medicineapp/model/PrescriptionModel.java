package com.hvantage.medicineapp.model;

import java.io.Serializable;

public class PrescriptionModel implements Serializable {
    String key;
    String image_base64;
    String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    String description = "";
    String date_time = "";

    public PrescriptionModel(String key, String image_base64) {
        this.key = key;
        this.image_base64 = image_base64;
    }

    public PrescriptionModel(String key, String image_base64, String title, String description, String date_time) {
        this.key = key;
        this.image_base64 = image_base64;
        this.title = title;
        this.description = description;
        this.date_time = date_time;
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

    @Override
    public String toString() {
        return "PrescriptionModel{" +
                "key='" + key + '\'' +
                ", image_base64='" + image_base64 + '\'' +
                '}';
    }
}
