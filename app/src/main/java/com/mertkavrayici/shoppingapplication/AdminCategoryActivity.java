package com.mertkavrayici.shoppingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView pc,phone,tv,white,smart,camera;
    private Button logoutBtn,checkOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        pc=findViewById(R.id.pc);
        phone=findViewById(R.id.phone);
        tv=findViewById(R.id.tv);

        camera=findViewById(R.id.camera);



        logoutBtn=findViewById(R.id.admin_logout_btn);
        checkOrderBtn=findViewById(R.id.checkorderButtons);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminNewOrderActivity.class);
                startActivity(intent);

            }
        });



        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminActivity.class);
                intent.putExtra("category","pc");
                startActivity(intent);

            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminActivity.class);
                intent.putExtra("category","phone");
                startActivity(intent);

            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminActivity.class);
                intent.putExtra("category","tv");
                startActivity(intent);

            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this,AdminActivity.class);
                intent.putExtra("category","camera");
                startActivity(intent);

            }
        });
    }
}