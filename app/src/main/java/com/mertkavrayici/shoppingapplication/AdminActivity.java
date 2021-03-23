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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {

    private String categoryName, description, price, name, saveCurrentDate, saveCurrentTime;
    private Button addProduct;
    private EditText productName, productDescription, productPrice;
    private ImageView addImage;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productsRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        categoryName = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        addImage = findViewById(R.id.select_image);
        
        addProduct = findViewById(R.id.add_product);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);

        loadingBar = new ProgressDialog(this);



        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });


    }

    private void ValidateProductData() {

        description = productDescription.getText().toString();
        price = productPrice.getText().toString();
        name = productName.getText().toString();



        if (imageUri == null) {

            Toast.makeText(this, "Lütfen Bir Görsel Seçiniz!", Toast.LENGTH_LONG);

        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Lütfen Ürün Açıklama Alanını Doldurunuz!", Toast.LENGTH_LONG);

        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Lütfen Ürün Fiyat Alanını Doldurunuz!", Toast.LENGTH_LONG);

        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Lütfen Ürün İsim Alanını Doldurunuz!", Toast.LENGTH_LONG);

        } else {

            StoreProductInformation();

        }


    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Ürün Eklemesi");
        loadingBar.setMessage("Ürün Eklenirken Lütfen Bekleyiniz...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminActivity.this, "Error " + message, Toast.LENGTH_LONG).show();
                loadingBar.dismiss();

            }


        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminActivity.this, "Resim Başarıyla Yüklendi", Toast.LENGTH_LONG).show();


                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {

                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();


                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl=task.getResult().toString();

                            Toast.makeText(AdminActivity.this, "Ürün Görsel Linkl Oluşturuldu!", Toast.LENGTH_LONG).show();


                            SaveProductInfoToDatebase();
                        }
                    }
                });
            }
        });


    }

    private void SaveProductInfoToDatebase() {


        HashMap<String, Object> productMap = new HashMap<>();
        String fav="1";
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("name", name);
        productMap.put("fav",fav);

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            loadingBar.dismiss();

                            Toast.makeText(AdminActivity.this,"Ürün Başarıyla Eklendi",Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(AdminActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else{
                            loadingBar.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(AdminActivity.this,"Error :"+message,Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            addImage.setImageURI(imageUri);


        }

    }
}