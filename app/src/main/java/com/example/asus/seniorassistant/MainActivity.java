package com.example.asus.seniorassistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech myTTS;
    private SpeechRecognizer mySR;
    private final int MY_PERMISSION_RECORD_AUDIO =1;
    

    FirebaseAuth auth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;
    BottomNavigationView btmNav;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();


        requestAudioPermissions();

        btmNav = (BottomNavigationView) findViewById(R.id.btm_navigation);
        btmNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();




       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/



        FloatingActionButton btnSpeak = (FloatingActionButton)findViewById(R.id.btnSpeak);
        btnSpeak.setAlpha(0.85f);
        btnSpeak.setOnClickListener((view)->{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
            mySR.startListening(intent);
        });

        initializeTTS();
        initializeSR();







    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void initializeSR() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            mySR = SpeechRecognizer.createSpeechRecognizer(this);
            mySR.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle s) {
                    List<String> results = s.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void processResult(String command) {
        command = command.toLowerCase();
        if(command.indexOf("what") !=-1){
            if(command.indexOf("your name")!= -1){
                speak(("My name is Alo."));
            }
            if(command.indexOf("time")!= -1) {
                Date now = new Date();
                String time = DateUtils.formatDateTime(this, now.getTime(),
                        DateUtils.FORMAT_SHOW_TIME);
                speak("This time now is " + time);
            }
            if(command.indexOf("date")!= -1) {
                Date now = new Date();
                String date = DateUtils.formatDateTime(this, now.getTime(),
                        DateUtils.FORMAT_SHOW_DATE);
                speak("This date now is " + date);
            }
        } else if (command.indexOf("open")!= -1) {
            if (command.indexOf("reminder") != -1){

                btmNav.setSelectedItemId(R.id.bnav_reminder);
                Fragment fragment = new ReminderFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }

        }
    }


    private void initializeTTS() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(myTTS.getEngines().size() ==0){
                    Toast.makeText(MainActivity.this, "No TTS engine on your device", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    myTTS.setLanguage(Locale.ENGLISH);
                    //speak("Hello, I am ready.");
                }
                
            }
        });
    }

    private void speak(String message) {
        if(Build.VERSION.SDK_INT >=21){
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH,null,null);
        }else{
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null);

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.bnav_home:
                            selectedFragment = new HomeFragment();

                            break;
                        case R.id.bnav_reminder:
                            selectedFragment = new ReminderFragment();

                            break;
                        case R.id.bnav_note:
                            selectedFragment = new NoteFragment();
                            break;
                        case R.id.bnav_audioNote:
                            selectedFragment = new AudioNoteFragment();
                            break;
                        case R.id.bnav_account:
                            selectedFragment = new AccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };


    @Override
    protected void onPause() {
        super.onPause();
        myTTS.shutdown();
    }



    private void requestAudioPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.RECORD_AUDIO)){
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_SHORT).show();


                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_RECORD_AUDIO);
            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case MY_PERMISSION_RECORD_AUDIO:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initializeTTS();
                }else{
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


}
