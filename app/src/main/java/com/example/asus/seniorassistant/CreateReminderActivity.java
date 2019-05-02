package com.example.asus.seniorassistant;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateReminderActivity extends AppCompatActivity {

    SQLiteDatabase db;
    DatabaseHelper mDbHelper;
    EditText etTitle, etDescription,etDatePicker,etTimePicker;
    Button btnCreate;
    DatePicker datePicker;
    TimePicker timePicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
        setTitle("Set Reminder");
        mDbHelper = new DatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);



        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String detail = etDescription.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(mDbHelper.TITLE, title);
                cv.put(mDbHelper.DETAIL, detail);
                cv.put(mDbHelper.TIME, getString(R.string.NoSet));

                Calendar calender = Calendar.getInstance();
                calender.clear();
                calender.set(Calendar.MONTH, datePicker.getMonth());
                calender.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calender.set(Calendar.YEAR, datePicker.getYear());
                calender.set(Calendar.HOUR, timePicker.getCurrentHour());
                calender.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calender.set(Calendar.SECOND, 00);

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                String timeString = formatter.format(new Date(calender.getTimeInMillis()));
                SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = dateformatter.format(new Date(calender.getTimeInMillis()));

                AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(CreateReminderActivity.this, AlarmReceiver.class);

                String alertTitle = etTitle.getText().toString();
                intent.putExtra(getString(R.string.alerttitle), alertTitle);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateReminderActivity.this, 0, intent, 0);

                alarmMgr.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
                cv.put(mDbHelper.TIME, timeString);
                cv.put(mDbHelper.DATE, dateString);

                db.insert(mDbHelper.TABLE_NAME, null, cv);
                finish();
                Toast.makeText(CreateReminderActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                /*Intent openMainScreen = new Intent(this, MainActivity.class);
                openMainScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(openMainScreen);*/
            }
        });








    }

}
