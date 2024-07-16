package com.example.foodapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodapp.Model.Food;
import com.example.foodapp.Model.Global;
import com.example.foodapp.Model.Stall;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainFood extends AppCompatActivity {
    private RecyclerView foodView;
    private ArrayList<Food> list;
    private String[] category;
    private String stallName;
    private FoodAdapter foodAdapter;
    private LinearLayout btnGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_food);

        ImageButton home = findViewById(R.id.btnHome);
        ImageButton cart = findViewById(R.id.btnCart);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainFood.this, MainStall.class);
                startActivity(intent);
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainFood.this, MainCart.class);
                startActivity(intent);
            }
        });
        Button btnAll = findViewById(R.id.btnCategory);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFoodDataFromDatabase();
            }
        });

        TextView txttable = findViewById(R.id.txttable);
        txttable.setText(Global.getTableno());

        Intent intent = getIntent();
        stallName = intent.getStringExtra("stallName");
        TextView txtStallName = findViewById(R.id.txtStall);
        txtStallName.setText(stallName);

        foodView = findViewById(R.id.foodview);
        list = new ArrayList<>();
        foodView.setLayoutManager((new LinearLayoutManager(this)));
        category = new String[]{"Default Category"};
        foodAdapter = new FoodAdapter(MainFood.this, list, category);
        foodView.setAdapter(foodAdapter);
        readFoodDataFromDatabase();

        btnGroup = findViewById(R.id.btnGroup);

    }

    private void readFoodDataFromDatabase() {
        DatabaseReference stallRef = FirebaseDatabase.getInstance().getReference().child("Stall").child(stallName);
        DatabaseReference foodRef= FirebaseDatabase.getInstance().getReference().child("Stall").child(stallName).child("Food");
        stallRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int length = dataSnapshot.child("LastCat").getValue(int.class);
                category = new String[length];

                for (DataSnapshot categorySnapshot : dataSnapshot.child("Category").getChildren()) {
                    int key = Integer.parseInt(categorySnapshot.getKey()); // Get the key as an integer
                    category[key - 1] = categorySnapshot.getValue(String.class);
                }
                btnGroup.removeViews(1, btnGroup.getChildCount() - 1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(7,8,7,8);
                for (int i = 0; i < category.length; i++) {
                    final String curCategory = category[i];
                    Button newButton = new Button(MainFood.this);
                    newButton.setLayoutParams(layoutParams);
                    newButton.setText(curCategory);
                    newButton.setBackgroundResource(R.drawable.btncategory);
                    newButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<Food> filteredFoods = new ArrayList<>();
                            for (Food food : list) {
                                if (food.getFoodcategory().equals(curCategory)) {
                                    filteredFoods.add(food);
                                }
                            }
                            foodAdapter.updateData(filteredFoods,curCategory);
                        }
                    });
                    btnGroup.addView(newButton);
                }
                foodAdapter = new FoodAdapter(MainFood.this, list, category);
                foodView.setAdapter(foodAdapter);
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Log.e(TAG, "Failed to read food data", databaseError.toException());
            }

        });
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear(); // Clear existing list
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    String foodID = foodSnapshot.getKey();
                    String foodCategory = foodSnapshot.child("foodcategory").getValue(String.class);
                    String foodName = foodSnapshot.child("foodname").getValue(String.class);
                    String foodImage = foodSnapshot.child("foodimage").getValue(String.class);
                    String foodDesc = foodSnapshot.child("fooddesc").getValue(String.class);
                    double foodPrice = foodSnapshot.child("foodprice").getValue(Double.class);
                    list.add(new Food(foodID, stallName, foodCategory, foodName, foodImage, foodDesc, foodPrice));
                }
                // Notify the adapter of data change
                foodAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Log.e(TAG, "Failed to read food data", databaseError.toException());
            }
        });
    }
}

