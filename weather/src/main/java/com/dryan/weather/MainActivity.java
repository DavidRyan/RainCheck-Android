package com.dryan.weather;

import android.app.Activity;
;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toast;

import com.dryan.weather.event.NetworkEvent;
import com.dryan.weather.service.WeatherService;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    public static final String PREF_CURRENT_LOCATION = "current_loc";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private LocationClient mLocationClient;
    private CurrentWeatherFragment mCurrent = new CurrentWeatherFragment();
    private WeeklyWeatherFragment mWeekly = new WeeklyWeatherFragment();
    private DailyWeatherFragment mDaily = new DailyWeatherFragment();
    private Location mLocation;

    @Inject
    WeatherService mWeatherService;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        WeatherApp.inject(this);
        mLocationClient = new LocationClient(this, this, this);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        getFragmentManager().beginTransaction().replace(R.id.container, mCurrent).commit();

        getActionBar().setTitle("" + getString(R.string.app_name));
    }

    @Override
    protected void onStart() {
        super.onStart();
        WeatherApp.BUS.register(this);
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        WeatherApp.BUS.unregister(this);
        mLocationClient.disconnect();
    }

    @Subscribe
    public void onNetwork(NetworkEvent event) {
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        if (!event.success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                getFragmentManager().beginTransaction().replace(R.id.container, mCurrent).commit();
                break;
            case 1:
                 getFragmentManager().beginTransaction().replace(R.id.container, mDaily).commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().replace(R.id.container, mWeekly).commit();
                break;
        }
    }

    private void search(double aLat, double aLong) {
        Timber.d("refreshing data..");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProgressBarVisibility(true);
                setProgressBarIndeterminateVisibility(true);
                setProgressBarIndeterminate(true);
            }
        });
        // mWeatherService.fetchWeatherWeather(35.2229,-80.8380);
        mWeatherService.fetchWeatherWeather(aLat, aLong);
    }

    private void search(String aName) {
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = geoCoder.getFromLocationName(aName, 1);
            ((WeatherApp)getApplication()).setLocation(PREF_CURRENT_LOCATION, addressList.get(0).getAddressLine(0));
            search(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Runnable mRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLocationClient.isConnected()) {
                Timber.d("refreshing data..");
                mLocation = mLocationClient.getLastLocation();
            }
            if (mLocation == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Cannot find location.", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            search(mLocation.getLatitude(), mLocation.getLongitude());
            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addressList = geoCoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                ((WeatherApp)getApplication()).setLocation(PREF_CURRENT_LOCATION, addressList.get(0).getAddressLine(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void refreshData() {
        new Thread(mRefreshRunnable).start();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final MenuItem search = menu.findItem(R.id.search);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                search.collapseActionView();
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return true;
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {

        refreshData();
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public void setAlarm() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive( Context context, Intent intent ) {
                mWeatherService.fetchWeatherWeather(mLocation.getLatitude(), mLocation.getLongitude());
            }
        };

      //  this.registerReceiver( receiver, new IntentFilter("com.dryan.weather.update") );

        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("com.dryan.weather.update"), 0 );
        AlarmManager manager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
       // manager.setRepeating( AlarmManager.ELAPSED_REALTIME , SystemClock.elapsedRealtime() + 1000*60*1, 1000*60*60*4, pendingIntent);
    }

}

