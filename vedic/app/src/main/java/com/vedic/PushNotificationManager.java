package com.vedic;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class PushNotificationManager extends FirebaseMessagingService {

    public PushNotificationManager() {
     
    }
    /**
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String token) {

        Log.d("vedictoken","Refreshed token: " +token);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.d("payload", "data message received with payload: " + remoteMessage.getData());
            Map<String,String> dataA = remoteMessage.getData();
            for (String key : dataA.keySet()) {
                String value = dataA.get(key);
                Log.e("payloadallkeys", "  " + key + " : " + value);
            }
            Log.e("payloadtitleandbody", dataA.get("title") + ": " + dataA.get("body"));

        }

        if (remoteMessage.getNotification() != null) {
              Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
    }

     public void showNotification(String title,String message) {
            String channel_id = "notification_channel";
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(),channel_id)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)

            .setVibrate(new long[]{1000, 1000, 1000,
                    1000, 1000})

            .setOnlyAlertOnce(true).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle()
                 .bigText(message))
            .setContentIntent(pendingIntent);
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel= new NotificationChannel(channel_id, "vedic",NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            int oneTimeID = (int) SystemClock.uptimeMillis();
            notificationManager.notify(oneTimeID, builder.build());
            }
}