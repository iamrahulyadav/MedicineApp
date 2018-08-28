package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportData implements Parcelable {

    @SerializedName("report_id")
    @Expose
    private String reportId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("title")
    @Expose
    private String title;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reportId);
        dest.writeString(this.image);
        dest.writeString(this.title);
    }

    public ReportData() {
    }

    protected ReportData(Parcel in) {
        this.reportId = in.readString();
        this.image = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<ReportData> CREATOR = new Parcelable.Creator<ReportData>() {
        @Override
        public ReportData createFromParcel(Parcel source) {
            return new ReportData(source);
        }

        @Override
        public ReportData[] newArray(int size) {
            return new ReportData[size];
        }
    };

    @Override
    public String toString() {
        return "ReportData{" +
                "reportId='" + reportId + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}