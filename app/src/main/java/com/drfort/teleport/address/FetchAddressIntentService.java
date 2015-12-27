package com.drfort.teleport.address;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.drfort.teleport.constants.Constants;
import com.drfort.teleport.wakeupatdestination.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ssres on 12/8/15.
 */
public class FetchAddressIntentService extends IntentService {

    public static final String TAG = "FetchAddressService";
    protected ResultReceiver resultReceiver;

    public FetchAddressIntentService(){
        super("FetchAddressIntentService");

    }
    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        resultReceiver = intent.
                getParcelableExtra(Constants.RECEIVER);
        Location location = intent.
                getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;

        try {
            addresses = geocoder
                    .getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG,errorMessage,ioException);
        } catch (IllegalArgumentException illegalArgumentException){
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + "-> "+
                "Latitude:"+ String.valueOf(location.getLatitude())+
                    ", Longitude:"+String.valueOf(location.getLongitude()),illegalArgumentException);
        }

        if(addresses == null || addresses.size()==0){
            errorMessage = getString(R.string.no_address_found);
            Log.e(TAG, errorMessage);
            deliverResultToReceiver(Constants.RESULT_FAILURE,errorMessage);
        }
        else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i=0; i< address.getMaxAddressLineIndex(); i++){
                Log.d("AddressLine"+i+"--",address.getAddressLine(i));
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG,getString(R.string.address_found));
            deliverResultToReceiver(Constants.RESULT_SUCCESS,
                    TextUtils.join(System.getProperty("line.seperator"),addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }
}
