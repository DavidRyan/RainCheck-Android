package com.dryan.weather.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class WeeklyWeather extends Weather {
    public WeeklyWeather(Cursor cursor) {
        super(cursor);
    }

    public WeeklyWeather() {}

    public Uri save(Context aContext) {
        return super.save(aContext);
    }
}
