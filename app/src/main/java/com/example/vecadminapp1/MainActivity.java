package com.example.vecadminapp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView uploadnotice,addgalleryImage,addebook,delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadnotice=findViewById(R.id.addnotice);
        addgalleryImage = findViewById(R.id.addimage);
        addebook = findViewById(R.id.addebook);
        delete = findViewById(R.id.delete);


        uploadnotice.setOnClickListener(this);
        addgalleryImage.setOnClickListener(this);
        addebook.setOnClickListener(this);
        delete.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

            case R.id.addnotice:
                intent  = new Intent(MainActivity.this, com.example.vecadminapp1.uploadnotice.class);
                startActivity(intent);
                break;

            case R.id.addimage:
                intent = new Intent(MainActivity.this, com.example.vecadminapp1.UploadImage.class);
                startActivity(intent);
                break;

                case R.id.addebook:
                intent = new Intent(MainActivity.this, com.example.vecadminapp1.UploadPdfActivity.class);
                startActivity(intent);
                break;

            case R.id.delete:
                intent = new Intent(MainActivity.this,com.example.vecadminapp1.Deletenoticeactivity.class);
                startActivity(intent);
                break;

        }

    }
}