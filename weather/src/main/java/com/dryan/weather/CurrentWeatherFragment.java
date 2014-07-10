package com.dryan.weather;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dryan.weather.model.CurrentWeather;
import com.dryan.weather.model.MinuteWeather;
import com.dryan.weather.model.Weather;
import com.dryan.weather.widget.WeatherWidget.IconUtil;
import com.dryan.weather.widget.WeatherWidget.PrecipitationGraph.Point;
import com.dryan.weather.widget.WeatherWidget.PrecipitationGraph;
import com.dryan.weather.widget.WeatherWidget.SkyconsDrawable;
import com.dryan.weather.widget.WeatherWidget.TemperatureGraph;
import com.vokal.database.DatabaseHelper;


import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class CurrentWeatherFragment extends Fragment  {

    public static final int MINUTE_LOADER = 0;
    public static final int CURRENT_LOADER = 1;
    public static final int HOUR_LOADER = 2;


    @InjectView(R.id.temp)
    TextView mTemp;

    @InjectView(R.id.powered)
    TextView mPowered;

    @InjectView(R.id.location)
    TextView mLocation;

    @InjectView(R.id.summary)
    TextView mSummary;

    @InjectView(R.id.feels_like)
    TextView mFeels;

    @InjectView(R.id.humidity_value)
    TextView mHumidity;

    @InjectView(R.id.wind_value)
    TextView mWind;

    @InjectView(R.id.precip_value)
    TextView mPrecip;

    @InjectView(R.id.humidity)
    TextView mHumidityLabel;

    @InjectView(R.id.wind)
    TextView mWindLabel;

    @InjectView(R.id.precip)
    TextView mPrecipLabel;

    @InjectView(R.id.precip_graph_label)
    TextView mGraphLabel;

    /*
    @InjectView(R.id.temp_graph_label)
    TextView mTempGraphLabel;
    */

    @InjectView(R.id.chart)
    PrecipitationGraph mChart;

    @InjectView(R.id.icon)
    ImageView mIcon;

    WeatherCursorLoader mLoader = new WeatherCursorLoader();
    private SkyconsDrawable mDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, v);
        getLoaderManager().initLoader(MINUTE_LOADER, null, mLoader);
        getLoaderManager().initLoader(HOUR_LOADER, null, mLoader);
        getLoaderManager().initLoader(CURRENT_LOADER, null, mLoader);
        mHumidityLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
        mGraphLabel.setTypeface(WeatherApp.getRobotoBold(getActivity()));
    //    mTempGraphLabel.setTypeface(WeatherApp.getRobotoBold(getActivity()));
        mWindLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
        mPrecipLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
        mLocation.setTypeface(WeatherApp.getRoboto(getActivity()));
        mPowered.setTypeface(WeatherApp.getRoboto(getActivity()));
        getActivity().getActionBar().setTitle(getActivity().getString(R.string.ab_current_title));
        return v;
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(CURRENT_LOADER, null, mLoader);
        getLoaderManager().restartLoader(MINUTE_LOADER, null, mLoader);
      //  getLoaderManager().restartLoader(HOUR_LOADER, null, mLoader);
        if (mDrawable != null) mDrawable.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDrawable != null) mDrawable.stop();
    }

    private class WeatherCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == CURRENT_LOADER) {
                return new CursorLoader(getActivity(), DatabaseHelper.getContentUri(CurrentWeather.class),
                        null, null, null, null);
            } else if (id == MINUTE_LOADER) {
                return new CursorLoader(getActivity(), DatabaseHelper.getContentUri(MinuteWeather.class),
                        null, null, null, null);
            /*} else if (id == HOUR_LOADER) {
                return new CursorLoader(getActivity(), DatabaseHelper.getContentUri(HourlyWeather.class),
                        null, null, null, null);
                        */
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) return;
            switch(loader.getId()) {
                case MINUTE_LOADER:
                    minuteLoaded(data);
                    break;
                case CURRENT_LOADER:
                    currentLoaded(data);
                    break;
                case HOUR_LOADER:
                    //hourLoaded(data);
                    break;
            }
        }

        private void minuteLoaded(Cursor data) {
            ArrayList<Point> points = new ArrayList<Point>();
            Weather w;
            Point p;
            if (data.moveToFirst()) {
                do {
                    w = new Weather(data);
                    p = new Point();
                    p.x = data.getPosition();
                    p.y = w.getPrecipIntensity();
                    points.add(p);
                } while (data.moveToNext());
            }

            mChart.setPoints(points);
        }

        private void currentLoaded(Cursor data) {
            if (!data.moveToFirst()) return;
            Weather weather = new Weather(data);
            mTemp.setText("" + (int) weather.getTemperature() + (char) 0x00B0);
            mTemp.setTypeface(WeatherApp.getRobotoBold(getActivity()));
            mSummary.setText(weather.getSummary());
            mSummary.setTypeface(WeatherApp.getRoboto(getActivity()));
            mFeels.setText("Feels Like " + (int) weather.getApparentTemperature() + (char) 0x00B0);
            mFeels.setTypeface(WeatherApp.getRoboto(getActivity()));
            mHumidity.setText(((int) (100 * weather.getHumidity()) + "%"));
            mHumidity.setTypeface(WeatherApp.getRobotoBold(getActivity()));
            mWind.setText((int)weather.getWindSpeed() + " mph");
            mWind.setTypeface(WeatherApp.getRobotoBold(getActivity()));
            mPrecip.setText((int) (100*weather.getPrecipIntensity()) + "%");
            mPrecip.setTypeface(WeatherApp.getRobotoBold(getActivity()));

            if (!weather.getPrecipType().equals("")) {
                mPrecipLabel.setText(weather.getPrecipType().toUpperCase() + "");
            }
            mLocation.setText(((WeatherApp) getActivity().getApplication()).getLocation("current_loc"));

            mDrawable = (SkyconsDrawable) IconUtil.getDrawable(weather.getIcon());
            if (mDrawable != null) {
                mIcon.setImageDrawable(mDrawable);
                //mDrawable.start();
            } else {
                mIcon.setImageDrawable(getResources().getDrawable(R.drawable.wind));
            }
        }

        private void hourLoaded(Cursor data) {
            ArrayList<TemperatureGraph.Point> points = new ArrayList<TemperatureGraph.Point>();
            Weather w;
            TemperatureGraph.Point p;
            if (data.moveToFirst()) {
                do {
                    w = new Weather(data);
                    p = new TemperatureGraph.Point();
                    p.x = data.getPosition();
                    p.y = w.getTemperature();
                    points.add(p);
                } while (data.moveToNext());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

}
