package integrals.inlens.Broadcast_Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import integrals.inlens.Services.RecentImageService;

public class RestartRecentImageService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,RecentImageService.class));
    }
}
