package com.mertkavrayici.shoppingapplication.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mertkavrayici.shoppingapplication.Interface.ItemClickListener;
import com.mertkavrayici.shoppingapplication.Model.Product;
import com.mertkavrayici.shoppingapplication.Prevalent.Prevalent;
import com.mertkavrayici.shoppingapplication.R;

import java.util.HashMap;

//Burada ürünleri sıraladığımız recycle ın adapteri yazıldı.Bu şablon önemli bir şablon.
public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtproductName,txtproductDescription,txtproductPrice;
    public ImageView imageView;
    public ItemClickListener listener;
    public Button fav_btn,fav2_btn;
    DatabaseReference favRes;
    FirebaseDatabase database=FirebaseDatabase.getInstance();

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);


        imageView=itemView.findViewById(R.id.product_image);
        txtproductName=itemView.findViewById(R.id.product_name);
        txtproductDescription=itemView.findViewById(R.id.product_description);
        txtproductPrice=itemView.findViewById(R.id.product_price);
        fav_btn=itemView.findViewById(R.id.fav_btn);
        fav2_btn=itemView.findViewById(R.id.fav2_btn);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");







        fav2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fav_btn.setVisibility(View.VISIBLE);
                fav2_btn.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void setItemClickListener(ItemClickListener listener){

        this.listener=listener;


    }

    @Override
    public void onClick(View v) {

        listener.onClick(v,getAdapterPosition(),false);
    }

    public void fvrtChecker(String postKey) {
    }
}
