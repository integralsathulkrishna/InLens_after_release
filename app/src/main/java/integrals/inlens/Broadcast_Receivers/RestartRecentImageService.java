package integrals.inlens.Broadcast_Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import integrals.inlens.Services.OreoService;
import integrals.inlens.Services.RecentImageService;
import static android.content.Context.MODE_PRIVATE;

public class RestartRecentImageService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("UsingCommunity::", false)) {
         try {
             startService(context,new Intent(context,RecentImageService.class));
             }catch (IllegalStateException e){
             e.printStackTrace();
            }
          }
    }


    public void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, OreoService.class);
            serviceIntent.putExtra("inputExtra", "Ongoing InLens Recent-Image service.");
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        else
        {
            context.startService(intent);
        }
    }


}

