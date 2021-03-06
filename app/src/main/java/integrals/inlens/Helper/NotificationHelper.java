package integrals.inlens.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
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
            notificationChannel.setSound(null,null);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getNotificationManager().createNotificationChannel(notificationChannel);

        }

        NotificationChannel uploadNotificationChannel= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            uploadNotificationChannel = new NotificationChannel("ID_504","Upload  Notification",NotificationManager.IMPORTANCE_MIN);
            getNotificationManager().createNotificationChannel(uploadNotificationChannel);

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
        Intent upload_intent;
        Intent attach_intent ;
        Intent upload_activity_intent;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            upload_intent = new Intent();
            upload_intent.setAction("ADD_FOR_UPLOAD_INLENS");
            attach_intent = new Intent("ATTACH_ACTIVITY_INLENS");
            upload_activity_intent = new Intent("RECENT_IMAGES_GRID_INLENS");

            upload_intent.setComponent(new ComponentName(getPackageName(),"integrals.inlens.Broadcast_Receivers.NotificationWorks"));
            attach_intent.setComponent(new ComponentName(getPackageName(),"integrals.inlens.Broadcast_Receivers.NotificationWorks"));
            upload_activity_intent.setComponent(new ComponentName(getPackageName(),"integrals.inlens.Broadcast_Receivers.NotificationWorks"));


        }else{

            upload_intent = new Intent("ADD_FOR_UPLOAD_INLENS");
            attach_intent = new Intent("ATTACH_ACTIVITY_INLENS");
            upload_activity_intent = new Intent("RECENT_IMAGES_GRID_INLENS");



        }









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


    public Notification.Builder buildNotificationForUploadData(
            int uploadID,int Record
    ){

        Notification.Builder Uploadbuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uploadbuilder = (Notification.Builder) new Notification.Builder(getApplicationContext(),"ID_504")
                    .setContentTitle("Upload Started")
                    .setContentText("Uploading " + uploadID + "/" + Record)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.inlens_logo_m)
                    .setProgress(100, 0, true);
        }

        return Uploadbuilder;
    }

    public void cancelUploadDataNotification(){
        notificationManager.cancel(672);
        }
    public void cancelNotificationRecentImage(){
        notificationManager.cancel(7907);
    }

}
