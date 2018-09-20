package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrderData implements Parcelable {

    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("order_type")
    @Expose
    private String orderType;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("payable_amount")
    @Expose
    private String payableAmount;
    @SerializedName("promo_code_applied")
    @Expose
    private String promoCodeApplied;
    @SerializedName("cod_charges")
    @Expose
    private String codCharges;
    @SerializedName("gst_tax_amt")
    @Expose
    private String gstTaxAmt;
    @SerializedName("other_tax_amt")
    @Expose
    private String otherTaxAmt;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("datdate_timee")
    @Expose
    private String datdateTimee;
    @SerializedName("prescription_data")
    @Expose
    private List<PrescriptionData> prescriptionData = null;
    @SerializedName("address_data")
    @Expose
    private AddressData addressData;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getPromoCodeApplied() {
        return promoCodeApplied;
    }

    public void setPromoCodeApplied(String promoCodeApplied) {
        this.promoCodeApplied = promoCodeApplied;
    }

    public String getCodCharges() {
        return codCharges;
    }

    public void setCodCharges(String codCharges) {
        this.codCharges = codCharges;
    }

    public String getGstTaxAmt() {
        return gstTaxAmt;
    }

    public void setGstTaxAmt(String gstTaxAmt) {
        this.gstTaxAmt = gstTaxAmt;
    }

    public String getOtherTaxAmt() {
        return otherTaxAmt;
    }

    public void setOtherTaxAmt(String otherTaxAmt) {
        this.otherTaxAmt = otherTaxAmt;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDatdateTimee() {
        return datdateTimee;
    }

    public void setDatdateTimee(String datdateTimee) {
        this.datdateTimee = datdateTimee;
    }

    public List<PrescriptionData> getPrescriptionData() {
        return prescriptionData;
    }

    public void setPrescriptionData(List<PrescriptionData> prescriptionData) {
        this.prescriptionData = prescriptionData;
    }

    public AddressData getAddressData() {
        return addressData;
    }

    public void setAddressData(AddressData addressData) {
        this.addressData = addressData;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderId);
        dest.writeString(this.address);
        dest.writeString(this.note);
        dest.writeString(this.orderType);
        dest.writeString(this.orderStatus);
        dest.writeString(this.totalAmount);
        dest.writeString(this.paymentMode);
        dest.writeString(this.paymentType);
        dest.writeString(this.payableAmount);
        dest.writeString(this.promoCodeApplied);
        dest.writeString(this.codCharges);
        dest.writeString(this.gstTaxAmt);
        dest.writeString(this.otherTaxAmt);
        dest.writeString(this.paymentStatus);
        dest.writeString(this.datdateTimee);
        dest.writeTypedList(this.prescriptionData);
        dest.writeParcelable(this.addressData, flags);
        dest.writeList(this.items);
    }

    public OrderData() {
    }

    protected OrderData(Parcel in) {
        this.orderId = in.readString();
        this.address = in.readString();
        this.note = in.readString();
        this.orderType = in.readString();
        this.orderStatus = in.readString();
        this.totalAmount = in.readString();
        this.paymentMode = in.readString();
        this.paymentType = in.readString();
        this.payableAmount = in.readString();
        this.promoCodeApplied = in.readString();
        this.codCharges = in.readString();
        this.gstTaxAmt = in.readString();
        this.otherTaxAmt = in.readString();
        this.paymentStatus = in.readString();
        this.datdateTimee = in.readString();
        this.prescriptionData = in.createTypedArrayList(PrescriptionData.CREATOR);
        this.addressData = in.readParcelable(AddressData.class.getClassLoader());
        this.items = new ArrayList<Item>();
        in.readList(this.items, Object.class.getClassLoader());
    }

    public static final Creator<OrderData> CREATOR = new Creator<OrderData>() {
        @Override
        public OrderData createFromParcel(Parcel source) {
            return new OrderData(source);
        }

        @Override
        public OrderData[] newArray(int size) {
            return new OrderData[size];
        }
    };
}
