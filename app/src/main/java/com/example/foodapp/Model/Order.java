package com.example.foodapp.Model;

public class Order {
    private int orderid,foodqty;
    private String foodid,foodstall,foodorder,foodremark,foodstatus;
    private double foodprice;

    public Order(int orderid, String foodid, String foodstall, int foodqty, String foodorder, String foodremark,double foodprice, String foodstatus) {
        this.orderid = orderid;
        this.foodid = foodid;
        this.foodstall = foodstall;
        this.foodqty = foodqty;
        this.foodorder = foodorder;
        this.foodremark = foodremark;
        this.foodprice = foodprice;
        this.foodstatus = foodstatus;
    }

    public int getOrderid() {
        return orderid;
    }

    public String getFoodid() {
        return foodid;
    }

    public String getFoodstall() {
        return foodstall;
    }

    public int getFoodqty() {
        return foodqty;
    }

    public String getFoodorder() {
        return foodorder;
    }

    public double getFoodprice() {
        return foodprice;
    }

    public String getFoodremark() {
        return foodremark;
    }

    public String getFoodstatus() {
        return foodstatus;
    }
}
