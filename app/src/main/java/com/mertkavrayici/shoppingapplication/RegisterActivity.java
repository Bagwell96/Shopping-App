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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText inputName,inputPhoneNumber,inputPassword;       //Eklendi
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        createAccountButton=findViewById(R.id.register_button);
        inputName=findViewById(R.id.register_username);
        inputPhoneNumber=findViewById(R.id.register_phone_number);   //Tanımlandı
        inputPassword=findViewById(R.id.register_password);
        loadingBar=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAccount(); //KAyıt ol butonuna tıklayınca createacconut calısıyor.
            }
        });


    }

    private void CreateAccount() {

        // 3 adet yazılan yazı alınıyor.İlk if kontrolü herhangi biri boş ise hata mesajı cıkartıyor.Eğer değilse validatephonenumber fonk.

        String userName=inputName.getText().toString();
        String phone=inputPhoneNumber.getText().toString();
        String password=inputPassword.getText().toString();

        if(TextUtils.isEmpty(userName)||TextUtils.isEmpty(phone)||TextUtils.isEmpty(password)){

            Toast.makeText(this,"Lütfen Tüm Alanları Doldurunuz..",Toast.LENGTH_LONG).show();

        }
        else{

            loadingBar.setTitle("Hesap Oluştur");
            loadingBar.setMessage("Hesabınızı oluşturulurken lütfen bekleyiniz...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            




            ValidatePhoneNumber(userName,phone,password);


        }



    }

    private void ValidatePhoneNumber(final String userName, final String phone, final String password) {
//Tekrar database referansı oluşturuldu.Daha sonra if kontrolü ile users sınıfında o no var mı kontrol edilir.Eğer varsa hata mesajı ile bildirilir
        //eğer yoksa girilen değerler hashmape sırayla kaydedilir ve userdatamape kaydedilerek veritabanına aktarılır.

        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phone).exists()))
                {

                    HashMap<String,Object> userDataMap=new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);
                    userDataMap.put("username",userName);

                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        Toast.makeText(RegisterActivity.this,"Hesabınız Oluşturuldu",Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                        Intent intent =new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Maalesef Hesabınız Oluşturulamadı!Lütfen Tekrar Deneyin",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });


                }
                else{

                    Toast.makeText(RegisterActivity.this,"Bu bilgiler ile zaten hesap oluşturulmuş",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Lütfen bilgilerinizi kontrol edin..",Toast.LENGTH_LONG).show();

                    Intent intent =new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}