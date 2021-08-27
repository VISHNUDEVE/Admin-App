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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;

public class uploadnotice extends AppCompatActivity {
    private CardView addImage;
    private ImageView noticeImageView;
    private EditText noticetitle;
    private Button noticebtn;


    private final int REQ=1;

    private Bitmap bitmap;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl = "";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);


        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);

        addImage= findViewById(R.id.addimage);
        noticeImageView = findViewById(R.id.noticeImageview);
        noticetitle = findViewById(R.id.noticetitle);
        noticebtn = findViewById(R.id.noticebtn);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openGallery();
            }
        });

        noticebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticetitle.getText().toString().isEmpty()){
                    noticetitle.setError("Empty");
                    noticetitle.requestFocus();
                }else if(bitmap == null){
                    uploaddata();

                }else {
                    uploadimage();
                }
            }
        });

    }

    private void uploadimage() {
        pd.setMessage("Uploading...");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 50 ,baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference FilePath;
        FilePath = storageReference.child("Notice").child(finalimg +"jpg");
        final UploadTask uploadTask = FilePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(uploadnotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    uploaddata();

                                }
                            });
                        }
                    });

                }else {
                    pd.dismiss();
                    Toast.makeText(uploadnotice.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploaddata() {
        DatabaseReference dbRef = reference.child("Notice");
        final String uniqueKey = dbRef.push().getKey();

        String Title = noticetitle.getText().toString();

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat CurrentDate = new SimpleDateFormat("dd-MM-yy");
        String date = CurrentDate.format(callforDate.getTime());

        Calendar callforTime = Calendar.getInstance();
        SimpleDateFormat CurrenTime = new SimpleDateFormat("hh:mm a");
        String time = CurrenTime.format(callforTime.getTime());

        noticeData noticeData = new noticeData(Title,downloadUrl,date,time,uniqueKey);

        assert uniqueKey != null;
        dbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(uploadnotice.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(uploadnotice.this,"something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            assert data != null;
            Uri uri = data.getData();
              try {
                  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
              }
              catch(IOException e){
                  e.printStackTrace();
              }
              noticeImageView.setImageBitmap(bitmap);
        }
    }
}