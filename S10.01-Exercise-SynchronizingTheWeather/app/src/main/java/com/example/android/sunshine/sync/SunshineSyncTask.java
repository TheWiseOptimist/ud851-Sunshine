package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.CONTENT_URI;

//  TODO completed (1) Create a class called SunshineSyncTask
class SunshineSyncTask {

    //  TODO completed (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
    synchronized static void syncWeather(Context context)  {
        //  TODO completed (3) Within syncWeather, fetch new weather data

        try {
            URL url = NetworkUtils.getUrl(context);
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            ContentValues[] contentValues =
                    OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, response);
            //  TODO completed (4) If we have valid results, delete the old data and insert the new
            if (contentValues != null && contentValues.length > 0) {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(CONTENT_URI, null, null);
                contentResolver.bulkInsert(CONTENT_URI, contentValues);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}