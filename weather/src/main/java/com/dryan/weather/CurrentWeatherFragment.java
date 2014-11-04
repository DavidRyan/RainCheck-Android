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
import com.dryan.weather.service.ForcastService;
import com.dryan.weather.widget.WeatherWidget.IconUtil;
import com.dryan.weather.widget.WeatherWidget.PrecipitationGraph.Point;
import com.dryan.weather.widget.WeatherWidget.PrecipitationGraph;
import com.dryan.weather.widget.WeatherWidget.SkyconsDrawable;
import com.dryan.weather.widget.WeatherWidget.TemperatureGraph;
import com.vokal.database.DatabaseHelper;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractCollection;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class CurrentWeatherFragment extends Fragment {

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

    private SkyconsDrawable mDrawable;

    Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, v);
        mHumidityLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
        mGraphLabel.setTypeface(WeatherApp.getRobotoBold(getActivity()));
        mWindLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
        mPrecipLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
        mLocation.setTypeface(WeatherApp.getRoboto(getActivity()));
        mPowered.setTypeface(WeatherApp.getRoboto(getActivity()));
        getActivity().getActionBar().setTitle(getActivity().getString(R.string.ab_current_title));
        return v;
    }

                    int count = 0;
                        public ArrayList<Point> points = new ArrayList<Point>();
    @Override
    public void onResume() {
        subscription = AndroidObservable.bindFragment(this, ForcastService.fetchWeatherObservable("", "minutely"))
                .subscribeOn(Schedulers.newThread())
                .map(weather -> {
                        Timber.d("point");
                        Point p = new Point();
                        p.x = ++count;
                        p.y = 0.2;//weather.getPrecipIntensity();
                        return p;
                }).subscribe(
                    point -> points.add(point),
                    e -> e.printStackTrace(),
                    () -> {
                        Timber.d("Complete");
                        mChart.setPoints(points);
                        ForcastService.fetchWeatherObservable("","currently").subscribe(
                            (Weather weather) -> {
                                Timber.d("Here.....");
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
                            },
                            (Throwable e) -> Timber.d("Current", e)
                            );
                    }
                    );

        super.onResume();
    }

    Subscriber<Weather> mCurrentSubscriber = new Subscriber<Weather>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.d("Current", e);
            }

            @Override
            public void onNext(Weather weather) {
                Timber.d("Here.....");
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
        };

    @Override
    public void onPause() {
        super.onPause();
        if (mDrawable != null) mDrawable.stop();
        subscription.unsubscribe();
    }


}
