package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.Model.Global;
import com.example.foodapp.Model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderFood extends AppCompatActivity{
    private NumberPicker quantity;
    private Button add;
    private TextView orderRemark;
    private int order,receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderfood);

        receipt=Global.getReceipt();
        getOrder();

        Food clickFood = Global.getClickedFood();

        ImageView foodImage = findViewById(R.id.foodimg);
        int imageResource = getResources().getIdentifier(clickFood.getFoodimage(), "drawable", getPackageName());
        foodImage.setImageResource(imageResource);

        TextView foodName = findViewById(R.id.txtName);
        foodName.setText(clickFood.getFoodname());
        TextView foodDesc = findViewById(R.id.txtDesc);
        foodDesc.setText(clickFood.getFooddesc());
        TextView foodPrice = findViewById(R.id.txtPrice);
        double price= clickFood.getFoodprice();
        DecimalFormat df = new DecimalFormat("0.00");
        String priceValue="RM "+ df.format(price);
        foodPrice.setText(priceValue);

        orderRemark=findViewById(R.id.edtRemark);

        quantity = findViewById(R.id.counterPicker);
        quantity.setMinValue(1);
        quantity.setMaxValue(20);
        quantity.setValue(1);
        quantity.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                double totalprice=newVal*price;
                String printValue="Add Order - RM "+ df.format(totalprice);
                // Update the text of the add button with the new value
                add.setText(printValue);
            }
        });

        add=findViewById(R.id.btnAdd);
        add.setText("Add Order - RM "+ df.format(price));
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DatabaseReference addOrder= FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receipt));
                String foodid = Global.getClickedFood().getFoodid();
                String foodstall = Global.getClickedFood().getFoodstall();
                int foodqty = quantity.getValue();
                String foodremark = orderRemark.getText().toString();
                if (TextUtils.isEmpty(foodremark)) {
                    foodremark = "";
                }
                double foodprice = foodqty*Global.getClickedFood().getFoodprice();
                String foodorder = determineFoodOrder();
                boolean payment=false;
                boolean delete=true;
                String foodstatus="prepare";
                String finalFoodremark = foodremark;

                addOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> Order = new HashMap<>();
                        Order.put("foodid", foodid);
                        Order.put("foodorder", foodorder);
                        Order.put("foodprice", foodprice);
                        Order.put("foodqty", foodqty);
                        Order.put("foodremark", finalFoodremark);
                        Order.put("foodstall", foodstall);
                        Order.put("foodstatus",foodstatus);
                        if (dataSnapshot.exists()) {
                            boolean duplicate=false;
                            for(int i=1; i<=order;i++){
                                String existingFoodId = dataSnapshot.child("order").child(String.valueOf(i)).child("foodid").getValue(String.class);
                                String existingFoodOrder = dataSnapshot.child("order").child(String.valueOf(i)).child("foodorder").getValue(String.class);
                                if(existingFoodId != null && existingFoodId.equals(foodid) && existingFoodOrder != null && existingFoodOrder.equals(foodorder)) {
                                    int existingQty = dataSnapshot.child("order").child(String.valueOf(i)).child("foodqty").getValue(int.class);
                                    addOrder.child("order").child(String.valueOf(i)).child("foodqty").setValue(existingQty+foodqty);
                                    duplicate=true;
                                    break;
                                }
                            }
                            if(!duplicate) {
                                addOrder.child("order").child(String.valueOf(order)).setValue(Order);
                                addOrder.child("counter").setValue(order);
                                order += 1;
                                addCounter(order);
                            }
                        } else {
                            DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receipt));
                            Map<String, Object> Receipt = new HashMap<>();
                            Receipt.put("payment", payment);
                            Receipt.put("delete",delete);
                            Receipt.put("table",Global.getTableno());
                            Receipt.put("counter",2);
                            receiptRef.setValue(Receipt);
                            receiptRef.child("order").child(String.valueOf(order)).setValue(Order);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(OrderFood.this, "Food added to cart successfully! ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        Button back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addCounter(int value) {
        DatabaseReference addCounter = FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receipt)).child("counter");
        addCounter.setValue(value);
    }
    private String determineFoodOrder() {
        RadioButton radiobtnTakeAway = findViewById(R.id.radiobtnTakeAway);
        RadioButton radiobtnDineIn = findViewById(R.id.radiobtnDineIn);
        if (radiobtnTakeAway.isChecked()) {
            return "Take Away";
        } else if (radiobtnDineIn.isChecked()) {
            return "Dine In";
        } else {
            return "";
        }
    }
    private void getOrder(){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receipt));
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    order = dataSnapshot.child("counter").getValue(Integer.class);
                } else {
                    order = 1; // Initialize with default value if dataSnapshot doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
}

