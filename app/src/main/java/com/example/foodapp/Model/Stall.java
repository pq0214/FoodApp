package com.example.foodapp.Model;

import java.util.List;

public class Stall {
    private String stallName, stallImage;
    public Stall(){

    }

    public Stall(String stallName, String stallImage) {
        this.stallName = stallName;
        this.stallImage = stallImage;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public String getStallImage() {
        return stallImage;
    }

    public void setStallImage(String stallImage) {
        this.stallImage = stallImage;
    }
}
