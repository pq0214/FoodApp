package com.example.foodapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.Model.Global;
import com.example.foodapp.Model.Food;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Food> list;
    private ArrayList<Food>[] sortlist;
    private String[] categories;

    public FoodAdapter(Context context, ArrayList<Food> list, String[] categories) {
        this.context = context;
        this.list = list;
        this.categories = categories;
        this.sortlist = new ArrayList[categories.length];
        for (int i = 0; i < categories.length; i++) {
            sortlist[i] = new ArrayList<>();
        }
        sortFood();
    }

    private ArrayList<Food>[] sortFood() {
        for (int i = 0; i < categories.length; i++) {
            for (Food food : list) {
                if (food.getFoodcategory().equals(categories[i])) {
                    sortlist[i].add(food);
                }
            }
        }
        return sortlist;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) { // Category header
            View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodcard_header, parent, false);
            return new CategoryViewHolder(categoryView);
        } else { // Food item
            View foodView = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodcard, parent, false);
            return new FoodViewHolder(foodView);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) { // Category header
            // Bind category header data
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            categoryViewHolder.txtCategory.setText(categories[position / 2]); // Adjust index for nested loop

        } else { // Food item
            // Bind food item data
            int totalOffset = 0;
            for (int i = 0; i < sortlist.length; i++) {
                if (position < totalOffset + sortlist[i].size() + 1) {
                    int foodPosition = position - totalOffset - 1;
                    FoodViewHolder foodViewHolder = (FoodViewHolder) holder;
                    Food food = sortlist[i].get(foodPosition);
                    foodViewHolder.foodName.setText(food.getFoodname());
                    int imageResource = getImageResourceByName(food.getFoodimage());
                    foodViewHolder.foodImage.setImageResource(imageResource);
                    foodViewHolder.foodDesc.setText(food.getFooddesc());
                    foodViewHolder.foodid.setText(String.valueOf(food.getFoodid()));
                    break;
                }
                totalOffset += sortlist[i].size() + 1; // Add 1 for the category header
            }
        }
    }

    private int getImageResourceByName(String foodimage) {
        return context.getResources().getIdentifier(foodimage, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        if (sortlist == null) {
            return 0;
        }
        int count = 0;
        for (ArrayList<Food> foodList : sortlist) {
            count += foodList.size();
        }
        return count + categories.length; // Add the category headers
    }

    @Override
    public int getItemViewType(int position) {
        // Check if the current position corresponds to a category header
        for (int i = 0, offset = 0; i < categories.length; i++) {
            if (position == offset) {
                return 0; // Category header view type
            }
            offset += sortlist[i].size() + 1; // Add 1 for the category header
        }
        return 1; // Food item view type
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
    public void updateData(ArrayList<Food> filteredFoods,String category) {
        for (ArrayList<Food> categoryList : sortlist) {
            categoryList.clear();
        }
        categories = new String[]{category};
        notifyDataSetChanged();
        // Add filtered foods to the category in sortlist
        sortlist[0].addAll(filteredFoods);
        notifyDataSetChanged();
    }
    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName, foodDesc, foodid;
        private ImageView foodImage;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.imgfood);
            foodName = itemView.findViewById(R.id.txtfoodname);
            foodDesc = itemView.findViewById(R.id.txtfooddesc);
            foodid = itemView.findViewById(R.id.txtfoodid);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Food clickedFood = getClickedFood(position);
                        Global.setClickedFood(clickedFood);
                        Intent intent = new Intent(context, OrderFood.class);
                        context.startActivity(intent);
                    }
                }
            });
        }
        private Food getClickedFood(int position) {
            int totalOffset = 0;
            for (int i = 0; i < sortlist.length; i++) {
                if (position < totalOffset + sortlist[i].size() + 1) {
                    int foodPosition = position - totalOffset - 1;
                    return sortlist[i].get(foodPosition);
                }
                totalOffset += sortlist[i].size() + 1; // Add 1 for the category header
            }
            return null;
        }
    }
}