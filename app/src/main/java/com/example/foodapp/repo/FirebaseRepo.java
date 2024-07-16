package com.example.foodapp.repo;

import androidx.annotation.NonNull;

import com.example.foodapp.Model.Stall;
import com.example.foodapp.Model.StallFood;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepo {
    private DatabaseReference dbRef;
    private OnRealtimeDbTaskComplete onRealtimeDbTaskComplete;
    public FirebaseRepo(OnRealtimeDbTaskComplete onRealtimeDbTaskComplete){
        dbRef= FirebaseDatabase.getInstance().getReference().child("stall");


    }
    public void getAllData(){
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Stall> stall =new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Stall stallname=new Stall();
                    stallname.setStallName(ds.child("parentName").getValue(String.class));
                    stallname.setStallImage(ds.child("parentImage").getValue(String.class));

//                    GenericTypeIndicator<ArrayList<StallFood>>();

                }
                onRealtimeDbTaskComplete.onSuccess(stall);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onRealtimeDbTaskComplete.onFailure(error);
            }
        });
    }
    public interface OnRealtimeDbTaskComplete{
        void onSuccess(List<Stall> stallname);
        void onFailure(DatabaseError error);
    }
}
