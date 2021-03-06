package com.dryan.weather.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.webkit.WebIconDatabase;

import com.vokal.database.AbstractDataModel;
import com.vokal.database.SQLiteTable;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class Weather extends AbstractDataModel {

    public static final String TIME = "time";
    public static final String SUMMARY = "summary";
    public static final String ICON = "icon";
    public static final String SUNRISE = "sunrise_time";
    public static final String PRECIPT_INTENS = "precip_intensity";
    public static final String PRECIP_TYPE = "precip_type";
    public static final String TEMP ="temperature";
    public static final String CLOUD_COVER = "cloud_cover";
    public static final String HUMIDITY = "humidity";
    public static final String WIND = "windSpeed";
    public static final String PRECIP = "precipProbability";
    public static final String TEMP_MAX = "temperature_max";
    public static final String TEMP_MIN = "temperature_min";
    public static final String APPARENT_TEMP = "apparent_temp";

    private long time;
    private String summary;
    private String icon;
    private long sunriseTime;
    private double precipIntensity;
    private String precipType;
    private double temperature;
    private double cloudCover;
    private double humidity;
    private double temperatureMax;
    private double temperatureMin;
    private double precipProbability;
    private double windSpeed;
    private int id;
    private double apparentTemperature;

    public Weather() {

    }

    public Weather(Cursor aCursor) {
        id = aCursor.getInt(aCursor.getColumnIndex("_id"));
        summary = aCursor.getString(aCursor.getColumnIndex(SUMMARY));
        icon = aCursor.getString(aCursor.getColumnIndex(ICON));
        sunriseTime = aCursor.getInt(aCursor.getColumnIndex(SUNRISE));
        precipIntensity = aCursor.getDouble(aCursor.getColumnIndex(PRECIPT_INTENS));
        humidity = aCursor.getDouble(aCursor.getColumnIndex(HUMIDITY));
        windSpeed = aCursor.getDouble(aCursor.getColumnIndex(WIND));
        precipProbability = aCursor.getDouble(aCursor.getColumnIndex(PRECIP));
        time = aCursor.getLong(aCursor.getColumnIndex(TIME));
        precipType = aCursor.getString(aCursor.getColumnIndex(PRECIP_TYPE));
        temperature = aCursor.getInt(aCursor.getColumnIndex(TEMP));
        cloudCover = aCursor.getInt(aCursor.getColumnIndex(HUMIDITY));
        temperatureMax = aCursor.getInt(aCursor.getColumnIndex(TEMP_MAX));
        temperatureMin = aCursor.getInt(aCursor.getColumnIndex(TEMP_MIN));
        apparentTemperature = aCursor.getInt(aCursor.getColumnIndex(APPARENT_TEMP));
    }

    @Override
    protected SQLiteTable buildTableSchema(SQLiteTable.Builder aBuilder) {
        aBuilder.addIntegerColumn("_id").primaryKey().autoincrement().unique()
                .addStringColumn(SUMMARY)
                .addStringColumn(ICON)
                .addIntegerColumn(SUNRISE)
                .addStringColumn(PRECIPT_INTENS)
                .addStringColumn(PRECIP_TYPE)
                .addIntegerColumn(TEMP)
                .addIntegerColumn(PRECIP)
                .addIntegerColumn(WIND)
                .addIntegerColumn(CLOUD_COVER)
                .addIntegerColumn(HUMIDITY)
                .addIntegerColumn(APPARENT_TEMP)
                .addRealColumn(TIME)
                .addIntegerColumn(TEMP_MAX)
                .addIntegerColumn(TEMP_MIN);
        return aBuilder.build();
    }

    @Override
    protected SQLiteTable updateTableSchema(SQLiteTable.Builder aBuilder, int aOldVersion) {
        return null;
    }

    @Override
    protected void populateContentValues(ContentValues aValues) {
        aValues.put(SUMMARY, summary);
        aValues.put(ICON, icon);
        aValues.put(SUNRISE, sunriseTime);
        aValues.put(PRECIPT_INTENS, precipIntensity);
        aValues.put(PRECIP_TYPE, precipType);
        aValues.put(TEMP, (int) temperature);
        aValues.put(CLOUD_COVER, cloudCover);
        aValues.put(HUMIDITY, humidity);
        aValues.put(APPARENT_TEMP, apparentTemperature);
        aValues.put(WIND, windSpeed);
        aValues.put(PRECIP, precipProbability);
        aValues.put(TIME, time);
        aValues.put(TEMP_MAX, temperatureMax);
        aValues.put(TEMP_MIN, temperatureMin);
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String aSummary) {
        summary = aSummary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }
}
