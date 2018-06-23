/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

//  TODO completed (21) Implement LoaderManager.LoaderCallbacks<Cursor>
public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";


    //  TODO completed (18) Create a String array containing the names of the desired data columns from our ContentProvider
    public static final String[] DETAIL_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };

    //  TODO completed (19) Create constant int values representing each column name's position above
    public static final int INDEX_SELECTED_WEATHER_DATE = 0;
    public static final int INDEX_SELECTED_WEATHER_CONDITIONS_ID = 1;
    public static final int INDEX_SELECTED_WEATHER_MAX_TEMP = 2;
    public static final int INDEX_SELECTED_WEATHER_MIN_TEMP = 3;
    public static final int INDEX_SELECTED_WEATHER_HUMIDITY = 4;
    public static final int INDEX_SELECTED_WEATHER_PRESSURE = 5;
    public static final int INDEX_SELECTED_WEATHER_WIND_SPEED = 6;
    public static final int INDEX_SELECTED_WEATHER_WIND_DEGREES = 7;

    //  TODO completed (20) Create a constant int to identify our loader used in DetailActivity
    private static final int ID_DETAIL_LOADER = 55;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

    //  TODO completed (15) Declare a private Uri field called mUri
    private Uri mUri;

    //  TODO completed (10) Remove the mWeatherDisplay TextView declaration

    //  TODO completed (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    private TextView mWeatherDate;
    private TextView mWeatherDescription;
    private TextView mWeatherHigh;
    private TextView mWeatherLow;
    private TextView mWeatherHumidity;
    private TextView mWeatherPressure;
    private TextView mWeatherWind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //  TODO completed (12) Remove mWeatherDisplay TextView

        //  TODO completed (13) Find each of the TextViews by ID
        mWeatherDate = findViewById(R.id.tv_selected_date);
        mWeatherDescription = findViewById(R.id.tv_selected_weather_description);
        mWeatherHigh = findViewById(R.id.tv_selected_max_temp);
        mWeatherLow = findViewById(R.id.tv_selected_min_temp);
        mWeatherHumidity = findViewById(R.id.tv_selected_humidity);
        mWeatherPressure = findViewById(R.id.tv_selected_pressure);
        mWeatherWind = findViewById(R.id.tv_selected_wind);

        //  TODO completed (14) Remove the code that checks for extra text
        //  TODO completed (16) Use getData to get a reference to the URI passed with this Activity's Intent
        mUri = getIntent().getData();

        //  TODO completed (17) Throw a NullPointerException if that URI is null
        if (mUri == null) throw new NullPointerException("Uri does not exist: " + mUri);

        // TODO completed (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }


    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    //  TODO completed (22) Override onCreateLoader
    //  TODO completed (23) If the loader requested is our detail loader, return the appropriate CursorLoader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:

                long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
                String mSelection = WeatherContract.WeatherEntry.COLUMN_DATE + " = " + normalizedUtcNow;

                return new CursorLoader(this,
                        mUri,
                        DETAIL_FORECAST_PROJECTION,
                        mSelection,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    //  TODO completed completed (24) Override onLoadFinished
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //  TODO completed (25) Check before doing anything that the Cursor has valid data
        if (data == null) return;

        //  TODO completed (26) Display a readable date string // said "data" instead of "date"
        String displayDate = SunshineDateUtils.getFriendlyDateString(this,
                data.getLong(INDEX_SELECTED_WEATHER_DATE),
                true);
        mWeatherDate.setText(displayDate);

        //  TODO completed (27) Display the weather description (using SunshineWeatherUtils)
        String displayDescription = getResources().getString(R.string.a11y_forecast,
                SunshineWeatherUtils.getStringForWeatherCondition(this,
                        data.getInt(INDEX_SELECTED_WEATHER_CONDITIONS_ID)));
        mWeatherDescription.setText(displayDescription);

        //  TODO completed (28) Display the high temperature
        String displayHigh = getResources().getString(
                R.string.a11y_high_temp,
                SunshineWeatherUtils.formatTemperature(this,
                        data.getDouble(INDEX_SELECTED_WEATHER_MAX_TEMP))
        );
        mWeatherHigh.setText(displayHigh);

        //  TODO completed (29) Display the low temperature
        String displayLow = getResources().getString(
                R.string.a11y_low_temp,
                SunshineWeatherUtils.formatTemperature(this,
                        data.getDouble(INDEX_SELECTED_WEATHER_MIN_TEMP))
        );
        mWeatherLow.setText(displayLow);

        //  TODO completed (30) Display the humidity
        String displayHumidity = getResources().getString(
                R.string.a11y_humidity,
                getResources().getString(
                        R.string.format_humidity,
                        data.getDouble(INDEX_SELECTED_WEATHER_HUMIDITY))
        );
        mWeatherHumidity.setText(displayHumidity);

        //  TODO completed (31) Display the wind speed and direction
        String displayWindSpeedAndDirection = getResources().getString(
                R.string.a11y_wind,
                SunshineWeatherUtils.getFormattedWind(
                        this,
                        data.getFloat(INDEX_SELECTED_WEATHER_WIND_SPEED),
                        data.getFloat(INDEX_SELECTED_WEATHER_WIND_DEGREES))
        );
        mWeatherWind.setText(displayWindSpeedAndDirection);

        //  TODO completed (32) Display the pressure
        String displayPressure = getResources().getString(
                R.string.format_pressure,
                data.getDouble(INDEX_SELECTED_WEATHER_PRESSURE)
        );
        mWeatherPressure.setText(displayPressure);

        //  TODO completed (33) Store a forecast summary in mForecastSummary
        mForecastSummary = displayDate + "\n" +
                displayDescription + "\n" +
                displayHigh + "\n" +
                displayLow + "\n" +
                displayHumidity + "\n" +
                displayWindSpeedAndDirection + "\n" +
                displayPressure;
    }

    //  TODO completed (34) Override onLoaderReset, but don't do anything in it yet
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


}