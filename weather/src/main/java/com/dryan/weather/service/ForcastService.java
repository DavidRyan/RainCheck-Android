package com.dryan.weather.service;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.dryan.weather.WeatherApp;
import com.dryan.weather.event.NetworkEvent;
import com.dryan.weather.model.CurrentWeather;
import com.dryan.weather.model.WeeklyWeather;
import com.dryan.weather.model.HourlyWeather;
import com.dryan.weather.model.MinuteWeather;
import com.dryan.weather.model.Weather;

import timber.log.Timber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ForcastService implements WeatherService {

    private RequestQueue mRequestQueue;
    private Context mContext;
    private static final String BASE_URL = "https://api.forecast.io/forecast/4ebd27f07f344acc582656e2f54f370e/%f,%f";

    public ForcastService(Context aContext) {
        mContext = aContext;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public class ForcastRequest extends JsonObjectRequest {

        public ForcastRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        protected Response<org.json.JSONObject> parseNetworkResponse(NetworkResponse response) {

            try {
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                JSONObject jsonResponse = new JSONObject(json);
                Timber.d("rep " + jsonResponse.optJSONObject("minutely"), new JSONObject());
                saveCurrent(jsonResponse.optJSONObject("currently"));
                saveHourly(jsonResponse.optJSONObject("hourly"));
                saveDaily(jsonResponse.optJSONObject("daily"));
                saveMinutely(jsonResponse.optJSONObject("minutely"));
                return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                e.printStackTrace();
                return Response.error(new ParseError(e));
            }
        }
    }

    @Override
    public void fetchWeatherWeather(double aLat, double aLong) {
        String url = String.format(BASE_URL, aLat, aLong );
        Timber.d("URL: " + url);
        ForcastRequest request = new ForcastRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        WeatherApp.BUS.post(new NetworkEvent(true));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                WeatherApp.BUS.post(new NetworkEvent(false));
                Timber.d(error.toString());
            }
        }
        );
        mRequestQueue.add(request);
    }

    public void saveDaily(JSONObject aDaily) {
        WeeklyWeather weeklyWeather = new WeeklyWeather();
        weeklyWeather.delete(mContext);
        JSONArray array = aDaily.optJSONArray("data");
        Timber.d("day " + array.optJSONObject(0).toString());
        for (int i = 0; i < array.length()-1; i++) {
            JSONObject object = array.optJSONObject(i);
            weeklyWeather = new WeeklyWeather();
            parse(object, weeklyWeather);
            weeklyWeather.save(mContext);
        }
    }

    public void saveHourly(JSONObject aHourly) {
        HourlyWeather hourly = new HourlyWeather();
        hourly.delete(mContext);
        JSONArray array = aHourly.optJSONArray("data");
        Timber.d("hour " + array.optJSONObject(0).toString());
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.optJSONObject(i);
            hourly = new HourlyWeather();
            parse(object, hourly);
            hourly.save(mContext);
        }
    }

    public void saveMinutely(JSONObject aMinutely) {
        MinuteWeather mintuely = new MinuteWeather();
        mintuely.delete(mContext);
        JSONArray array = aMinutely.optJSONArray("data");
        for (int i = 0; i < array.length(); i++) {

            JSONObject object = array.optJSONObject(i);
            mintuely = new MinuteWeather();
            parse(object, mintuely);
            mintuely.save(mContext);
        }
    }

    public void saveCurrent(JSONObject aCurrent) {
        Timber.d("current " + aCurrent);
        CurrentWeather weather = new CurrentWeather();
        weather.delete(mContext);
        parse(aCurrent, weather);
        weather.setId(0);
        weather.save(mContext);
    }

    @Override
    public void parse(JSONObject aResponse, Weather aWeather) {
        try {
            aWeather.setSummary(aResponse.optString("summary","-"));
            aWeather.setIcon(aResponse.optString("icon", ""));
            aWeather.setTime(aResponse.optLong("time", 3));
            aWeather.setPrecipProbability(100*aResponse.optDouble("precipProbability", 1212.00));
            aWeather.setWindSpeed(aResponse.optLong(Weather.WIND, 4));
            aWeather.setSunriseTime(aResponse.optLong("sunriseTime",0l));
            aWeather.setPrecipIntensity(aResponse.optDouble("precipIntensity", 4.0));
            aWeather.setPrecipType(aResponse.optString("precipType",""));
            aWeather.setApparentTemperature(aResponse.optDouble("apparentTemperature",0.0));
            aWeather.setTemperature(aResponse.optDouble("temperature",0.0));
            aWeather.setCloudCover(aResponse.optDouble("cloudCover",0.0));
            aWeather.setHumidity(aResponse.optDouble("humidity", 0.0));
            aWeather.setTemperatureMax(aResponse.optDouble("temperatureMax", 0));
            aWeather.setTemperatureMin(aResponse.optDouble("temperatureMin", 0));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
