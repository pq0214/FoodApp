package com.example.foodapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>  {
    private final Context context;
    private final ArrayList<Order> list;
    private final Receipt receiptObj;
    private int orderID;

    public CartAdapter(Context context, ArrayList<Order> list, Receipt receiptObj) {
        this.context = context;
        this.list = list;
        this.receiptObj = receiptObj;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ordercard, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {
        Order order = list.get(position);
        String foodid=order.getFoodid();
        String foodstall=order.getFoodstall();
        orderID=order.getOrderid();

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
        int receipt=receiptObj.getReceiptid();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            final DatabaseReference deleteRef= FirebaseDatabase.getInstance().getReference().child("Receipt").child(String.valueOf(receipt)).child("order").child(String.valueOf(orderID));
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Action");
                builder.setMessage("Do you want to remove this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Item cleared successfully! ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainCart.class);
                                context.startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("Stall").child(foodstall).child("Food").child(foodid);

                foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String foodname = dataSnapshot.child("foodname").getValue(String.class);
                        String foodimage = dataSnapshot.child("foodimage").getValue(String.class);
                        String fooddesc = dataSnapshot.child("fooddesc").getValue(String.class);
                        double foodprice = dataSnapshot.child("foodprice").getValue(double.class);
                        String foodcategory = dataSnapshot.child("foodcategory").getValue(String.class);

                        Food foodObj = new Food(foodid, foodstall,foodcategory,foodname, foodimage, fooddesc, foodprice);

                        Global.setEditOrder(order, foodObj);
                        Global.setReceipt(receipt);

                        // Start the UpdateFood activity
                        Intent intent = new Intent(context, UpdateFood.class);
                        context.startActivity(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

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
        private TextView foodprice,foodname, foodqty, foodorder;
        private ImageButton delete,edit;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodimg = itemView.findViewById(R.id.orderfoodimg);
            foodprice = itemView.findViewById(R.id.txtordertotal);
            foodname = itemView.findViewById(R.id.txtordername);
            foodqty = itemView.findViewById(R.id.txtorderqty);
            foodorder = itemView.findViewById(R.id.txtordermode);
            delete = itemView.findViewById(R.id.btndelete);
            edit = itemView.findViewById(R.id.btnedit);
        }
    }
}
