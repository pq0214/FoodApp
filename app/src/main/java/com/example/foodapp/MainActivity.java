package com.example.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp.Model.Global;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    public String username;
    EditText name;
    Spinner table;
    Button btnOrder,btnLogin;
    public String selectedtable;
    private int receipt,order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setReceipt();

        name=findViewById(R.id.edtname);
        table=findViewById(R.id.dropdown_table);
        btnOrder=findViewById(R.id.btnOrder);
        btnLogin=findViewById(R.id.btnLogin);


        Spinner tablelist = findViewById(R.id.dropdown_table);
        String[] table = {"Select a table","Table 1", "Table 2", "Table 3", "Table 4", "Table 5"}; //dropdown list

        // Create an ArrayAdapter using the array of items and a default layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, table);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tablelist.setAdapter(adapter);

        tablelist.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tablelist.getViewTreeObserver().removeOnGlobalLayoutListener(this); // Remove the listener to prevent multiple calls

                //set display position of dropdown list after the dropdown menu position
                int spinnerHeight = tablelist.getHeight();
                int offset = spinnerHeight + getResources().getDimensionPixelSize(R.dimen.vertical_offset);
                tablelist.setDropDownVerticalOffset(offset);
            }
        });// set dropdown default text
        tablelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    selectedtable = parent.getItemAtPosition(position).toString();
                } else {
                    selectedtable = null; // Reset selected table if hint is selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedtable == null) {
                    View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                    String message = "Please select your table.";
                    int duration = Snackbar.LENGTH_SHORT;
                    showSnackbar(rootView, message, duration);
                } else {
                    username = name.getText().toString();
                    Intent intent = new Intent(MainActivity.this, MainStall.class);
                    Global.setTableno(selectedtable);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    private void setReceipt() {
        DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference().child("Counter");
        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receipt = dataSnapshot.child("Receipt").getValue(int.class);
                Global.setReceipt(receipt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
}
