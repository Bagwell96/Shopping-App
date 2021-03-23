package com.mertkavrayici.shoppingapplication.ViewHolder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mertkavrayici.shoppingapplication.HomeActivity;
import com.mertkavrayici.shoppingapplication.Model.CategoryModel;
import com.mertkavrayici.shoppingapplication.PhoneListActivity;
import com.mertkavrayici.shoppingapplication.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements View.OnClickListener{

    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList){

        this.categoryModelList=categoryModelList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {

        String icon=categoryModelList.get(position).getCategoryIconLink();
        String name=categoryModelList.get(position).getCategoryName();
        holder.setCategoryName(name);
        holder.setCategoryIcon(icon);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryIcon;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName=itemView.findViewById(R.id.category_name);
            categoryIcon=itemView.findViewById(R.id.category_image);



        }

        private void setCategoryIcon(String iconUri){

if(!iconUri.equals("null")) {
    Glide.with(itemView.getContext()).load(iconUri).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_tv_24)).into(categoryIcon);

}
        }
        private void setCategoryName(String name){

            categoryName.setText(name);
        }

    }
}
