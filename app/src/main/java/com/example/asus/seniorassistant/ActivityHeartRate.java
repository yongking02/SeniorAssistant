package com.example.asus.seniorassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class ActivityHeartRate extends AppCompatActivity {

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartrate);

        barChart = (BarChart)findViewById(R.id.barChart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(44f,0));
        barEntries.add(new BarEntry(55f,1));
        barEntries.add(new BarEntry(66f,2));
        barEntries.add(new BarEntry(33f,3));
        barEntries.add(new BarEntry(50f,4));
        barEntries.add(new BarEntry(60f,5));
        barEntries.add(new BarEntry(56f,6));

        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        ArrayList<String> theDates = new ArrayList<>();
        theDates.add("Monday");
        theDates.add("Tuesday");
        theDates.add("Wednesday");
        theDates.add("Thursday");
        theDates.add("Friday");
        theDates.add("Saturday");
        theDates.add("Friday");

        BarData theData = new BarData((IBarDataSet) theDates, barDataSet);
        barChart.setData(theData);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

    }


}
