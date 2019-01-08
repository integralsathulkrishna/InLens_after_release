package integrals.inlens.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import integrals.inlens.GridView.MainActivity;
import integrals.inlens.R;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);
        createNotificationChannels();

    }

    private void createNotificationChannels() {
        NotificationChannel notificationChannel= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("ID_503","Recent Image Notification",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getNotificationManager().createNotificationChannel(notificationChannel);

        }

    }

    public NotificationManager getNotificationManager() {
     if(notificationManager==null)
         notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

     return notificationManager;
    }


    public Notification.Builder buildNotificationForRecentImage(
            RemoteViews remoteViews,
            Bitmap LogoBitMap
    ){
        Intent upload_intent = new Intent("ADD_FOR_UPLOAD_INLENS");
        Intent attach_intent = new Intent("ATTACH_ACTIVITY_INLENS");
        Intent upload_activity_intent = new Intent("RECENT_IMAGES_GRID_INLENS");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 9388, upload_intent, 0);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(getApplicationContext(), 1428, upload_activity_intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.AddForUpload, pendingIntent1);
        remoteViews.setOnClickPendingIntent(R.id.GotoUploadActivity, pendingIntent3);


        Notification.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = (Notification.Builder)
                        new Notification.Builder(getApplicationContext(), "ID_503")
                                .setContentTitle("New image detected")
                                .setContentText("Inlens has detected a new image. Expand to get more info.")
                                .setOnlyAlertOnce(true)
                                .setCustomBigContentView(remoteViews)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.inlens_logo_m)
                                .setLargeIcon(LogoBitMap);

                builder.setContentIntent(pendingIntent);
            }

        return builder;
    }

}
