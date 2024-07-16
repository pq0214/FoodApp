package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.Model.Global;
import com.example.foodapp.Model.Order;
import com.example.foodapp.Model.Receipt;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainCart extends AppCompatActivity {
    private int receiptno;
    private ArrayList<Order> list;
    private Receipt receiptObj;
    private String tableno;
    private CartAdapter cartAdapter;
    private RecyclerView cartView;
    private double totalamount=0;
    private int totalitem=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cart);

        receiptno=Global.getReceipt();
        tableno = Global.getTableno();
        TextView table= findViewById(R.id.txttable);
        table.setText(tableno);

        ImageButton clear = findViewById(R.id.btnClear);

        clear.setOnClickListener(new View.OnClickListener() {
            DatabaseReference deleteRef= FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receiptno)).child("order");
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainCart.this);
                builder.setTitle("Confirm Action");
                builder.setMessage("Do you want to clear cart?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                resetCounter();
                                Toast.makeText(MainCart.this, "Cart cleared successfully! ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to remove order nodes
                            }
                        });
                        Intent intent = new Intent(MainCart.this, MainStall.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageButton close = findViewById(R.id.btnClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton home=findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCart.this, MainStall.class);
                startActivity(intent);
            }
        });

        Button makeOrder=findViewById(R.id.btnOrder);
        makeOrder.setOnClickListener(new View.OnClickListener() {
            DatabaseReference makeOrder= FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receiptno));
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainCart.this).inflate(R.layout.inputcontact, null);
                EditText contactno = dialogView.findViewById(R.id.editcontact);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainCart.this);
                builder.setTitle("Confirm Order");
                builder.setMessage("Enter your contact number to confirm your order.");
                builder.setView(dialogView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String contact = contactno.getText().toString().trim();
                        // Validate the input and process it as needed
                        if (!TextUtils.isEmpty(contact)) {
                            makeOrder.child("delete").setValue(false);
                            makeOrder.child("contact").setValue(contact);
                            makeOrder.child("date").setValue(getCurrentDate());
                            makeOrder.child("time").setValue(getCurrentTime());
                            Toast.makeText(MainCart.this, "Order confirmed!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainCart.this, MainReceipt.class);
                            startActivity(intent);
                        } else {
                            // Input is invalid, show an error message
                            Toast.makeText(MainCart.this, "Invalid contact number. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User canceled, do nothing
                    }
                });
                builder.create().show();
                receiptno+=1;
                addReceipt(receiptno);

            }
        });
        cartView = findViewById(R.id.orderView);
        list = new ArrayList<>();
        cartAdapter = new CartAdapter(MainCart.this, list, receiptObj);
        cartView.setAdapter(cartAdapter);
        readOrderDataFromDatabase();
        cartView.setLayoutManager((new LinearLayoutManager(this)));

    }

    private void readOrderDataFromDatabase() {
        DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receiptno));
        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean delete = Boolean.TRUE.equals(snapshot.child("delete").getValue(Boolean.class));
                boolean payment = Boolean.TRUE.equals(snapshot.child("payment").getValue(Boolean.class));
                receiptObj=new Receipt(receiptno,delete,"","",tableno,payment);

                DataSnapshot orderSnapshot = snapshot.child("order");
                for (DataSnapshot childSnapshot  : orderSnapshot.getChildren()) {
                    int orderid = Integer.parseInt(childSnapshot.getKey());
                    String foodid = childSnapshot.child("foodid").getValue(String.class);
                    String foodstall = childSnapshot.child("foodstall").getValue(String.class);
                    int foodqty = childSnapshot.child("foodqty").getValue(int.class);
                    String foodorder = childSnapshot.child("foodorder").getValue(String.class);
                    String foodremark = childSnapshot.child("foodremark").getValue(String.class);
                    double foodprice = childSnapshot.child("foodprice").getValue(double.class);
                    String foodstatus = childSnapshot.child("foodstatus").getValue(String.class);

                    totalitem += foodqty;
                    totalamount += foodprice;

                    list.add(new Order(orderid, foodid, foodstall, foodqty, foodorder, foodremark, foodprice, foodstatus));
                }
                DecimalFormat df = new DecimalFormat("0.00");

                TextView item = findViewById(R.id.txtItem);
                String printitem=totalitem+" items";
                item.setText(printitem);
                TextView subtotal=findViewById(R.id.txtsubtotal);
                TextView total=findViewById(R.id.txttotal);
                TextView lasttotal=findViewById(R.id.txtLastTotal);
                String printtotal="RM "+df.format(totalamount);
                subtotal.setText(printtotal);
                total.setText(printtotal);
                lasttotal.setText(printtotal);

                cartAdapter = new CartAdapter(MainCart.this, list, receiptObj);
                cartView.setAdapter(cartAdapter);
                cartAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Data retrieval failed: " + error.getMessage());
            }
        });
    }
    private void resetCounter() {
        DatabaseReference resetCounter = FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receiptno)).child("counter");
        resetCounter.setValue(1);
        Global.setOrder(1);
    }

    private void addReceipt(int value) {
        DatabaseReference resetCounter = FirebaseDatabase.getInstance().getReference().child("Counter").child("Receipt");
        resetCounter.setValue(value);
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getCurrentTime() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date time = Calendar.getInstance().getTime();
        return timeFormat.format(time);
    }
}