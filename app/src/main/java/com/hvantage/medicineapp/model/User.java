package com.hvantage.medicineapp.model;

public class User {

    String uid = "", name = "", email = "", mob_no = "";

    public User(String uid,String name, String email, String mob_no) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mob_no = mob_no;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMob_no() {
        return mob_no;
    }

    public void setMob_no(String mob_no) {
        this.mob_no = mob_no;
    }
}
