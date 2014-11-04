package com.dryan.weather.service;

import com.dryan.weather.model.Weather;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public interface WeatherService {
    public void fetchWeatherWeather(double aLat, double aLong) throws IOException;
    public void parse(JSONObject aResponse, Weather aWeather);
}
