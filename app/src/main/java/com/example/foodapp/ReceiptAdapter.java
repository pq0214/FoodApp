package com.example.foodapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.Model.Food;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.MyViewHolder>  {
    private final Context context;
    private final ArrayList<Order> list;

    public ReceiptAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReceiptAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.receiptcard, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptAdapter.MyViewHolder holder, int position) {
        Order order = list.get(position);
        String foodid=order.getFoodid();
        String foodstall=order.getFoodstall();

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("Stall").child(foodstall).child("Food").child(foodid);
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String foodname = dataSnapshot.child("foodname").getValue(String.class);
                String foodimage = dataSnapshot.child("foodimage").getValue(String.class);
                holder.foodname.setText(foodname);
                int imageResource = getImageResourceByName(foodimage);
                holder.foodimg.setImageResource(imageResource);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DecimalFormat df = new DecimalFormat("0.00");
        String printPrice="RM "+ df.format(order.getFoodprice());
        holder.foodprice.setText(printPrice);
        String printQty="Quantity: "+order.getFoodqty();
        holder.foodqty.setText(printQty);
        holder.foodorder.setText(order.getFoodorder());
        String foodstatus = order.getFoodstatus();
        if(foodstatus.equals("prepare") ){
            holder.foodstatus.setText("Preparing...");
            holder.foodstatus.setTextColor(Color.RED);
        }else{
            holder.foodstatus.setText("Done");
            holder.foodstatus.setTextColor(Color.GREEN);
        }
    }

    private int getImageResourceByName(String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodimg;
        private TextView foodprice,foodname, foodqty, foodorder, foodstatus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodimg = itemView.findViewById(R.id.orderfoodimg);
            foodprice = itemView.findViewById(R.id.txtordertotal);
            foodname = itemView.findViewById(R.id.txtordername);
            foodqty = itemView.findViewById(R.id.txtorderqty);
            foodorder = itemView.findViewById(R.id.txtordermode);
            foodstatus = itemView.findViewById(R.id.txtorderstatus);
        }
    }
}
