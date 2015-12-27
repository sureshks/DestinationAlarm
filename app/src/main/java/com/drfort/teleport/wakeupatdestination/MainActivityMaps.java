package com.drfort.teleport.wakeupatdestination;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.drfort.teleport.address.AddressResultReceiver;
import com.drfort.teleport.address.FetchAddressIntentService;
import com.drfort.teleport.constants.Constants;
import com.drfort.teleport.geofence.GeofenceTrigger;
import com.drfort.teleport.maps.GoogleMapsHelper;
import com.drfort.teleport.settings.SettingsActivity;
import com.drfort.teleport.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;

/**
 * Created by ssres on 12/1/15.
 */
public class MainActivityMaps extends AppCompatActivity
    {

        public static GoogleMap mMap;
        public static GoogleApiClient googleApiClient;
        public static Circle circle;
        public static Location location;
        public static GoogleMapsHelper mapsHelper;
        private AddressResultReceiver addressResultReceiver =
                new AddressResultReceiver(new Handler(),MainActivityMaps.this);
        private GeofenceTrigger geofenceTrigger;

     protected static Location setLocation(){
         Location loc = new Location("");
         loc.setLatitude(12.1260d);
         loc.setLongitude(78.1540d);
         return loc;
     }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        setActionBar();
        getMapFragment();

        mapsHelper = new GoogleMapsHelper(MainActivityMaps.this, mMap);
        googleApiClient = mapsHelper.getGoogleClient();
    }

        protected void setActionBar(){
            Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
            mainToolbar.setTitle("WakeMeUp");
            setSupportActionBar(mainToolbar);
        }

        protected void getMapFragment(){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;

    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.wru_action:
                    if(Utils.isGoogleApiConnected(googleApiClient)) {
                        geofenceTrigger = new GeofenceTrigger(this,
                                googleApiClient, MainActivityMaps.location);
                        geofenceTrigger.addGeofence();
                    }
                    return true;
                case R.id.settings_action:
                    Intent settingsIntent = new Intent(this,SettingsActivity.class);
                    this.startActivity(settingsIntent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        protected void onStart() {
            Log.d("---OnStart", "Started");
            super.onStart();
            googleApiClient.connect();
        }

        @Override
        protected void onStop() {
            Log.d("---OnStop", "Stopped");
            if(googleApiClient.isConnected())
                googleApiClient.disconnect();
            super.onStop();
        }

        protected void startIntentService(){
            Intent intent = new Intent(this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER,addressResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
            startService(intent);
        }
}
