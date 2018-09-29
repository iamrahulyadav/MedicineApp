package com.hvantage.medicineapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SliderData {

    @SerializedName("slider_id")
    @Expose
    private String sliderId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("button_text")
    @Expose
    private String buttonText;
    @SerializedName("button_action")
    @Expose
    private String buttonAction;

    public String getSliderId() {
        return sliderId;
    }

    public void setSliderId(String sliderId) {
        this.sliderId = sliderId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonAction() {
        return buttonAction;
    }

    public void setButtonAction(String buttonAction) {
        this.buttonAction = buttonAction;
    }

}