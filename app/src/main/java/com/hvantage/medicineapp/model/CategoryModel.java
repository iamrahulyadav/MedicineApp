package com.hvantage.medicineapp.model;

public class CategoryModel {

    String name = "";
    int img;
    int id;

    public CategoryModel(int i, String ayurvedic, int cat_ayurvedic) {
        id = i;
        name = ayurvedic;
        img = cat_ayurvedic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "name='" + name + '\'' +
                ", img=" + img +
                ", id=" + id +
                '}';
    }
}
