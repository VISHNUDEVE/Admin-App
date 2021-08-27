package com.example.vecadminapp1;
import android.content.Context;
import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class noticeadapter extends RecyclerView.Adapter<noticeadapter.noticeviewadapter>  {

    private final Context context;
    private final ArrayList<noticeData>list;


    public noticeadapter(Context context, ArrayList<noticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public noticeviewadapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_feed,parent,false);
        return new noticeviewadapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull noticeviewadapter holder, int position) {

        noticeData currentItem = list.get(position);

        holder.deletenoticetitle.setText(currentItem.getTitle());

        try {
            if (currentItem.getImage() != null)
            Picasso.get().load(currentItem.getImage()).into(holder.deletenoticeimage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.Deletenotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
                reference.child(currentItem.getKey()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                notifyItemRemoved(position);
            }

        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class noticeviewadapter extends RecyclerView.ViewHolder {

        private Button Deletenotice;
        private TextView deletenoticetitle;
        private ImageView deletenoticeimage;

        public noticeviewadapter(@NonNull View itemView) {
            super(itemView);
            Deletenotice = itemView.findViewById(R.id.Deletenotice);
            deletenoticetitle = itemView.findViewById(R.id.deletenoticetitle);
            deletenoticeimage = itemView.findViewById(R.id.deletenoticeimage);
        }
    }

}
