package integrals.inlens.Broadcast_Receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import integrals.inlens.Services.RecentImageService;
import integrals.inlens.Services.SituationNotyService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Athul Krishna on 13/03/2018.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private Context MyContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.MyContext=context;
        Boolean Default = false;
        SharedPreferences sharedPreferences = context.getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("UsingCommunity::", Default) == true) {
            RecentImageService recentImageService;
            recentImageService = new RecentImageService(MyContext);
            if (!isMyServiceRunning(recentImageService.getClass())) {
                context.startService(new Intent(context, RecentImageService.class));
                context.startService(new Intent(context, SituationNotyService.class));
              }


        }


    }

    //Created By Elson
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MyContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }

        return false;
    }



}
