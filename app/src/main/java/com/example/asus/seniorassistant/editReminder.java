package com.example.asus.seniorassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class editReminder extends AppCompatActivity {

    SQLiteDatabase db;
    DatabaseHelper mDbHelper;
    EditText etTitle, etDescription,etDatePicker,etTimePicker;
    Button btnCreate;
    DatePicker datePicker;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Reminder");
        mDbHelper = new DatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);


        final long id = getIntent().getExtras().getLong(getString(R.string.row_id_log));

        Cursor cursor = db.rawQuery("select * from " + mDbHelper.TABLE_NAME + " where " + mDbHelper.C_ID + "=" + id, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                etTitle.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.TITLE)));
                etDescription.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.DETAIL)));
            }
            cursor.close();
        }

    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, MainActivity.class);
        startActivity(setIntent);
    }

    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminderedit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            case R.id.btnSave:
                final long id = getIntent().getExtras().getLong(getString(R.string.row_id_long));
                String title = etTitle.getText().toString();
                String detail = etDescription.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(mDbHelper.TITLE, title);
                cv.put(mDbHelper.DETAIL, detail);
                cv.put(mDbHelper.TIME, getString(R.string.NoSet));
                cv.putNull(mDbHelper.DATE);

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
                    Intent intent = new Intent(this, AlarmReceiver.class);

                    String alertTitle = etTitle.getText().toString();
                    intent.putExtra(getString(R.string.alerttitle), alertTitle);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

                    alarmMgr.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
                    cv.put(mDbHelper.TIME, timeString);
                    cv.put(mDbHelper.DATE, dateString);



                db.update(mDbHelper.TABLE_NAME, cv, mDbHelper.C_ID + "=" + id, null);
                Intent openMainScreen = new Intent(editReminder.this, MainActivity.class);
                openMainScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(openMainScreen);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
