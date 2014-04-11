package com.dryan.weather.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class WeeklytestWeather extends Weather {
    public WeeklytestWeather(Cursor cursor) {
        super(cursor);
    }

    public WeeklytestWeather() {}

    public Uri save(Context aContext) {
        return super.save(aContext);
    }
}
