package integrals.inlens.Broadcast_Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import integrals.inlens.Activities.CreateCloudAlbum;
import integrals.inlens.MainActivity;
import integrals.inlens.Services.RecentImageService;
import static android.content.Context.MODE_PRIVATE;

public class RestartRecentImageService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("UsingCommunity::", false)) {
            context.startService(new Intent(context,RecentImageService.class));
        }
    }
}
