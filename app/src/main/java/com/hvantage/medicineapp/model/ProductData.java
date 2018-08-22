package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductData implements Parcelable {

    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("sub_category_name")
    @Expose
    private String subCategoryName;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("long_description")
    @Expose
    private String longDescription;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("power")
    @Expose
    private String power;
    @SerializedName("prescription_required")
    @Expose
    private Boolean prescriptionRequired;
    @SerializedName("price_mrp")
    @Expose
    private String priceMrp;
    @SerializedName("price_discount")
    @Expose
    private String priceDiscount;
    @SerializedName("discount_percentage")
    @Expose
    private String discountPercentage;
    @SerializedName("discount_text")
    @Expose
    private String discountText;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("packaging_contain")
    @Expose
    private String packagingContain;
    @SerializedName("total_available")
    @Expose
    private int totalAvailable;


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public Boolean getPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(Boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    public String getPriceMrp() {
        return priceMrp;
    }

    public void setPriceMrp(String priceMrp) {
        this.priceMrp = priceMrp;
    }

    public String getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(String priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPackagingContain() {
        return packagingContain;
    }

    public void setPackagingContain(String packagingContain) {
        this.packagingContain = packagingContain;
    }

    public int getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(int totalAvailable) {
        this.totalAvailable = totalAvailable;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.categoryName);
        dest.writeString(this.subCategoryName);
        dest.writeString(this.shortDescription);
        dest.writeString(this.longDescription);
        dest.writeString(this.image);
        dest.writeString(this.productId);
        dest.writeString(this.manufacturer);
        dest.writeString(this.name);
        dest.writeString(this.power);
        dest.writeValue(this.prescriptionRequired);
        dest.writeString(this.priceMrp);
        dest.writeString(this.priceDiscount);
        dest.writeString(this.discountPercentage);
        dest.writeString(this.discountText);
        dest.writeString(this.productType);
        dest.writeString(this.packagingContain);
        dest.writeInt(this.totalAvailable);
    }

    public ProductData() {
    }

    protected ProductData(Parcel in) {
        this.categoryName = in.readString();
        this.subCategoryName = in.readString();
        this.shortDescription = in.readString();
        this.longDescription = in.readString();
        this.image = in.readString();
        this.productId = in.readString();
        this.manufacturer = in.readString();
        this.name = in.readString();
        this.power = in.readString();
        this.prescriptionRequired = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.priceMrp = in.readString();
        this.priceDiscount = in.readString();
        this.discountPercentage = in.readString();
        this.discountText = in.readString();
        this.productType = in.readString();
        this.packagingContain = in.readString();
        this.totalAvailable = in.readInt();
    }

    public static final Parcelable.Creator<ProductData> CREATOR = new Parcelable.Creator<ProductData>() {
        @Override
        public ProductData createFromParcel(Parcel source) {
            return new ProductData(source);
        }

        @Override
        public ProductData[] newArray(int size) {
            return new ProductData[size];
        }
    };
}