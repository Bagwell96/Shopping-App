package com.mertkavrayici.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mertkavrayici.shoppingapplication.Model.User;
import com.mertkavrayici.shoppingapplication.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinButton,loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton=findViewById(R.id.join_button);   // butonları tanımladık.
        loginButton=findViewById(R.id.login_button);
        Paper.init(this);
        loadingBar=new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,LoginActivity.class); //Giriş butonu
                startActivity(intent);

            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);  //Üye olma butonu
                startActivity(intent);

            }
        });

        String userPhoneKey=Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey=Paper.book().read(Prevalent.userPasswordkKey);
        if(userPhoneKey!="" && userPasswordKey!=""){

            if(!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){


                AllowAccess(userPhoneKey,userPasswordKey);                                     //Önceden giriş yapıldıysa direkt giriş yapıyor


                loadingBar.setTitle("Oturum Açılıyor");
                loadingBar.setMessage("Oturum açılırken lütfen bekleyiniz...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }

    private void AllowAccess(final String userPhoneKey,  final String userPasswordKey) {

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("Users").child(userPhoneKey).exists()){


                    User userData=snapshot.child("Users").child(userPhoneKey).getValue(User.class);

                    if(userData.getPhone().equals(userPhoneKey)){


                        if(userData.getPassword().equals(userPasswordKey)){

                            Toast.makeText(MainActivity.this,"Giriş Başarılı...",Toast.LENGTH_LONG);
                            loadingBar.dismiss();

                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUser=userData;
                            startActivity(intent);



                        }
                        else {

                            Toast.makeText(MainActivity.this,"Hatalı Giriş Yaptınız",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }



                }
                else{

                    Toast.makeText(MainActivity.this,"Maalesef Oturum Açılamadı!Lütfen Tekrar Deneyin",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}