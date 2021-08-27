package com.example.vecadminapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Deletenoticeactivity extends AppCompatActivity {

    private RecyclerView Deletenoticerecycler;
    private ProgressBar progressBar;
    private ArrayList<noticeData> list;
    private noticeadapter Adapter;

    private DatabaseReference reference;

    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletenoticeactivity);

        Deletenoticerecycler = findViewById(R.id.Deletenoticerecycler);
        progressBar = findViewById(R.id.progressbar);

        reference = FirebaseDatabase.getInstance().getReference().child("Notice");

        Deletenoticerecycler.setLayoutManager(new LinearLayoutManager(this));
        Deletenoticerecycler.setHasFixedSize(true);
        
        getNotice();

    }

    private void getNotice() {
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot datasnapshot) {
              list = new ArrayList<>();
              for (DataSnapshot snapshot1 : datasnapshot.getChildren()){
                  noticeData data = snapshot1.getValue(noticeData.class);
                  list.add(data);

              }

              Adapter = new noticeadapter(Deletenoticeactivity.this,list);
              Adapter.notifyDataSetChanged();
              progressBar.setVisibility(View.GONE);
              Deletenoticerecycler.setAdapter(Adapter);
           }


           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(Deletenoticeactivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
           }
       });
    }
}