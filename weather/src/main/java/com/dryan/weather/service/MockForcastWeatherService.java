package com.dryan.weather.service;

import android.content.Context;

import com.dryan.weather.WeatherApp;
import com.dryan.weather.event.NetworkEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class MockForcastWeatherService extends ForcastService {

    private Context mContext;

    public MockForcastWeatherService(Context aContext) {
        super(aContext);
        mContext = aContext;
    }

    @Override
    public void fetchWeatherWeather(double aLat, double aLong) {
        // on main thread but needs to be serial for unit testing
        try {
            String json = loadJSONFromAsset();
            JSONObject jsonResponse = null;
            jsonResponse = new JSONObject(json);
            Timber.d("rep " + jsonResponse.optJSONObject("minutely"), new JSONObject());
            //saveCurrent(jsonResponse.optJSONObject("currently"));
            //saveHourly(jsonResponse.optJSONObject("hourly"));
            //saveDaily(jsonResponse.optJSONObject("daily"));

            //saveMinutely(jsonResponse.optJSONObject("minutely"));
            WeatherApp.BUS.post(new NetworkEvent(true));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
