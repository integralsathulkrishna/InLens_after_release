package integrals.inlens.Broadcast_Receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import integrals.inlens.Services.RecentImageService;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Athul Krishna on 14/03/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private Context MyContext;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.MyContext=context;
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable() || (!wifi.isAvailable()) || (!mobile.isAvailable())) {
            Boolean Default = false;
            SharedPreferences sharedPreferences = context.getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
            if (sharedPreferences.getBoolean("UsingCommunity::", Default) == true) {
                RecentImageService recentImageService;
                recentImageService = new RecentImageService(MyContext);
                if (!isMyServiceRunning(recentImageService.getClass())) {
                    context.startService(new Intent(context, RecentImageService.class));
                }


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
