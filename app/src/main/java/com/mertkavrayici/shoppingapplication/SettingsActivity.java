package com.mertkavrayici.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mertkavrayici.shoppingapplication.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullnameEditText,userPhoneEditText,addressEditText;
    private TextView profileChangeTextBtn,closeTextBtn,saveButton;

    private Uri imageUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        profileImageView=findViewById(R.id.profile_settings);
        fullnameEditText=findViewById(R.id.fullname_settings);
        userPhoneEditText=findViewById(R.id.phone_settings);
        addressEditText=findViewById(R.id.address_settings);
        profileChangeTextBtn=findViewById(R.id.profile_change);
        closeTextBtn=findViewById(R.id.close_settings);
        saveButton=findViewById(R.id.update_settings);

        userInfoDisplay(profileImageView,fullnameEditText,userPhoneEditText,addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){

                    userInfoSave();

                }
                else{

                    updateUserOnlyInfo();

                }
            }
        });
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateUserOnlyInfo() {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("username",fullnameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phoneOrder",userPhoneEditText.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);



        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profil Bilgileriniz Güncellendi", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&resultCode==RESULT_OK
        && data!=null
        ){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profileImageView.setImageURI(imageUri);

        }
        else{

            Toast.makeText(this,"Hata Oluştu!Tekrar Deneyin",Toast.LENGTH_LONG).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();

        }

    }

    private void userInfoSave() {

        if(TextUtils.isEmpty(fullnameEditText.getText().toString())){

            Toast.makeText(this,"İsim Alanı Boş Bırakılamaz!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this,"Adres Alanı Boş Bırakılamaz!",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this,"Telefon Alanı Boş Bırakılamaz!",Toast.LENGTH_LONG).show();

        }
        else if(checker.equals("clicked")){

            uploadImage();
        }


    }

    private void uploadImage() {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Profile Güncelleniyor");
        progressDialog.setMessage("Profiliniz Güncellenirken Lütfen Bekleyiniz!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null){

            final StorageReference fileRef=storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone() +".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){

                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl=task.getResult();
                        myUrl=downloadUrl.toString();
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("username",fullnameEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
                        userMap.put("image",myUrl);

                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profil Bilgileriniz Güncellendi", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Hata Oluştu! Tekrar Deneyiniz", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(SettingsActivity.this, "Resim Seçilmedi! Tekrar Deneyiniz", Toast.LENGTH_SHORT).show();

        }


    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullnameEditText, EditText userPhoneEditText, EditText addressEditText) {


        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    if(snapshot.child("image").exists()){

                        String image=snapshot.child("image").getValue().toString();
                        String username=snapshot.child("username").getValue().toString();
                        String address=snapshot.child("address").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEditText.setText(username);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}