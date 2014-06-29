package com.dryan.weather.service;

import android.content.Context;

import com.dryan.weather.BuildConfig;
import com.dryan.weather.MainActivity;
import com.dryan.weather.WeatherApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */

@Module(library = true,
        overrides = true,
        injects = {
                MainActivity.class,
                WeatherApp.class
        })
public class ForcastServiceModule {

    Context mContext;

    public ForcastServiceModule(Context aContext) {
        mContext = aContext;
    }

    @Provides
    @Singleton
    public WeatherService providesWeatherService() {
        return new MocForcastWeatherService(mContext);
        //return new ForcastService(mContext);
    }

}
