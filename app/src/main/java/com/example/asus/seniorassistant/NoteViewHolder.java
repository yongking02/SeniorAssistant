package com.example.asus.seniorassistant;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    View mView;
    TextView textTitle, textTime;
    ImageView imageView;
    CardView cvNote;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        root = mView.findViewById(R.id.list_root);
        textTitle = mView.findViewById(R.id.tvNoteTitle);
        textTime = mView.findViewById(R.id.tvNoteTime);
        imageView = mView.findViewById(R.id.ivImage);

        cvNote = mView.findViewById(R.id.cv_note);
    }

    public void setNoteTitle(String title){
        textTitle.setText(title);
    }

    public void setNoteTime(String time){
        textTime.setText(time);
    }

}
