package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.Model.Food;
import com.example.foodapp.Model.Global;
import com.example.foodapp.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class UpdateFood extends AppCompatActivity {
    private NumberPicker foodquantity;
    private Button update;
    private int receiptID;
    private Order order;
    private Food food;
    private TextView orderRemark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatefood);

        receiptID = Global.getReceipt();
        order=Global.geteditOrder();
        food=Global.getEditFood();

        ImageView foodImage = findViewById(R.id.foodimg);
        int imageResource = getResources().getIdentifier(food.getFoodimage(), "drawable", getPackageName());
        foodImage.setImageResource(imageResource);

        TextView foodName = findViewById(R.id.txtName);
        foodName.setText(food.getFoodname());

        TextView foodDesc = findViewById(R.id.txtDesc);
        foodDesc.setText(food.getFooddesc());

        TextView foodPrice = findViewById(R.id.txtPrice);
        double price= food.getFoodprice();

        DecimalFormat df = new DecimalFormat("0.00");
        String priceValue="RM "+ df.format(price);
        foodPrice.setText(priceValue);

        orderRemark=findViewById(R.id.edtRemark);
        orderRemark.setText(order.getFoodremark());

        foodquantity = findViewById(R.id.counterPicker);
        foodquantity.setMinValue(1);
        foodquantity.setMaxValue(20);
        foodquantity.setValue(order.getFoodqty());

        foodquantity.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                double totalprice=newVal*price;
                String printValue="Update Order - RM "+ df.format(totalprice);
                update.setText(printValue);
            }
        });

        Button back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update=findViewById(R.id.btnUpdate);
        update.setText("Update Order - RM "+ df.format(price));
        update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DatabaseReference updateOrder= FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receiptID)).child("order").child(String.valueOf(order.getOrderid()));
                int foodqty = foodquantity.getValue();
                String foodremark = orderRemark.getText().toString();
                if (TextUtils.isEmpty(foodremark)) {
                    foodremark = "";
                }
                double foodprice = foodqty*food.getFoodprice();
                String foodorder=determineFoodOrder();
                updateOrder.child("foodremark").setValue(foodremark);
                updateOrder.child("foodprice").setValue(foodprice);
                updateOrder.child("foodorder").setValue(foodorder);
                updateOrder.child("foodqty").setValue(foodqty);
                Toast.makeText(UpdateFood.this, "Food updated to cart successfully! ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateFood.this, MainCart.class);
                startActivity(intent);
            }
        });
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
}