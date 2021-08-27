package com.example.vecadminapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UploadImage extends AppCompatActivity {

    private Spinner Image_Category;
    private CardView selectImage;
    private Button uploadImage;
    private ImageView galleryImageview;

    private String category;

    private final int REQ = 1;
    private Bitmap bitmap;

    private DatabaseReference reference,dummyreference;
    private StorageReference storageReference;
    String downloadUrl;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        selectImage = findViewById(R.id.addGalleryImage);
        Image_Category = findViewById(R.id.Image_category);
        uploadImage = findViewById(R.id.uploadimagebtn);
        galleryImageview = findViewById(R.id.galleryImageview);

        reference = FirebaseDatabase.getInstance().getReference().child("gallery");
        dummyreference= reference;
        storageReference = FirebaseStorage.getInstance().getReference().child("gallery");


        pd = new ProgressDialog(this);

        String[] items = new String[]{"Select Category","Placement Acheivements","Students Acheivements","Faculties Acheivements"};
        Image_Category.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));
        Image_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                category = Image_Category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         selectImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) { openGallery(); }
         });

         uploadImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(bitmap == null){
                     Toast.makeText(UploadImage.this, "Please upload Image", Toast.LENGTH_SHORT).show();
                 }else if(category.equals("Select Category")){
                     Toast.makeText(UploadImage.this, "Please Select Category", Toast.LENGTH_SHORT).show();
                 }else{
                      pd.setMessage("Uploading...");
                      pd.show();
                      uploadImage();
                 }
             }
         });



    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);
    }


    private void uploadImage() {
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 50 ,baos);
        byte[] finalimg = baos.toByteArray();
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss'.jpg'").format(new Date());
        final StorageReference FilePath;
         FilePath = storageReference.child(fileName);
//        FilePath = storageReference.child("test.jpg");
        final UploadTask uploadTask = FilePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UploadImage.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            FilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    uploadData();

                                }
                            });
                        }
                    });

                }else {
                    pd.dismiss();
                    Toast.makeText(UploadImage.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadData() {
        reference = reference.child(category);
        final String uniquekey = reference.push().getKey();

        assert uniquekey != null;
        reference.child(uniquekey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(UploadImage.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadImage.this, "Uploading failed", Toast.LENGTH_SHORT).show();
            }
        });
       reference=dummyreference;
    }

     @Override
                protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         UploadImage.super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == REQ && resultCode == RESULT_OK) {
             Uri uri = data.getData();
             try {
                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

             } catch (IOException e) {
                 e.printStackTrace();
             }
             galleryImageview.setImageBitmap(bitmap);
         }
     }

        }