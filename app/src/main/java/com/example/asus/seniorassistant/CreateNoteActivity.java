package com.example.asus.seniorassistant;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CreateNoteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageButton btnTitle,btnDesc;
    private final int REQ_CODE_SPEECH_INPUT1 = 101;
    private final int REQ_CODE_SPEECH_INPUT2 = 102;
    private ProgressDialog mProgressDialog;
    private Button btnCreate;
    private EditText etTitle,etDescription;
    private ImageView ivImage;
    private DatabaseReference noteDatabase;
    private FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri photoURI;
    private static final int REQUEST_CAMERA = 123;
    private static final int SELECT_FILE = 124;
    String pathtofile;
    String imageURL;
    String title, description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);
        mProgressDialog = new ProgressDialog(CreateNoteActivity.this);

        if(Build.VERSION.SDK_INT >=23){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }



        etTitle=(EditText)findViewById(R.id.etTitle);
        etDescription=(EditText)findViewById(R.id.etDescription);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnTitle = (ImageButton) findViewById(R.id.btnTitle);
        btnTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnDesc = (ImageButton) findViewById(R.id.btnDesc);
        btnDesc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput2();
            }
        });




        auth = FirebaseAuth.getInstance();
        noteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(auth.getCurrentUser().getUid());

        btnCreate = (Button)findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString().trim();
                description = etDescription.getText().toString().trim();


                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)){
                    //createNote(title,description);
                    uploadImage();
                    finish();



                    
                }else{
                    Toast.makeText(CreateNoteActivity.this, "Fill empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something&#8230");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT1);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void promptSpeechInput2() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something&#8230");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT2);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createNote(String title, String description) {
        if(auth.getCurrentUser()!=null){
            final DatabaseReference newNoteRef = noteDatabase.push();
            final Map noteMap = new HashMap();
            noteMap.put("title",title);
            noteMap.put("description",description);
            noteMap.put("timestamp",ServerValue.TIMESTAMP);
            noteMap.put("image",imageURL);


            Thread mainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(CreateNoteActivity.this, "Note added to database", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(CreateNoteActivity.this, "Error!Note added to database", Toast.LENGTH_SHORT).show();

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

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v,Gravity.RIGHT);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_photomenu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.takePhoto:
                Toast.makeText(this, "Take Photo", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    photoFile = createPhotoFile();
                    if(photoFile != null) {
                        pathtofile = photoFile.getAbsolutePath();
                        photoURI = FileProvider.getUriForFile(CreateNoteActivity.this,"com.example.asus.seniorassistant",photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                }
                return true;

            case R.id.takeFromFile:
                Toast.makeText(this, "Take Photo From File", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent();
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent2, "Select Picture"), SELECT_FILE);
                /*Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                startActivityForResult(intent2, SELECT_FILE);*/
                return true;
            default:
                return false;
        }
    }

    private File createPhotoFile() {
        //String name = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        //image = new File(storageDir, name + etTitle.getText()+ ".jpg");
        try {
           image = File.createTempFile("SeniorAssistant_",".jpg",storageDir);


        } catch (IOException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode==REQUEST_CAMERA){

                Bitmap bitmap = BitmapFactory.decodeFile(pathtofile);

                ivImage.setImageBitmap(bitmap);

            }else if(requestCode==SELECT_FILE){

                photoURI = data.getData();
                ivImage.setImageURI(photoURI);
            }else if(requestCode==REQ_CODE_SPEECH_INPUT1){
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etTitle.setText(result.get(0));
                }
            }else if(requestCode==REQ_CODE_SPEECH_INPUT2){
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etDescription.setText(result.get(0));
                }
            }

        }
    }

    private void uploadImage() {

        if(photoURI != null)
        {


            StorageReference ref = storageReference.child("/images/"+ UUID.randomUUID().toString());


            ref.putFile(photoURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL = uri.toString();
                                    createNote(title,description);

                                }
                            });

                            Toast.makeText(CreateNoteActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(CreateNoteActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());

                        }
                    });
        }
        else{
            Toast.makeText(this, "Empty URI", Toast.LENGTH_SHORT).show();
        }
    }

}
