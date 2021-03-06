package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private String mForecast;
    private TextView mWeatherDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDisplay = (TextView) findViewById(R.id.tv_display_weather);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mForecast = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherDisplay.setText(mForecast);
            }
        }
    }

    // TODO completed (3) Create a menu with an item with id of action_share
    // TODO completed (4) Display the menu and implement the forecast sharing functionality

    // My original approach
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.detail_forecast, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_share) {
//            shareWeatherDetail();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void shareWeatherDetail() {
//        String mimeType = "text/plain";
//        String title = "Share the Weather";
//        String subject = "Weather Info";
//        ShareCompat.IntentBuilder.from(this)
//                .setType(mimeType)
//                .setText(mForecast + FORECAST_SHARE_HASHTAG)
//                .setChooserTitle(title)
//                .setSubject(subject)
//                .startChooser();
//    }

        // modified Udacity approach
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.detail_forecast, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            menuItem.setIntent(
                    ShareCompat.IntentBuilder.from(this)
                            .setType("plain/text")
                            .setText(mForecast + FORECAST_SHARE_HASHTAG)
                            .setSubject("Weather Info")
                            .getIntent()
            );
            return true;
        }


    }
