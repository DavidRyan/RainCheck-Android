package com.dryan.weather;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import com.dryan.weather.model.CurrentWeather;
import com.dryan.weather.model.WeeklyWeather;
import com.dryan.weather.model.HourlyWeather;
import com.dryan.weather.model.MinuteWeather;
import com.dryan.weather.service.ForcastServiceModule;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.vokal.database.DatabaseHelper;


import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */

public class WeatherApp extends Application {

    private static ObjectGraph mObjectGraph;

    public static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Typeface sRoboto;
    public static Typeface sRobotoBold;
    public static Context sApplication;

    static {
        Timber.plant(new Timber.DebugTree());
    }

    public static void inject(Object aObject) {
        mObjectGraph.inject(aObject);
    }

    @Override
    public void onCreate() {
        resetObjectGraph();
        DatabaseHelper.registerModel(this, CurrentWeather.class);
        DatabaseHelper.registerModel(this, HourlyWeather.class);
        DatabaseHelper.registerModel(this, WeeklyWeather.class);
        DatabaseHelper.registerModel(this, MinuteWeather.class);
        sApplication = this;
    }

    private void resetObjectGraph() {
        mObjectGraph = ObjectGraph.create(new ForcastServiceModule(this));
    }

    public static Typeface getRoboto(Context aContext) {
        if (sRoboto == null) {
            sRoboto = Typeface.createFromAsset(aContext.getAssets(), "fonts/roboto-light.ttf");
        }
        return sRoboto;
    }


    public static Typeface getRobotoBold(Context aContext) {
        if (sRobotoBold == null) {
            sRobotoBold = Typeface.createFromAsset(aContext.getAssets(), "fonts/roboto-bold.ttf");
        }
        return sRobotoBold;
    }

    public void setLocation(String aKey, String aValue) {
        SharedPreferences prefs = this.getSharedPreferences(
                "weather", Context.MODE_PRIVATE);
        prefs.edit().putString(aKey, aValue).commit();
    }

    public String getLocation(String aKey) {
        SharedPreferences prefs = this.getSharedPreferences(
                "weather", Context.MODE_PRIVATE);
        return prefs.getString(aKey,"");
    }

    public static Context get() {
        return sApplication;
    }
}
