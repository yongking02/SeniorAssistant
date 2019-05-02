package com.example.asus.seniorassistant;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ViewRecordedAudio extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference audioNoteDatabase;
    private String audioNoteID ;
    private TextView tvTitle;
    private StorageReference mStorage;
    private Button btnStart, btnStop;
    String audioURL;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recorded_audio);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        try{
            audioNoteID = getIntent().getStringExtra("audioNoteId");
           /* if(noteID.equals("no")) {
                menu.getItem(0).setVisible(false);

            }*/
        }catch(Exception e){
            e.printStackTrace();
        }

        auth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        if(auth.getCurrentUser()!=null){
            audioNoteDatabase = FirebaseDatabase.getInstance().getReference().child("audioNotes").child(auth.getCurrentUser().getUid());

        }

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        putData();


        btnStart = (Button)findViewById(R.id.btnStart) ;
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        btnStop = (Button)findViewById(R.id.btnStop) ;
        btnStop.setEnabled(false);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
                btnStop.setEnabled(false);
                btnStart.setEnabled(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editdelete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.note_btnDeletenote:
                deleteNote();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void putData(){
        audioNoteDatabase.child(audioNoteID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("title")) {

                    String title = dataSnapshot.child("title").getValue().toString();
                    audioURL = dataSnapshot.child("audio").getValue().toString();

                    tvTitle.setText(title);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void stopAudio(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }


    private void playAudio(){
        mediaPlayer = new MediaPlayer();
        //convert millis to appropriate time
        try{
            mediaPlayer.setDataSource(audioURL);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepare();

        }catch (IOException e){
            e.printStackTrace();

        }


    }

    private void deleteNote(){
        audioNoteDatabase.child(audioNoteID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ViewRecordedAudio.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    /*noteID = "no";*/
                    finish();
                }else{
                    Toast.makeText(ViewRecordedAudio.this, "Error!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
