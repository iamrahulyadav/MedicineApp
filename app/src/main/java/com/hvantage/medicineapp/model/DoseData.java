package com.hvantage.medicineapp.model;

public class DoseData {
    String dose_id, dose;
    boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDose_id() {
        return dose_id;
    }

    public void setDose_id(String dose_id) {
        this.dose_id = dose_id;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public DoseData(String dose_id, String dose) {
        this.dose_id = dose_id;
        this.dose = dose;
    }
}
