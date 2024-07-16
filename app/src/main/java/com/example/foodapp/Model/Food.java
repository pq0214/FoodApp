package com.example.foodapp.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties //ignore all other properties that are not define by this class
public class Food {
    private String foodid;
    private String foodstall;
    private String foodcategory;
    private String foodname;
    private String foodimage;
    private String fooddesc;
    private double foodprice;
    public Food() {
    }
    public Food(String foodid,String foodstall, String foodcategory,String foodname, String foodimage,String fooddesc, double price) {
        this.foodid=foodid;
        this.foodstall=foodstall;
        this.foodcategory=foodcategory;
        this.foodname = foodname;
        this.foodimage=foodimage;
        this.fooddesc = fooddesc;
        this.foodprice = price;
    }

    public String getFoodname() { //generate->getter
        return foodname;
    }

    public String getFooddesc() {
        return fooddesc;
    }
    public String getFoodid(){return foodid;}

    public String getFoodcategory() {
        return foodcategory;
    }

    public String getFoodimage() {
        return foodimage;
    }

    public double getFoodprice() {
        return foodprice;
    }

    public String getFoodstall() {
        return foodstall;
    }
}
