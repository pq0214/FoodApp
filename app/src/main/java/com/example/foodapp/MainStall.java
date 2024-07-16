package com.example.foodapp;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.Model.Global;
import com.example.foodapp.Model.Stall;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainStall extends AppCompatActivity {
    private RecyclerView stallView;
    private TextView txttable;
    private ArrayList<Stall> list;
    private StallAdapter stalladapter;
    private ImageButton logout, cart;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stall);

        logout = findViewById(R.id.btnLogOut);
        cart = findViewById(R.id.btnCart);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainStall.this);
                builder.setTitle("Confirm Action");
                builder.setMessage("Are you sure you want to quit? \n (Take Note: All records will be discarded.)");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String receipt= String.valueOf(Global.getReceipt());
                        DatabaseReference receiptRef = FirebaseDatabase.getInstance().getReference().child("Receipt").child(receipt);

                        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Boolean delete = dataSnapshot.child("delete").getValue(Boolean.class);
                                if (delete != null && delete) {
                                    receiptRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(MainStall.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to delete receipt
                                            Log.e(TAG, "Failed to delete receipt", e);
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle potential errors
                                Log.e(TAG, "Failed to read delete flag", databaseError.toException());
                            }
                        });
                        Intent intent = new Intent(MainStall.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainStall.this, MainCart.class);
                startActivity(intent);
            }
        });

        txttable = findViewById(R.id.txttable);
        txttable.setText(Global.getTableno());

        stallView = findViewById(R.id.stallview);
        list = new ArrayList<>();
        stalladapter = new StallAdapter(this, list);
        stallView.setAdapter(stalladapter);
        // Read data from text file and populate list
        readStallDataFromDatabase();

        // Set up RecyclerView layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        stallView.setLayoutManager(layoutManager);
    }


    private void readStallDataFromDatabase() {
        DatabaseReference stallsRef = FirebaseDatabase.getInstance().getReference().child("Stall");

        stallsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot stallSnapshot : dataSnapshot.getChildren()) {
                    String stallName = stallSnapshot.getKey();
                    String stallImage = stallSnapshot.child("Image").getValue(String.class);

                    // Create a new Stall object and add it to the list
                    list.add(new Stall(stallName, stallImage));
                }

                // Notify the adapter of data change
                stalladapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Log.e(TAG, "Failed to read stall data", databaseError.toException());
            }
        }); // Notify adapter of data change
    }
}
