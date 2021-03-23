package com.mertkavrayici.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mertkavrayici.shoppingapplication.Model.User;
import com.mertkavrayici.shoppingapplication.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {


    private EditText inputNumber, inputPassword;
    private Button loginButton;
    private String parentDbName = "Users";

    private ProgressDialog loadingBar;

    private CheckBox checkBoxRemember;

    private TextView adminLink, notAdminLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        inputNumber = findViewById(R.id.login_phone_number);  //Butonlar ve edittextler eklendi
        inputPassword = findViewById(R.id.login_password);
        loadingBar = new ProgressDialog(this);


        checkBoxRemember = findViewById(R.id.remember_me);
        adminLink=findViewById(R.id.admin_panel_link);
        notAdminLink=findViewById(R.id.not_admin_panel_link);


        Paper.init(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();      //Giriş


            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.setText("Yönetici Girişi");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);         //Admin Girişi
                parentDbName="Admins";

            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.setText("Giriş Yap");
                adminLink.setVisibility(View.VISIBLE);              //Normal Giriş
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });
    }

    private void loginUser() {

        //Burada giriş metou yazıldı. Eğer boşluklar doldurulmadıysa hata mesajı veriyor.Eğer doluysa allow access fonksiyonu çalışıyor.

        String phone = inputNumber.getText().toString();
        String password = inputPassword.getText().toString();


        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Lütfen Tüm Alanları Doldurunuz..", Toast.LENGTH_LONG).show();

        } else {

            loadingBar.setTitle("Oturum Açılıyor");
            loadingBar.setMessage("Oturum açılırken lütfen bekleyiniz...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone, password);

        }
    }

    private void allowAccessToAccount(final String phone, final String password) {

        //Öncelikle checkbox kontrol ediliyor.Eğer işaretli ise kullanıcı no ve şifre papera yazılıyor.(Yukarıda belirtildi)
        //Daha sonra firebaseden bir referans oluşturuluyor.
        //Telefon genel başlık öncelikle o kontrol ediliyor.Daha sonra phone ve şifre değerleri veritabanı ile aynı ise giriş yapılıyor.En son
        //Parentdb name kontrol edilerek admin veya kullanıcı girişi sağlanıyo

        if (checkBoxRemember.isChecked()) {

            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordkKey, password);

        }


        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDbName).child(phone).exists()) {


                    User userData = snapshot.child(parentDbName).child(phone).getValue(User.class);

                    if (userData.getPhone().equals(phone)) {


                        if (userData.getPassword().equals(password)) {

                          if(parentDbName.equals("Admins")){

                              Toast.makeText(LoginActivity.this, "Giriş Başarılı...", Toast.LENGTH_LONG);
                              loadingBar.dismiss();

                              Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                              startActivity(intent);
                              finish();

                          }
                          else if(parentDbName.equals("Users")){

                              Toast.makeText(LoginActivity.this, "Giriş Başarılı...", Toast.LENGTH_LONG);
                              loadingBar.dismiss();

                              Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                              Prevalent.currentOnlineUser=userData;
                              startActivity(intent);
                              finish();




                          }


                        } else {

                            Toast.makeText(LoginActivity.this, "Hatalı Giriş Yaptınız", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }


                } else {

                    Toast.makeText(LoginActivity.this, "Maalesef Oturum Açılamadı!Lütfen Tekrar Deneyin", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}