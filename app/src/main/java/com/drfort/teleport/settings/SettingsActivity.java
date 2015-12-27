package com.drfort.teleport.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.drfort.teleport.db.DbAccessor;
import com.drfort.teleport.wakeupatdestination.R;

/**
 * Created by ssres on 12/25/15.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setActionBar();
        //Display Fragment as main content
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_container, new SettingsFragment())
                .commit();
    }

    protected void setActionBar(){
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);

        settingsToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings_preferences);
            PreferenceManager.setDefaultValues(getActivity().getApplicationContext(),
                    R.xml.settings_preferences, true);
            initSummary(getPreferenceScreen());
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            //Deregister the listener on Stop
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        private void initSummary(Preference p) {
            if (p instanceof PreferenceGroup) {
                PreferenceGroup pGrp = (PreferenceGroup) p;
                for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                    initSummary(pGrp.getPreference(i));
                }
            } else {
                updatePrefSummary(p);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }

        public void updatePrefSummary(Preference preference){
            if(preference instanceof EditTextPreference){
                EditTextPreference textPref = (EditTextPreference) preference;
                if(textPref.getKey().contentEquals(getString(R.string.radius_key))){
                    handleRadiusTextPreference(preference,textPref);
                }
            }

            if(preference instanceof ListPreference){
                ListPreference listPref = (ListPreference) preference;
                if(listPref.getKey().contentEquals(getString(R.string.destination_key))){
                    handleDestinationList(listPref);
                }
            }

            if(preference instanceof RingtonePreference){
                RingtonePreference ringtonePref = (RingtonePreference) preference;
                if(ringtonePref.getKey().contentEquals(getString(R.string.ringtone_search_key))){
                    handleRingtonePreference(ringtonePref);
                }
            }
        }

        private void handleRadiusTextPreference(Preference preference,
                                                EditTextPreference textPref){
            int radiusValue = 0;
            String userInput = textPref.getText().toString();
            if(userInput.length() > 0){
                radiusValue = Integer.parseInt(userInput);
            }
            else{
                radiusValue = 0;
                textPref.setText("0");
            }
            Log.d("RadiusValue", String.valueOf(radiusValue));
            if(radiusValue < 1 || radiusValue > 30)
                preference.setSummary("Please enter valid value for radius(1-30 km)");
            else
                preference.setSummary(userInput + " km");
        }

        private void handleDestinationList(ListPreference listPref){
            String[][] savedLocations = readSavedLocationData();
            String[] savedLocationNames;
            String[] savedLocationLatLong;
            if(savedLocations != null){
                savedLocationNames = new String[savedLocations.length];
                savedLocationLatLong = new String[savedLocations.length];

                for(int count=0; count<savedLocations.length; count++){
                    savedLocationNames[count] = savedLocations[count][0];
                    savedLocationLatLong[count] = savedLocations[count][1]
                            + "," + savedLocations[count][2];
                    Log.d("SavedLocations"+count+"--->",savedLocationNames[count]);
                    Log.d("SavedLatLong"+count+"--->",savedLocationLatLong[count]);
                }
                listPref.setEntries(savedLocationNames);
                listPref.setEntryValues(savedLocationLatLong);

                if(listPref.getEntry() != null){
                    String selectedEntry = (String) listPref.getEntry();
                    listPref.setSummary(selectedEntry);
                }
                else{
                    listPref.setSummary("Please select the destination");
                }
            }
        }

        private String[][] readSavedLocationData(){
            DbAccessor dbAccessorObject = new DbAccessor(getActivity().getApplicationContext());
            dbAccessorObject.insertIntoDb("Home",10.0f,20.0f);
            dbAccessorObject.insertIntoDb("Room",15.0f,25.0f);
            return dbAccessorObject.readAllFromDb();
        }

        private void handleRingtonePreference(RingtonePreference ringtonePref) {
            Uri defaultRingtoneUri = Settings.System.DEFAULT_ALARM_ALERT_URI;

            SharedPreferences getRingtone = PreferenceManager.
                    getDefaultSharedPreferences(getActivity().getBaseContext());
            String ringtonePath = getRingtone.
                    getString(getString(R.string.ringtone_search_key), defaultRingtoneUri.getPath());
            Uri selectedRingtoneUri = Uri.parse(ringtonePath);

            Ringtone selectedRingtone = RingtoneManager.
                    getRingtone(getActivity().getBaseContext(), selectedRingtoneUri);
            String selectedRingtoneName = selectedRingtone.getTitle(getActivity().getBaseContext());

            Log.d("SelectedRingtoneUri",selectedRingtoneUri.getPath());
            Log.d("SelectedRingtoneName",selectedRingtoneName);
            ringtonePref.setSummary(selectedRingtoneName.toString());
        }

    }

}
