package com.dryan.weather.model;

import android.database.Cursor;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class CurrentWeather extends Weather {

    public CurrentWeather() {

    }

    public CurrentWeather(Cursor data) {
        super(data);
    }

}
