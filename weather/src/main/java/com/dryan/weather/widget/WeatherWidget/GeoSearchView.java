package com.dryan.weather.widget.WeatherWidget;

import android.content.Context;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.util.AttributeSet;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;


import com.dryan.weather.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cold-One-City-USA on 4/10/14.
 */
public class GeoSearchView extends SearchView {

    public interface SearchCallback {
        public String onSearch(String aString);
    }

    public GeoSearchView(Context context) {
        super(context);
        setOnQueryTextListener(listener);
    }

    public GeoSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnQueryTextListener(listener);
    }


    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String text) {
            Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> list = geoCoder.getFromLocationName(text, 6);
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
           //     String[] from = {"text"};
        //        int[] to = {R.id.dropDown};
         //       SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), R.layout.map_search_dropdown, cursor, from, to, 0);
          //      GeoSearchView.this.setSuggestionsAdapter(adapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    };
}
