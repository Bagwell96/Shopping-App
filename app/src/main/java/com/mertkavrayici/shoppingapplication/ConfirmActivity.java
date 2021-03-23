package com.mertkavrayici.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mertkavrayici.shoppingapplication.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        totalAmount=getIntent().getStringExtra("Total Price");


        confirmOrderBtn=findViewById(R.id.confirm_final_order);
        nameEditText=findViewById(R.id.shipment_name);
        phoneEditText=findViewById(R.id.shipment_phone);
        addressEditText=findViewById(R.id.shipment_address);
        cityEditText=findViewById(R.id.shipment_city);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Check();
            }
        });

    }

    private void Check() {

        if(TextUtils.isEmpty(nameEditText.getText().toString())){

            Toast.makeText(this,"Lütfen Ad Soyad Bilgisini Giriniz",Toast.LENGTH_SHORT).show();
        }
         else if(TextUtils.isEmpty(phoneEditText.getText().toString())){

            Toast.makeText(this,"Lütfen Telefon Bilgisini Giriniz",Toast.LENGTH_SHORT).show();
        }
         else if(TextUtils.isEmpty(addressEditText.getText().toString())){

            Toast.makeText(this,"Lütfen Adres Bilgisini Giriniz",Toast.LENGTH_SHORT).show();
        }
         else if(TextUtils.isEmpty(cityEditText.getText().toString())){

            Toast.makeText(this,"Lütfen Şehir Bilgisini Giriniz",Toast.LENGTH_SHORT).show();
        }
         else{
             ConfirmOrder();

        }
    }

    private void ConfirmOrder() {

        Calendar calForDate=new Calendar() {
            @Override
            protected void computeTime() {

            }

            @Override
            protected void computeFields() {

            }

            @Override
            public void add(int field, int amount) {

            }

            @Override
            public void roll(int field, boolean up) {

            }

            @Override
            public int getMinimum(int field) {
                return 0;
            }

            @Override
            public int getMaximum(int field) {
                return 0;
            }

            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }

            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }
        }.getInstance();

        final String saveCurrentDate,saveCurrentTime;

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");

        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");

        saveCurrentTime=currentDate.format(calForDate.getTime());

        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> ordersMap=new HashMap<>();

        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful()){

                    FirebaseDatabase.getInstance().getReference().child("Cart List").
                            child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmActivity.this,"Siparişiniz Onaylandı",Toast.LENGTH_LONG).show();

                                        Intent intent =new Intent(ConfirmActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                }
            }
        });


    }
}