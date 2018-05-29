package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private String weatherForDay;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // TODO completed (2) Display the weather forecast that was passed from MainActivity
        Intent intentReceivedFromMainActivity = getIntent();
        // The if statements implement safety precautions in retrieving the intent data.
        if (intentReceivedFromMainActivity != null
//                && intentReceivedFromMainActivity.getStringExtra(Intent.EXTRA_TEXT) != null) {
                && intentReceivedFromMainActivity.hasExtra(Intent.EXTRA_TEXT)) {
            weatherForDay = intentReceivedFromMainActivity.getStringExtra(Intent.EXTRA_TEXT);
        }
        TextView detailTextView = findViewById(R.id.detail_text_view);
        detailTextView.setText(weatherForDay);
    }
}