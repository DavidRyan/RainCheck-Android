package com.dryan.weather.widget.WeatherWidget;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;


import com.dryan.weather.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Cold-One-City-USA on 4/10/14.
 */
public class GeoSearchView extends SearchView {

    private SearchCallback mCallbacks;

    public interface SearchCallback {
        public void onSearch(String aString);
    }

    public GeoSearchView(Context context) {
        super(context);
        SearchManager searchManager = (SearchManager) getContext().getSystemService(context.SEARCH_SERVICE);
        setSearchableInfo(searchManager.getSearchableInfo(((Activity) context).getComponentName()));
        setOnSuggestionListener(suggestionListener);
        setOnQueryTextListener(listener);
    }

    public GeoSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SearchManager searchManager = (SearchManager) getContext().getSystemService(context.SEARCH_SERVICE);
        setSearchableInfo(searchManager.getSearchableInfo(((Activity) context).getComponentName()));
        setOnSuggestionListener(suggestionListener);
        setOnQueryTextListener(listener);
    }

    public void setCallback(SearchCallback mCallbacks) {
        this.mCallbacks = mCallbacks;
    }

    private List<Address> mList;

    OnSuggestionListener suggestionListener = new OnSuggestionListener() {
        @Override
        public boolean onSuggestionSelect(int position) {
            mCallbacks.onSearch(mList.get(position).getAddressLine(0));
            clearFocus();
            return true;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            mCallbacks.onSearch(mList.get(position).getAddressLine(0));
            clearFocus();
            return true;
        }
    };

    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {

        AsyncTask<String, Void, List<Address>> mTask;

        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String text) {
            final Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
            if (mTask != null) {
                mTask.cancel(true);
            }
             mTask = new AsyncTask<String, Void, List<Address>>() {
                @Override
                protected List<Address> doInBackground(String... params) {
                    try {
                        mList = geoCoder.getFromLocationName("40 n State", 6);
                        return mList;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                 @Override
                 protected void onPostExecute(List<Address> list) {
                     super.onPostExecute(list);
                     String[] columnNames = {"_id","text"};
                     MatrixCursor cursor = new MatrixCursor(columnNames);
                     String[] array = new String[list.size()];
                     for(int i = 0; i < list.size(); i++) {
                         array[i] = list.get(i).getAddressLine(0).toString() + ", " + list.get(i).getAddressLine(1).toString();

                     }
                     String[] temp = new String[2];
                     int id = 0;
                     for(String item : array) {
                         temp[0] = Integer.toString(id++);
                         temp[1] = item;
                         cursor.addRow(temp);
                     }
                     String[] from = {"text"};
                     int[] to = {R.id.dropDown};
                     SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), R.layout.map_search_dropdown, cursor, from, to, 0);
                     GeoSearchView.this.setSuggestionsAdapter(adapter);
                 }
             };
            mTask.execute(text);
            return true;
        }
    };
}
