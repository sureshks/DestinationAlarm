package com.drfort.teleport.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.drfort.teleport.wakeupatdestination.MainActivityMaps;
import com.drfort.teleport.wakeupatdestination.R;

/**
 * Created by ssres on 12/7/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("----AlarmReceived", "true");
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        //Ringtone alarmTone = RingtoneManager.getRingtone(context, alarmUri);
        //alarmTone.play();

        Intent notificationIntent = new Intent(context, MainActivityMaps.class);

        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivityMaps.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);*/

        PendingIntent pendingIntent = PendingIntent.
                getActivity(context, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_play_light)
                .setContentTitle("Alarm")
                .setContentText("Please Wake Up: You are close to destination")
                .setSound(alarmUri, RingtoneManager.TYPE_ALARM)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags |= notification.FLAG_INSISTENT;

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock screenOn = powerManager.
                newWakeLock(powerManager.SCREEN_DIM_WAKE_LOCK | powerManager.ACQUIRE_CAUSES_WAKEUP,"Destination Alarm");
        screenOn.acquire();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        screenOn.release();
    }
}
