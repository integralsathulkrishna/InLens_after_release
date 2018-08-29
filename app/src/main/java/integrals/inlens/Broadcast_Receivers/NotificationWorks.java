package integrals.inlens.Broadcast_Receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import integrals.inlens.Activities.AttachSituation;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.MainActivity;

/**
 * Created by Athul Krishna on 03/02/2018.
 */

public  class NotificationWorks extends BroadcastReceiver {
    private  static  String UPLOAD_IMAGE_URL="NULLX";
    private  static  Boolean OngoingNotification=true;
    private  String  UPLOAD_BASKET_INTENT=  "ADD_FOR_UPLOAD_INLENS";
    private  String  UPLOAD_TASK_INTENT=    "ATTACH_ACTIVITY_INLENS";
    private  String  UPLOAD_ACTIVITY_INTENT="RECENT_IMAGES_GRID_INLENS";
    private  Intent  UploadTaskIntent,UploadActivityIntent;
    private  String  DatabaseImageUri="NULLX";
    private  String  DatabaseWeatherDetails="NULLX";
    private  String  DatabaseLocationDetails="NULLX";
    private  String  DatabaseAudioCaptionUri="NULLX";
    private  String  DatabaseTimeTaken="NULLX";
    private  String  DatabaseUploadStatus="NULLX";
    private  String  DatabaseTextCaption="NULLX";
    private  String  DatabaseUploaderID="NULLX";
    private  String  DatabaseUploaderName="NULLX";
    private  String  DatabaseUploaderProfilePic="NULLX";
    private  String  DatabaseCurrentTimeMillis="NULLX";
    private  Context context;
    private  String  TIME_TAKEN;
    public NotificationWorks() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UploadTaskIntent=new Intent(context.getApplicationContext(),AttachSituation.class);
        UploadTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context=context;
        UploadActivityIntent=new Intent(context.getApplicationContext(),MainActivity.class);
        UploadActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        RecentImageDatabase recentImageDatabase= new RecentImageDatabase(context,"",null,1);
        UPLOAD_IMAGE_URL=recentImageDatabase.GetPhotoUri(recentImageDatabase.GetNumberOfRows());
        TIME_TAKEN=recentImageDatabase.GetTimeTaken(recentImageDatabase.GetNumberOfRows());
        if(intent.getAction().equals(UPLOAD_TASK_INTENT)) {
            cancelNotification();
            context.startActivity(UploadTaskIntent);
        }
        else if (intent.getAction().equals(UPLOAD_ACTIVITY_INTENT)){
            cancelNotification();
            context.startActivity(UploadActivityIntent);
        }
        else if(intent.getAction().equals(UPLOAD_BASKET_INTENT)){
            cancelNotification();
            UploadDatabaseHelper  uploadDatabaseHelper= new UploadDatabaseHelper(context,"",null,1);
            CreateDatabaseValues();
            uploadDatabaseHelper.InsertUploadValues(
                    DatabaseImageUri ,
                    DatabaseWeatherDetails,
                    DatabaseLocationDetails,
                    "NULL",
                    DatabaseTimeTaken,
                    DatabaseUploadStatus,
                    DatabaseTextCaption,
                    DatabaseUploaderID,
                    DatabaseUploaderName,
                    DatabaseUploaderProfilePic,
                    DatabaseCurrentTimeMillis


            );

        }
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        SharedPreferences sharedPreferences3= context.getSharedPreferences("Notificationclick.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3=sharedPreferences3.edit();
        editor3.putBoolean("ClickIndex:",true);
        editor3.commit();
    }

    private void CreateDatabaseValues() {
        String Default="SS";
        DatabaseImageUri=UPLOAD_IMAGE_URL;
        DatabaseCurrentTimeMillis= String.valueOf(System.currentTimeMillis());
        DatabaseUploaderID="";
        DatabaseUploaderName="";
        DatabaseUploaderProfilePic="";
        DatabaseUploadStatus="NOT_UPLOADED";
        Calendar calendar=Calendar.getInstance();
        DatabaseTimeTaken =TIME_TAKEN;
        CurrentDatabase currentDatabase= new CurrentDatabase(context,"",null,1);
        int Value=currentDatabase.GetUploadingTotal();
        currentDatabase.ResetUploadTotal((Value+1));
        currentDatabase.close();

    }
}
