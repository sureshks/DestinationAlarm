package com.drfort.teleport.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.drfort.teleport.constants.Constants;

import java.util.Calendar;

/**
 * Created by ssres on 12/7/15.
 */
public class AlarmTrigger {
    private Context context;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public AlarmTrigger(Context context){
        this.context = context;
    }

    public void triggerAlarmNow(){
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,0,alarmReceiverIntent,0);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, Constants.ALARM_DELAY);
        Log.d("--Alarm Time:",String.valueOf(cal.getTimeInMillis()));
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), pendingIntent);
    }
}
