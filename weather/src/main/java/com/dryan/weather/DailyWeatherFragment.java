package com.dryan.weather;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dryan.weather.model.HourlyWeather;
import com.dryan.weather.model.Weather;
import com.dryan.weather.widget.WeatherWidget.ExpandingTemperatureBar;
import com.dryan.weather.widget.WeatherWidget.IconUtil;
import com.dryan.weather.widget.WeatherWidget.SkyconsDrawable;
import com.vokal.database.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Cold-One-City-USA on 2/26/14.
 */
public class DailyWeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @InjectView(R.id.hour_list)
    ListView mListView;

    HourAdapter mAdapter;

    Weather mMaxTemp;
    Weather mMaxFeelTemp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hour, container, false);
        ButterKnife.inject(this, v);
        getLoaderManager().initLoader(0, null, this);
        getActivity().getActionBar().setTitle("48 HOUR FORCAST");
        return v;
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(0,null,this);
        super.onResume();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DatabaseHelper.getContentUri(HourlyWeather.class),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) return;

        Cursor c;
        c = getActivity().getContentResolver().query(DatabaseHelper.getContentUri(HourlyWeather.class),null,null,null,HourlyWeather.TEMP_MAX + " DESC");
        if (!c.moveToFirst()) return;
        mMaxTemp = new Weather(c);

        c = getActivity().getContentResolver().query(DatabaseHelper.getContentUri(HourlyWeather.class),null,null,null,HourlyWeather.APPARENT_TEMP+ " DESC");
        c.moveToFirst();
        mMaxFeelTemp = new Weather(c);

        mAdapter = new HourAdapter(getActivity(), data, false);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class HourAdapter extends CursorAdapter {

        LayoutInflater mInflater;
        Context mContext;
        Date mDate = new Date();

        public HourAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            mInflater = LayoutInflater.from(context);
            mContext = context;
            mDate = new Date();
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mInflater.inflate(R.layout.hour_item,parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Weather w = new Weather(cursor);
            TextView time = (TextView) view.findViewById(R.id.time);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView feels = (TextView) view.findViewById(R.id.feels_label);
            TextView temp = (TextView) view.findViewById(R.id.high_label);
            TextView precipLabel = (TextView) view.findViewById(R.id.precip_label);
            TextView precip = (TextView) view.findViewById(R.id.precip);
            TextView humidLabel = (TextView) view.findViewById(R.id.humid_label);
            TextView humid = (TextView) view.findViewById(R.id.humidity);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            time.setTypeface(WeatherApp.getRoboto(getActivity()));
            date.setTypeface(WeatherApp.getRoboto(getActivity()));
            feels.setTypeface(WeatherApp.getRoboto(getActivity()));
            temp.setTypeface(WeatherApp.getRoboto(getActivity()));
            precipLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
            precip.setTypeface(WeatherApp.getRobotoBold(getActivity()));
            humidLabel.setTypeface(WeatherApp.getRoboto(getActivity()));
            humid.setTypeface(WeatherApp.getRobotoBold(getActivity()));

            precip.setText("" + (int)w.getPrecipProbability()+ "%");
            mDate.setTime(w.getTime()*1000);
            DateFormat format = new SimpleDateFormat("ha");
            String formatted = format.format(mDate);
            time.setText(formatted);

            humid.setText((int)(w.getHumidity()*100) + "%");
            format = new SimpleDateFormat("E");
            formatted = format.format(mDate);
            date.setText(formatted);

            if (!w.getPrecipType().equals("")) {
                precipLabel.setText(w.getPrecipType().toUpperCase() + " : ");
            }

            int high = 0;
            if (mMaxFeelTemp.getApparentTemperature() > mMaxTemp.getTemperature()) {
                high = (int) mMaxFeelTemp.getApparentTemperature();
            } else {
                high = (int) mMaxTemp.getTemperature();
            }


            SkyconsDrawable drawable = (SkyconsDrawable) IconUtil.getDrawable(w.getIcon());
            if (drawable != null) {
                icon.setImageDrawable(drawable);
                drawable.stop();
            } else {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.wind));
            }

            ExpandingTemperatureBar range = (ExpandingTemperatureBar) view.findViewById(R.id.bar);
            range.setMinTemp(0);
            range.setColor(Color.BLACK);
            range.setMaxTemp(high);
            range.setHigh((int)w.getTemperature());
            range.begin();

            ExpandingTemperatureBar feel_range = (ExpandingTemperatureBar) view.findViewById(R.id.feel_bar);
            feel_range.setMinTemp(0);
            feel_range.setColor(0x36000000);
            feel_range.setMaxTemp(high);
            feel_range.setHigh((int) w.getApparentTemperature());
            feel_range.begin();
        }

    }
}
