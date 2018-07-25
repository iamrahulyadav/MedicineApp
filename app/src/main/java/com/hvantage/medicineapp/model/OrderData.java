package com.hvantage.medicineapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderData implements Serializable {

    private AddressModel delivery_details;
    private ArrayList<PrescriptionModel> prescriptions = new ArrayList<PrescriptionModel>();
    private ArrayList<CartModel> items = new ArrayList<CartModel>();
    private String item_total;
    private String delivery_fee;
    private String taxes;
    private String total_amount;
    private String payable_amount;
    private String date;
    private String time;

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    private String payment_mode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    private String by;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public OrderData() {
    }

    public AddressModel getDelivery_details() {
        return delivery_details;
    }

    public void setDelivery_details(AddressModel delivery_details) {
        this.delivery_details = delivery_details;
    }

    public ArrayList<PrescriptionModel> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(ArrayList<PrescriptionModel> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public ArrayList<CartModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<CartModel> items) {
        this.items = items;
    }

    public String getItem_total() {
        return item_total;
    }

    public void setItem_total(String item_total) {
        this.item_total = item_total;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayable_amount() {
        return payable_amount;
    }

    public void setPayable_amount(String payable_amount) {
        this.payable_amount = payable_amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
