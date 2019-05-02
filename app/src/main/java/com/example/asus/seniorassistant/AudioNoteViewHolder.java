package com.example.asus.seniorassistant;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AudioNoteViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    View mView;
    TextView textTitle, textTime, textDuration;
    CardView cvNote;

    public AudioNoteViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        root = mView.findViewById(R.id.audiolist_root);
        textTitle = mView.findViewById(R.id.tvAudioNoteTitle);
        textTime = mView.findViewById(R.id.tvAudioNoteTime);
        cvNote = mView.findViewById(R.id.cv_note);
        textDuration = mView.findViewById(R.id.tvDuration);
    }

    public void setNoteTitle(String title){
        textTitle.setText(title);
    }

    public void setNoteTime(String time){
        textTime.setText(time);
    }

    public void setDuration(String duration){ textDuration.setText(duration);}
}
