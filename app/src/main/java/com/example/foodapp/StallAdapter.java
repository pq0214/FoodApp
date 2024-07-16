//package com.example.foodapp;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.foodapp.Model.Stall;
//import com.example.foodapp.Model.StallFood;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StallAdapter extends RecyclerView.Adapter<StallAdapter.MyViewHolder> {
//    private Context context;
//
//    private ArrayList<Stall> list;
//
//    public StallAdapter(Context context, ArrayList<Stall> list){
//        this.context=context;
//        this.list=list;
//    }
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v= LayoutInflater.from(context).inflate(R.layout.stallcard,parent,false);
//        return new MyViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        Stall stallFood= list.get(position);
//        holder.stallName.setText(stallFood.getStallName());
//        Glide.with(holder.itemView.getContext()).load(stallFood.getStallImage()).into(holder.stallImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder{
//        private ImageView stallImage;
//        private TextView stallName;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            stallImage=itemView.findViewById(R.id.imgstall);
//            stallName=itemView.findViewById(R.id.txtstall);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(),"Clicked on:"+stallName,Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
package com.example.foodapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp.Model.Stall;
import java.util.ArrayList;

public class StallAdapter extends RecyclerView.Adapter<StallAdapter.MyViewHolder> {
    private static Context context;
    private static ArrayList<Stall> list;

    public StallAdapter(Context context, ArrayList<Stall> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.stallcard, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stall stall = list.get(position);
        holder.stallName.setText(stall.getStallName());

        // Load image using Glide
        int imageResource = getImageResourceByName(stall.getStallImage());
        holder.stallImage.setImageResource(imageResource);
    }

    // Method to get image resource ID by name
    private int getImageResourceByName(String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView stallImage;
        private TextView stallName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stallImage = itemView.findViewById(R.id.imgstall);
            stallName = itemView.findViewById(R.id.txtstall);

            // Set click listener directly in the constructor
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Stall clickedStall = list.get(position);
                        Intent intent = new Intent(context, MainFood.class);
                        intent.putExtra("stallName", clickedStall.getStallName());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}

