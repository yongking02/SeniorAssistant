package com.example.asus.seniorassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class DeleteEditNote extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {



    private EditText etTitle,etDescription;
    private ImageView ivImage;
    private DatabaseReference noteDatabase;
    StorageReference storageReference;
    FirebaseStorage storage;
    private FirebaseAuth auth;
    private String noteID ;
    private Menu menu;
    private Uri photoURI;
    private static final int REQUEST_CAMERA = 123;
    private static final int SELECT_FILE = 124;

    String pathtofile;
    String imageURL;
    PhotoViewAttacher pAttacher;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteeditnote);

        try{
            noteID = getIntent().getStringExtra("noteId");
        }catch(Exception e){
            e.printStackTrace();
        }


        etTitle=(EditText)findViewById(R.id.etTitle);
        etDescription=(EditText)findViewById(R.id.etDescription);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        pAttacher = new PhotoViewAttacher(ivImage);
        pAttacher.update();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        auth = FirebaseAuth.getInstance();
        noteDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(auth.getCurrentUser().getUid());

        putData();
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
                if(photoURI!= null) {
                    uploadImage(); // include UploadImage and EditNote function
                }else {
                    EditNoteNoImage();
                }


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

    private void deleteNote(){
        noteDatabase.child(noteID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DeleteEditNote.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    /*noteID = "no";*/
                    finish();
                }else{
                    Toast.makeText(DeleteEditNote.this, "Error!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void EditNote(){
        Map updateMap =  new HashMap();
        updateMap.put("title",etTitle.getText().toString().trim());
        updateMap.put("description",etDescription.getText().toString().trim());
        updateMap.put("timestamp",ServerValue.TIMESTAMP);
        updateMap.put("image",imageURL);

        noteDatabase.child(noteID).updateChildren(updateMap);

        Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private void EditNoteNoImage(){
        Map updateMap =  new HashMap();
        updateMap.put("title",etTitle.getText().toString().trim());
        updateMap.put("description",etDescription.getText().toString().trim());
        updateMap.put("timestamp",ServerValue.TIMESTAMP);


        noteDatabase.child(noteID).updateChildren(updateMap);

        Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private void putData(){
        noteDatabase.child(noteID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("title")&& dataSnapshot.hasChild("description")) {

                    String title = dataSnapshot.child("title").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String imageURL = dataSnapshot.child("image").getValue().toString();


                    etTitle.setText(title);
                    etDescription.setText(description);
                    Picasso.get().load(imageURL).into(ivImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        photoURI = FileProvider.getUriForFile(DeleteEditNote.this,"com.example.asus.seniorassistant",photoFile);
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
            }

        }
    }

    private void uploadImage() {

        if(photoURI != null)
        {


            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());


            ref.putFile(photoURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL = uri.toString();
                                    EditNote();


                                }
                            });

                            Toast.makeText(DeleteEditNote.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(DeleteEditNote.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Empty photoURI", Toast.LENGTH_SHORT).show();
        }
    }

}


