package integrals.inlens.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import integrals.inlens.MainActivity;
import integrals.inlens.R;

public class SituationNotyService extends Service {

    DatabaseReference ComNotyRef;
    String MyUserID;
    NotificationCompat.Builder noty  ;
    static int notyid =808679;

    public SituationNotyService() {
    }

    public SituationNotyService(Context applicationContext) {
        super();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            MyUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ComNotyRef = FirebaseDatabase.getInstance().getReference().child("ComNoty").child(MyUserID);
        }

        noty = new NotificationCompat.Builder(this);
        noty.setAutoCancel(true);
        noty.setSmallIcon(R.mipmap.ic_launcher);
        noty.setContentTitle("New Situation Detected");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("code",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        noty.setContentIntent(pendingIntent);
        noty.setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/impulse"));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ComNotyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {   try {
                    String notificationid = snapshot.getKey();
                    String sitname = snapshot.child("name").getValue().toString();
                    String ownername = snapshot.child("ownername").getValue().toString();
                    noty.setContentText(ownername+" has created a new situation, "+sitname+". Check it out now.");
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(notyid,noty.build());
                    ComNotyRef.child(notificationid).removeValue();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }
}
