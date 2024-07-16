package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainReceipt extends AppCompatActivity {
    private int receiptno;
    private String tableno;
    private ArrayList<Order> list;
    private ReceiptAdapter receiptAdapter;
    private RecyclerView receiptview;
    private double totalamount=0;
    private int totalitem=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_receipt);

        receiptno = Global.getReceipt();
        TextView receiptid = findViewById(R.id.txtReceiptID);
        receiptid.setText("#"+receiptno);
        tableno = Global.getTableno();
        TextView table= findViewById(R.id.txttable);
        table.setText(tableno);

        receiptview = findViewById(R.id.receiptView);
        list = new ArrayList<>();
        receiptAdapter = new ReceiptAdapter(MainReceipt.this, list);
        receiptview.setAdapter(receiptAdapter);
        readDataFromDatabase();
        receiptview.setLayoutManager((new LinearLayoutManager(this)));

        ImageButton close=findViewById(R.id.btnClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainReceipt.this);
                builder.setTitle("Confirm Action");
                builder.setMessage("Ready to start your new order? \n Please note that if you proceed, your current order will no longer be accessible for viewing.");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setReceipt();
                        Intent intent = new Intent(MainReceipt.this, MainStall.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void readDataFromDatabase() {
        DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receiptno));
        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean delete = Boolean.TRUE.equals(snapshot.child("delete").getValue(Boolean.class));
                boolean payment = Boolean.TRUE.equals(snapshot.child("payment").getValue(Boolean.class));
                String date =snapshot.child("date").getValue(String.class);
                String time = snapshot.child("time").getValue(String.class);

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
                TextView datetime = findViewById(R.id.txtDateTime);
                datetime.setText(date+" "+time);
                TextView item = findViewById(R.id.txtItem);
                String printitem=totalitem+" items";
                item.setText(printitem);
                TextView total=findViewById(R.id.txttotal);
                String printtotal="RM "+df.format(totalamount);
                total.setText(printtotal);
                TextView txtpayment = findViewById(R.id.txtPayment);
                if(payment){
                    txtpayment.setText("Paid");
                    txtpayment.setTextColor(Color.GREEN);
                }else{
                    txtpayment.setText("Pay At Counter");
                    txtpayment.setTextColor(Color.RED);
                }
                receiptAdapter = new ReceiptAdapter(MainReceipt.this, list);
                receiptview.setAdapter(receiptAdapter);
                receiptAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Data retrieval failed: " + error.getMessage());
            }
        });
    }

    private void setReceipt() {
        DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference().child("Counter");
        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int receipt = dataSnapshot.child("Receipt").getValue(int.class);
                Global.setReceipt(receipt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
}