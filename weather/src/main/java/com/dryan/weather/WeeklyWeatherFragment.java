package com.dryan.weather;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dryan.weather.model.Weather;
import com.dryan.weather.model.WeeklyWeather;
import com.dryan.weather.service.ForcastService;
import com.dryan.weather.widget.WeatherWidget.ExpandingTemperatureRange;
import com.vokal.database.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;
/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class WeeklyWeatherFragment extends Fragment {

    WeekAdapter mWeekAdapter;
    Subscription subscription;

    @InjectView(R.id.day_list)
    ListView mListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily, container, false);
        ButterKnife.inject(this, v);
        getActivity().getActionBar().setTitle(getActivity().getString(R.string.ab_weekly_title));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }

    ArrayList<Weather> mWeekWeatherList = new ArrayList<Weather>();
    @Override
    public void onResume() {
        super.onResume();
        subscription = AndroidObservable.bindFragment(this, ForcastService.fetchWeatherObservable("", "daily"))
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                    (Weather weather) -> mWeekWeatherList.add(weather),
                    (Throwable e) -> e.printStackTrace(),
                    () -> {
                        ArrayList<Weather> sorted = new ArrayList<Weather>(mWeekWeatherList);
                        Collections.sort(sorted, (Weather lhs, Weather rhs) -> {
                                if (lhs.getTemperatureMax() > rhs.getTemperatureMax()) {
                                    return -1;
                                } else if (lhs.getTemperatureMax() < rhs.getTemperatureMax()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        );
                        double max = sorted.get(0).getTemperatureMax();
                        Collections.sort(sorted, (Weather lhs, Weather rhs) -> {
                                if (lhs.getTemperatureMin() > rhs.getTemperatureMin()) {
                                    return 1;
                                } else if (lhs.getTemperatureMin() < rhs.getTemperatureMin()) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        );
                        double min = sorted.get(0).getTemperatureMin();
                        Timber.d("max= " + max + " min=" + min);
                        mWeekAdapter = new WeekAdapter(getActivity(), mWeekWeatherList, (int) min, (int) max);
                        mListView.setAdapter(mWeekAdapter);
                    }
                );
    }

    private class WeekAdapter extends ArrayAdapter<Weather> {

        LayoutInflater mInflater;
        ArrayList<Weather> data;
        Context mContext;
        int mMin;
        int mMax;
        Date mDate = new Date();

        public WeekAdapter(Context context, ArrayList<Weather> values, int min, int max) {
            super(context, R.layout.day_item, values);
            mContext = context;
            data = values;
            mMax = max;
            mMin = min;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)getActivity()).getLayoutInflater();
            view= inflater.inflate(R.layout.day_item, parent, false);
            ExpandingTemperatureRange range = (ExpandingTemperatureRange) view.findViewById(R.id.expand);
            TextView day = (TextView) view.findViewById(R.id.day);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView precip = (TextView) view.findViewById(R.id.precip);
            TextView precipLabel = (TextView) view.findViewById(R.id.precip_label);
            TextView humid = (TextView) view.findViewById(R.id.humidity);
            TextView humidLabel = (TextView) view.findViewById(R.id.humid_label);

            day.setTypeface(WeatherApp.getRoboto(getActivity()));
            date.setTypeface(WeatherApp.getRoboto(getActivity()));
            precipLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
            humidLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
            precip.setTypeface(WeatherApp.getRobotoBold(getActivity()));
            humid.setTypeface(WeatherApp.getRobotoBold(getActivity()));

            Weather weather =  data.get(position);
            mDate = new Date(weather.getTime()*1000);
            if (!weather.getPrecipType().equals("")) {
                precipLabel.setText(weather.getPrecipType().toUpperCase() + " : ");
            }
            precip.setText((int)weather.getPrecipProbability() + "% ");
            humid.setText((int)(weather.getHumidity()*100) + "%");
            DateFormat format = new SimpleDateFormat("E");
            String formatted = format.format(mDate);
            day.setText("" +formatted.toUpperCase());
            format = new SimpleDateFormat("MMM d");
            date.setText("" + format.format(mDate));
            range.setMinTemp(mMin);
            range.setMaxTemp(mMax);
            range.setHighAndLow((int) weather.getTemperatureMax(), (int) weather.getTemperatureMin());
            return view;
        }
    }
}
