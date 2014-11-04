package com.dryan.weather.service;

import android.content.Context;

import com.dryan.weather.model.CurrentWeather;
import com.dryan.weather.model.WeeklyWeather;
import com.dryan.weather.model.HourlyWeather;
import com.dryan.weather.model.MinuteWeather;
import com.dryan.weather.model.Weather;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ForcastService implements WeatherService {

    private Context mContext;
    private static final String BASE_URL = "https://api.forecast.io/forecast/4ebd27f07f344acc582656e2f54f370e/%f,%f";
    private static String json;

    public ForcastService(Context aContext) {
        mContext = aContext;
    }

    @Override
    public void fetchWeatherWeather(double aLat, double aLong) {

    }

    @Override
    public void parse(JSONObject aResponse, Weather aWeather) {

    }

    public static Observable<Weather> fetchWeatherObservable(final String aUrl, final String aType) {
        final OkHttpClient client = new OkHttpClient();
        return Observable.create((Observable.OnSubscribe<JSONObject>) subscriber -> {
            try {
                if (json == null) {
                    final String url = "https://api.forecast.io/forecast/4ebd27f07f344acc582656e2f54f370e/35.2229,-80.8380"; //String.format(BASE_URL, 35.2229,-80.8380);
                    Timber.d("https://api.forecast.io/forecast/4ebd27f07f344acc582656e2f54f370e/35.2229,-80.8380");
                    Request request = new Request.Builder()
                    .url("https://api.forecast.io/forecast/4ebd27f07f344acc582656e2f54f370e/35.2229,-80.8380")
                    .build();

                    Call call = client.newCall(request);
                    Response response = null;
                    try {
                        response = call.execute();
                        if (response.isSuccessful()) {
                            json = response.body().string();
                            Timber.d("RESPONSE: " +  json);
                        }
                    } catch (IOException e) {
                        Timber.e("error",e);
                        subscriber.onError(e);
                    }
                }

                // Because forcast's api is stupid.
                JSONArray data;
                if (!aType.equals("currently")) {
                    data = new JSONObject(json).getJSONObject(aType).getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        subscriber.onNext(data.getJSONObject(i));
                    }
                } else {
                    subscriber.onNext(new JSONObject(json).getJSONObject(aType));
                }
                subscriber.onCompleted();

            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        }).map(json -> {
            Weather weather = new Weather();
            parseWeather(json, weather);
            return weather;
        });
    }


    public static void parseWeather(JSONObject aResponse, Weather aWeather) {
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
