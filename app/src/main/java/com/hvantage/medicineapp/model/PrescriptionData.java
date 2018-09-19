package com.hvantage.medicineapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrescriptionData implements Parcelable {

    @SerializedName("prescription_id")
    @Expose
    private String prescription_id;
    @SerializedName("prescription_title")
    @Expose
    private String prescription_title = "";
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("doctor_details")
    @Expose
    private DoctorDetails doctorDetails;
    @SerializedName("patient_details")
    @Expose
    private PatientDetails patientDetails;
    @SerializedName("medicine_details")
    @Expose
    private ArrayList<PreMedicineData> medicineDetails = null;
    @SerializedName("diagnosis_details")
    @Expose
    private String diagnosisDetails;
    @SerializedName("notes")
    @Expose
    private String notes;

    public PrescriptionData() {
    }


    public String getPrescription_id() {
        return prescription_id;
    }

    public void setPrescription_id(String prescription_id) {
        this.prescription_id = prescription_id;
    }

    public String getPrescription_title() {
        return prescription_title;
    }

    public void setPrescription_title(String prescription_title) {
        this.prescription_title = prescription_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public DoctorDetails getDoctorDetails() {
        return doctorDetails;
    }

    public void setDoctorDetails(DoctorDetails doctorDetails) {
        this.doctorDetails = doctorDetails;
    }

    public PatientDetails getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(PatientDetails patientDetails) {
        this.patientDetails = patientDetails;
    }

    public ArrayList<PreMedicineData> getMedicineDetails() {
        return medicineDetails;
    }

    public void setMedicineDetails(ArrayList<PreMedicineData> medicineDetails) {
        this.medicineDetails = medicineDetails;
    }

    public String getDiagnosisDetails() {
        return diagnosisDetails;
    }

    public void setDiagnosisDetails(String diagnosisDetails) {
        this.diagnosisDetails = diagnosisDetails;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PrescriptionData{" +
                "prescription_id='" + prescription_id + '\'' +
                ", prescription_title='" + prescription_title + '\'' +
                ", date='" + date + '\'' +
                ", image='" + image + '\'' +
                ", doctorDetails=" + doctorDetails +
                ", patientDetails=" + patientDetails +
                ", medicineDetails=" + medicineDetails +
                ", diagnosisDetails='" + diagnosisDetails + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.prescription_id);
        dest.writeString(this.prescription_title);
        dest.writeString(this.date);
        dest.writeString(this.image);
        dest.writeParcelable(this.doctorDetails, flags);
        dest.writeParcelable(this.patientDetails, flags);
        dest.writeList(this.medicineDetails);
        dest.writeString(this.diagnosisDetails);
        dest.writeString(this.notes);
    }

    protected PrescriptionData(Parcel in) {
        this.prescription_id = in.readString();
        this.prescription_title = in.readString();
        this.date = in.readString();
        this.image = in.readString();
        this.doctorDetails = in.readParcelable(DoctorDetails.class.getClassLoader());
        this.patientDetails = in.readParcelable(PatientDetails.class.getClassLoader());
        this.medicineDetails = new ArrayList<PreMedicineData>();
        in.readList(this.medicineDetails, PreMedicineData.class.getClassLoader());
        this.diagnosisDetails = in.readString();
        this.notes = in.readString();
    }

    public static final Creator<PrescriptionData> CREATOR = new Creator<PrescriptionData>() {
        @Override
        public PrescriptionData createFromParcel(Parcel source) {
            return new PrescriptionData(source);
        }

        @Override
        public PrescriptionData[] newArray(int size) {
            return new PrescriptionData[size];
        }
    };
}