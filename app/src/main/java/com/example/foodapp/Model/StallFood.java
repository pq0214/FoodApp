package com.example.foodapp.Model;

public class StallFood {
    private String childname, childImage;

    public StallFood(){}

    public StallFood(String childname, String childImage) {
        this.childname = childname;
        this.childImage = childImage;
    }

    public String getChildname() {
        return childname;
    }

    public void setChildname(String childname) {
        this.childname = childname;
    }

    public String getChildImage() {
        return childImage;
    }

    public void setChildImage(String childImage) {
        this.childImage = childImage;
    }
}
