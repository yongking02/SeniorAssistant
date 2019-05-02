package com.example.asus.seniorassistant;

import android.app.ProgressDialog;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RecordAudioActivity extends AppCompatActivity {

    private Button btnStart,btnStop;
    private EditText etTitle;
    private TextView tvDuration;
    private MediaRecorder mRecorder = null;
    private String mFileName = null;
    private StorageReference mStorage;
    private DatabaseReference audioNoteDatabase;
    private FirebaseAuth auth;
    private ProgressDialog mProgress;
    private Chronometer simpleChronometer;
    String title;
    String audioURL;
    long elapsedMillis;
    int s;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordaudio);
        mStorage = FirebaseStorage.getInstance().getReference();
        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer
        auth = FirebaseAuth.getInstance();
        audioNoteDatabase = FirebaseDatabase.getInstance().getReference().child("audioNotes").child(auth.getCurrentUser().getUid());

        etTitle = (EditText)findViewById(R.id.etTitle);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setEnabled(false);
        mProgress = new ProgressDialog(this);
        mFileName = getExternalCacheDir().getAbsolutePath(); //Absolute Path
        mFileName += "/recordedAudio.3gp"; //File Name

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString().trim();
                if(!TextUtils.isEmpty(title)){
                    //createNote(title,description);
                    startRecording();

                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                    Toast.makeText(RecordAudioActivity.this, "Start Recording", Toast.LENGTH_SHORT).show();


                }else{
                    Toast.makeText(RecordAudioActivity.this, "Fill empty fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                Toast.makeText(RecordAudioActivity.this, "Stop Recording", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //Get Source
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //Set Format
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //Set Encoder

        try{
            mRecorder.prepare(); //Prepare Recording
        }
        catch(Exception ex){
        }
        mRecorder.start(); //Start Recording
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start(); // start a chronometer
    }

    private void stopRecording(){
        simpleChronometer.stop(); // stop a chronometer
        mRecorder.stop();
        showElapsedTime();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio(){
        mProgress.setMessage("Uploading Audio ...");


        mProgress.show();
        mStorage = mStorage.child("/AudioNote/"+ UUID.randomUUID().toString());
        //mStorage = mStorage.child("Audio").child("new_audio.3gp"); //Create Firebase Reference
        Uri uri = Uri.fromFile(new File(mFileName));

        mStorage.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                audioURL = uri.toString();
                                createNote(title);
                                mProgress.dismiss();
                                finish();


                            }
                        });

                        Toast.makeText(RecordAudioActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void createNote(String title) {
        if(auth.getCurrentUser()!=null){
            final DatabaseReference newNoteRef = audioNoteDatabase.push();
            final Map noteMap = new HashMap();
            noteMap.put("title",title);
            noteMap.put("timestamp",ServerValue.TIMESTAMP);
            noteMap.put("audio",audioURL);
            noteMap.put("duration",s);


            Thread mainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(RecordAudioActivity.this, "AudioNote added to database", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(RecordAudioActivity.this, "Error!AudioNote added to database", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            });
            mainThread.start();





        }else{
            Toast.makeText(this, "User is not sign in!", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void showElapsedTime() {

        elapsedMillis = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        Toast.makeText(RecordAudioActivity.this, "Elapsed milliseconds: " + elapsedMillis,
                Toast.LENGTH_SHORT).show();
    }*/
    private void showElapsedTime() {
        elapsedMillis = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        s  = (int) (elapsedMillis/ 1000 % 60);
        Toast.makeText(this, "secod"+s, Toast.LENGTH_SHORT).show();
    }




}
