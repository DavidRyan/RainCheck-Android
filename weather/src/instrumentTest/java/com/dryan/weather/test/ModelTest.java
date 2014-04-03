package com.dryan.weather.test;

import android.content.Context;
import android.database.Cursor;
import android.test.InstrumentationTestCase;

import com.dryan.weather.model.CurrentWeather;
import com.dryan.weather.service.MockForcastWeatherService;
import com.vokal.database.DatabaseHelper;

/**
 * Created by Cold-One-City-USA on 3/12/14.
 */
public class ModelTest extends InstrumentationTestCase {
    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getContext();
    }

    public void testParse() {
        MockForcastWeatherService service = new MockForcastWeatherService(mContext);
        service.fetchWeatherWeather(0.0,0.0);
        Cursor c = mContext.getContentResolver().query(DatabaseHelper.getContentUri(CurrentWeather.class),null,null,null,null);
        assertTrue("Cursor Empty", c.moveToFirst());
        CurrentWeather weather = new CurrentWeather(c);
        assertEquals("Got: " + (int) weather.getTemperature() + "Looked for: " + 33, weather.getTemperature(), 33);
    }
}
