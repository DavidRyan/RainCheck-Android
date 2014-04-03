package com.dryan.weather;

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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dryan.weather.model.WeeklyWeather;
import com.dryan.weather.widget.WeatherWidget.ExpandingTemperatureRange;
import com.vokal.database.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class WeeklyWeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    WeekAdapter mWeekAdapter;

    @InjectView(R.id.day_list)
    ListView mListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily, container, false);
        ButterKnife.inject(this, v);
        getLoaderManager().initLoader(0, null, this);
        getActivity().getActionBar().setTitle("WEEKLY FORCAST");
        return v;
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(0,null,this);
        super.onResume();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DatabaseHelper.getContentUri(WeeklyWeather.class),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) return;
        Timber.d("count " + data.getCount());

        Cursor c;
        c = getActivity().getContentResolver().query(DatabaseHelper.getContentUri(WeeklyWeather.class),null,null,null, WeeklyWeather.TEMP_MAX + " DESC");
        if (!c.moveToFirst()) return;
        WeeklyWeather max = new WeeklyWeather(c);
        c = getActivity().getContentResolver().query(DatabaseHelper.getContentUri(WeeklyWeather.class),null,null,null, WeeklyWeather.TEMP_MIN + " ASC");
        c.moveToFirst();
        WeeklyWeather min = new WeeklyWeather(c);

        mWeekAdapter = new WeekAdapter(getActivity(), data, false, (int)min.getTemperatureMin(), (int)max.getTemperatureMax());
        mListView.setAdapter(mWeekAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class WeekAdapter extends CursorAdapter {

        LayoutInflater mInflater;
        Context mContext;
        int mMin;
        int mMax;
        Date mDate = new Date();

        public WeekAdapter(Context context, Cursor c, boolean autoRequery, int aMin, int aMax) {
            super(context, c, autoRequery);
            mInflater = LayoutInflater.from(context);
            mMin = aMin;
            mMax = aMax;
            mContext = context;
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mInflater.inflate(R.layout.day_item,parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

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

            WeeklyWeather weather = new WeeklyWeather(cursor);
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
        }

    }
}
