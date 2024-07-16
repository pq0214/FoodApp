package com.example.foodapp.Model;

import com.example.foodapp.FoodAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Global {
    private static Food clickedFood;
    private static String tableno;
    private static int receipt;
    private static int order;
    private static Order editOrder;
    private static Food editFood;

    public static void setEditOrder(Order order,Food food) {
        editOrder = order;
        editFood = food;
    }

    public static Order geteditOrder(){return editOrder;}

    public static Food getEditFood() {
        return editFood;
    }

    public static Food getClickedFood() {
        return clickedFood;
    }

    public static void setClickedFood(Food food) {
        clickedFood = food;
    }

    public static String getTableno() {
        return tableno;
    }

    public static void setTableno(String value) {
        tableno = value;
    }

    public static int getReceipt() {
        return receipt;
    }

    public static void setReceipt(int value) {
        receipt = value;
    }

    public static int getOrder() {
        return order;
    }

    public static void setOrder(int value) {
        order = value;
    }
}
