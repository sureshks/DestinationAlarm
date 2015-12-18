package com.drfort.teleport.wakeupatdestination;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ssres on 12/8/15.
 */
public class AddressResultReceiver extends ResultReceiver {

    public Context context;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }
    public AddressResultReceiver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        String addressOutput="";
        addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        Toast toast = Toast.makeText(context,addressOutput,Toast.LENGTH_LONG);
        toast.show();

        if(resultCode == Constants.RESULT_SUCCESS){
            Log.d("AddressResultReceiver:","Successful address");
            Log.d("Address:", addressOutput);
        }
        else
            Log.d("AddressResultReceiver:","No Address");
    }
}
