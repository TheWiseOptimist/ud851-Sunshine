/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.sunshine.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherJsonUtils {


    /* Location information */
    private static final String OWM_CITY = "city";
    private static final String OWM_COORD = "coord";

    /* Location coordinate */
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";

    /* Weather information. Each day's forecast info is an element of the "list" array */
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    /* All temperatures are children of the "temp" object */
    private static final String OWM_TEMPERATURE = "temp";

    /* Max temperature for the day */
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";

    private static final String OWM_MESSAGE_CODE = "cod";

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forecastJsonStr) {

        ContentValues[] weatherContentValues = null;
        JSONObject forecastJson;
        try {
            forecastJson = new JSONObject(forecastJsonStr);


            /* Is there an error? */
            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        /* Location invalid */
                        return null;
                    default:
                        /* Server probably down */
                        return null;
                }
            }

            JSONArray jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);


            SunshinePreferences.setLocationDetails(context, cityLatitude, cityLongitude);

            weatherContentValues = new ContentValues[jsonWeatherArray.length()];

            /*
             * OWM returns daily forecasts based upon the local time of the city that is being asked
             * for, which means that we need to know the GMT offset to translate this data properly.
             * Since this data is also sent in-order and the first day is always the current day, we're
             * going to take advantage of that to get a nice normalized UTC date for all of our weather.
             */
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);

            long normalizedUtcStartDay = SunshineDateUtils.getNormalizedUtcDateForToday();

            for (int i = 0; i < jsonWeatherArray.length(); i++) {

                long dateTimeMillis;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                int weatherId;

                /* Get the JSON object representing the day */
                JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);

                /*
                 * We ignore all the datetime values embedded in the JSON and assume that
                 * the values are returned in-order by day (which is not guaranteed to be correct).
                 */
                dateTimeMillis = normalizedUtcStartDay + SunshineDateUtils.DAY_IN_MILLIS * i;

                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                /*
                 * Description is in a child array called "weather", which is 1 element long.
                 * That element also contains a weather code.
                 */
                JSONObject weatherObject =
                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                /*
                 * Temperatures are sent by Open Weather Map in a child object called "temp".
                 *
                 * Editor's Note: Try not to name variables "temp" when working with temperature.
                 * It confuses everybody. Temp could easily mean any number of things, including
                 * temperature, temporary variable, temporary folder, temporary employee, or many
                 * others, and is just a bad variable name.
                 */
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);

                ContentValues weatherValues = new ContentValues();
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                weatherContentValues[i] = weatherValues;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherContentValues;
    }
}
/*
Parameters: in Current Weather Forecast https://openweathermap.org/current
        (FREE OR PAID) http://api.openweathermap.org/data/2.5/weather?zip=85204,us&units=imperial&appid=b4565f4409c46a000a99340410117f4a
        coord
        coord.lon City geo location, longitude
        coord.lat City geo location, latitude
        weather (more info Weather condition codes)
        weather.id Weather condition id
        weather.main Group of weather parameters (Rain, Snow, Extreme etc.)
        weather.description Weather condition within the group
        weather.icon Weather icon id
        base Internal parameter
        main
        main.temp Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
        main.pressure Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
        main.humidity Humidity, %
        main.temp_min Minimum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
        main.temp_max Maximum temperature at the moment. This is deviation from current temp that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
        main.sea_level Atmospheric pressure on the sea level, hPa
        main.grnd_level Atmospheric pressure on the ground level, hPa
        wind
        wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
        wind.deg Wind direction, degrees (meteorological)
        clouds
        clouds.all Cloudiness, %
        rain
        rain.3h Rain volume for the last 3 hours
        snow
        snow.3h Snow volume for the last 3 hours
        dt Time of data calculation, unix, UTC
        sys
        sys.type Internal parameter
        sys.id Internal parameter
        sys.message Internal parameter
        sys.country Country code (GB, JP etc.)
        sys.sunrise Sunrise time, unix, UTC
        sys.sunset Sunset time, unix, UTC
        id City ID
        name City name
        cod Internal parameter
*/

/*
Parameters: in 5-day Weather Forecast https://openweathermap.org/forecast5
        (FREE OR PAID) http://api.openweathermap.org/data/2.5/forecast?zip=85204,us&units=imperial&appid=b4565f4409c46a000a99340410117f4a
        code Internal parameter
        message Internal parameter
        city
        city.id City ID
        city.name City name
        city.coord
        city.coord.lat City geo location, latitude
        city.coord.lon City geo location, longitude
        city.country Country code (GB, JP etc.)
        cnt Number of lines returned by this API call
        list
        list.dt Time of data forecasted, unix, UTC
        list.main
        list.main.temp Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
        list.main.temp_min Minimum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
        list.main.temp_max Maximum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large cities and megalopolises geographically expanded (use these parameter optionally). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
        list.main.pressure Atmospheric pressure on the sea level by default, hPa
        list.main.sea_level Atmospheric pressure on the sea level, hPa
        list.main.grnd_level Atmospheric pressure on the ground level, hPa
        list.main.humidity Humidity, %
        list.main.temp_kf Internal parameter
        list.weather (more info Weather condition codes)
        list.weather.id Weather condition id
        list.weather.main Group of weather parameters (Rain, Snow, Extreme etc.)
        list.weather.description Weather condition within the group
        list.weather.icon Weather icon id
        list.clouds
        list.clouds.all Cloudiness, %
        list.wind
        list.wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
        list.wind.deg Wind direction, degrees (meteorological)
        list.rain
        list.rain.3h Rain volume for last 3 hours, mm
        list.snow
        list.snow.3h Snow volume for last 3 hours
        list.dt_txt Data/time of calculation, UTC

*/
/*
    Parameters: in 16-day Weather Forecast https://openweathermap.org/forecast16
    (PAID ONLY) http://api.openweathermap.org/data/2.5/forecast/daily?zip=85204,us&units=imperial&appid=b4565f4409c46a000a99340410117f4a
    city
    city.id City ID
    city.name City name
    city.coord
    city.coord.lat City geo location, latitude
    city.coord.lon City geo location, longitude
    city.country Country code (GB, JP etc.)
    cod Internal parameter
    message Internal parameter
    cnt Number of lines returned by this API call
    list
    list.dt Time of data forecasted
    list.temp
    list.temp.day Day temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    list.temp.min Min daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    list.temp.max Max daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    list.temp.night Night temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    list.temp.eve Evening temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    list.temp.morn Morning temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    list.pressure Atmospheric pressure on the sea level, hPa
    list.humidity Humidity, %
            list.weather (more info Weather condition codes)
    list.weather.id Weather condition id
    list.weather.main Group of weather parameters (Rain, Snow, Extreme etc.)
    list.weather.description Weather condition within the group
    list.weather.icon Weather icon id
    list.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
    list.deg Wind direction, degrees (meteorological)
    list.clouds Cloudiness, %
*/