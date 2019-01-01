package integrals.inlens.Broadcast_Receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import integrals.inlens.Services.RecentImageService;

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
                context.startService(new Intent(context, RecentImageService.class));


        }


    }



}
