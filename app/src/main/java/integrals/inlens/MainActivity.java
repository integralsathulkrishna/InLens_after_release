package integrals.inlens;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import integrals.inlens.Activities.CloudAlbum;
import integrals.inlens.Activities.CreateCloudAlbum;
import integrals.inlens.Activities.LoginActivity;
import integrals.inlens.Activities.QRCodeReader;
import integrals.inlens.Activities.SettingActivity;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.InLensJobScheduler.InLensJobScheduler;
import integrals.inlens.Models.AlbumModel;
import integrals.inlens.Services.RecentImageService;
import integrals.inlens.Services.SituationNotyService;
import integrals.inlens.ViewHolder.AlbumViewHolder;


public class MainActivity extends AppCompatActivity {
    private RecyclerView MemoryRecyclerView;
    private DatabaseReference InDatabaseReference;
    private String CommunityPostKey;
    private ComponentName componentName;
    private String CurrentUser;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth InAuthentication;
    private FirebaseUser firebaseUser;
    private DatabaseReference participantDatabaseReference,
            getParticipantDatabaseReference;
    private String CommunityID;
    private Intent intent;
    private static final int JOB_ID=7907;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;

    //
    //
    // Import from Elson.............................................................................
    //1.Service Running  Continuation
    //2.Progress Bar for Profile Pic Upload
    //3.Date of Completion date picker on the CREATE CLOUD ALBUM
    //4.Add Situation

    //Update for Elson
    // Remove Unncessary code for Invitaitoon Database in
    //Cloud-Album
    //Situation Adapter
    //Setting Activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(25);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.CreateCloudAlbumFloat);



        ComponentName componentName= new ComponentName(this, InLensJobScheduler.class);
        JobInfo.Builder builder= new JobInfo.Builder(JOB_ID,componentName);
        builder.setPeriodic(15*60*1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        jobInfo=builder.build();
        jobScheduler=(JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        //1.Service Running Continuation
        RecentImageService recentImageService;
        recentImageService = new RecentImageService(getApplicationContext());
        if (!isMyServiceRunning(recentImageService.getClass()) && firebaseUser!=null) {
            startService(new Intent(getApplicationContext(), RecentImageService.class));
           }

           
        SituationNotyService situationNotyService;
        situationNotyService = new SituationNotyService(getApplicationContext());
        if (!isMyServiceRunning(situationNotyService.getClass()) && firebaseUser!=null) {
            startService(new Intent(getApplicationContext(), SituationNotyService.class));
        }


        //User Authentication
        InAuthentication= FirebaseAuth.getInstance();
        firebaseUser=InAuthentication.getCurrentUser();
        try{
            if(firebaseUser==null){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                }
                 else {
                CurrentUser = firebaseUser.getUid();
                      }

         }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        // Float Button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("UsingCommunity::",false) == true) {
                    Toast.makeText(getApplicationContext(),"Sorry.You can't create a new Cloud-Album before you quit the current one.",Toast.LENGTH_LONG).show();
                }
                else{
                    startActivity(new Intent(MainActivity.this, CreateCloudAlbum.class));
                    }
            }
        });

        //Participant Database Reference
        participantDatabaseReference=FirebaseDatabase.getInstance().getReference();
        //Setting Recycler View
        MemoryRecyclerView=(RecyclerView)findViewById(R.id.CloudAlbumRecyclerView);
        MemoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        MemoryRecyclerView.setLayoutManager(linearLayoutManager);

    }
    // Added By Elson
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }

        return false;
    }




    @Override
    protected void onStart() {
        super.onStart();
        // Downloading Recycler View
        try {
            InDatabaseReference=
                            FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Users")
                            .child(CurrentUser)
                            .child("Communities");

            final FirebaseRecyclerAdapter<AlbumModel,AlbumViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AlbumModel, AlbumViewHolder>(
                    AlbumModel.class,
                    R.layout.cloud_album_card,
                    AlbumViewHolder.class,
                    InDatabaseReference

            ) {
                @Override
                protected void populateViewHolder(final AlbumViewHolder viewHolder, final AlbumModel model, final int position) {
                    viewHolder.SetAlbumCover(getApplicationContext(),model.getAlbumCoverImage());
                    viewHolder.SetTitle(model.getAlbumTitle());
                    viewHolder.SetProfilePic(getApplicationContext(),model.getPostedByProfilePic());
                    viewHolder.SetAlbumDescription(model.getAlbumDescription());
                            try {
                                InDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        CommunityID = dataSnapshot.child(getRef(position).getKey().toString()).child("CommunityID").getValue().toString().trim();
                                        getParticipantDatabaseReference=participantDatabaseReference.child("Communities").child(CommunityID).child("CommunityPhotographer");
                                        viewHolder.SetParticipants(getApplicationContext(),getParticipantDatabaseReference);
                                      }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }catch (NullPointerException e) {
                            }
                            viewHolder.ShareButton.setOnClickListener(new View.OnClickListener() {
                            final String PostKeyS=getRef(position).getKey().toString().trim();


                        @Override
                        public void onClick(View v) {

                            final Intent SharingIntent = new Intent(Intent.ACTION_SEND);
                            SharingIntent.setType("text/plain");
                            InDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CommunityPostKey=dataSnapshot.child(PostKeyS).child("CommunityID").getValue().toString().trim();
                                    SharingIntent.putExtra(Intent.EXTRA_TEXT,"https://inlens.in/watch/"+CommunityPostKey);
                                    startActivity(SharingIntent);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });






                        }
                    });
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String PostKey=getRef(position).getKey().toString().trim();

                            InDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CommunityPostKey=dataSnapshot.child(PostKey).child("CommunityID").getValue().toString().trim();
                                    startActivity(new Intent(MainActivity.this,CloudAlbum.class)
                                            .putExtra("AlbumName",model.getAlbumTitle())
                                            .putExtra("GlobalID::",CommunityPostKey));

                                    }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                 }



            };

            MemoryRecyclerView.setAdapter(firebaseRecyclerAdapter);

        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (DatabaseException e){
            e.printStackTrace();
        }
        try {
            InDatabaseReference.keepSynced(true);

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Add Participant")
                .setIcon(R.drawable.ic_read_qr)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,1,0,"Quit Cloud-Album");
        menu.add(0,2,0,"Upload Activity")
        .setIcon(R.drawable.ic_recent_images_option)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,3,0,"Profile picture");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==0){
            SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
            if (sharedPreferences.getBoolean("UsingCommunity::",false) == true) {
                Toast.makeText(getApplicationContext(),"Sorry,You can't scan a new Cloud-Album before you quit the current one.",Toast.LENGTH_LONG).show();
            }else

            {
                startActivity(new Intent(MainActivity.this, QRCodeReader.class));

            }
        }
        if(item.getItemId()==1){

                AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Quit Cloud-Album");
                builder.setMessage("Are you sure you want to quit the current community");

                builder.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
                        currentDatabase.DeleteDatabase();
                        RecentImageDatabase recentImageDatabase=new RecentImageDatabase(getApplicationContext(),"",null,1);
                        recentImageDatabase.DeleteDatabase();
                        UploadDatabaseHelper uploadDatabaseHelper= new UploadDatabaseHelper(getApplicationContext(),"",null,1);
                        uploadDatabaseHelper.DeleteDatabase();
                        SharedPreferences sharedPreferencesC=getSharedPreferences("InCommunity.pref",MODE_PRIVATE);
                        SharedPreferences.Editor editorC=sharedPreferencesC.edit();
                        editorC.putBoolean("UsingCommunity::",false);
                        editorC.commit();
                        stopService(new Intent(MainActivity.this, RecentImageService.class));
                        JobScheduler jobScheduler=(JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                        jobScheduler.cancel(7907);
                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        Toast.makeText(getApplicationContext(),"Successfully left from the current community",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
                return true;

           }
        else if(item.getItemId()==2){
            startActivity(new Intent(MainActivity.this, integrals.inlens.GridView.MainActivity.class));
        }
        else if(item.getItemId()==3){
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
            return true;
    }



    @Override
    protected void onResume() {
     super.onResume();
     intent = getIntent();

    if ((intent != null) && (intent.getData() != null)) {
        String Data = intent.getDataString().toString();
        String str = Data.substring(18,23);
             if(str.contentEquals("joins")){
                 Toast.makeText(getApplicationContext(),"Join "+Data.substring(24),Toast.LENGTH_SHORT).show();
                  SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
            if (sharedPreferences.getBoolean("UsingCommunity::", false) == true) {
                Toast.makeText(getApplicationContext(),"Sorry.You can't join to a new Cloud-Album, " +
                        "before you quit the current one.",Toast.LENGTH_SHORT)
                        .show();
            }else {
                AddToCloud(Data.substring(24));
                  }

        }
    }
}

    private void AddToCloud(String substring) {

             final DatabaseReference CommunityPhotographer;
             FirebaseAuth CommunityPhotographerAuthentication;
             final String UserID;
             final DatabaseReference UserData;
             final String CommunityID =substring;
             final DatabaseReference[] databaseReference = new DatabaseReference[1];
             final DatabaseReference databaseReference2;
             CommunityPhotographerAuthentication = FirebaseAuth.getInstance();
            UserData = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(CommunityPhotographerAuthentication.getCurrentUser()
                            .getUid());
            UserID = CommunityPhotographerAuthentication.getCurrentUser().getUid();
            databaseReference[0] = FirebaseDatabase.getInstance().getReference();
            CommunityPhotographer = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Communities")
                    .child(CommunityID)
                    .child("CommunityPhotographer");
            databaseReference2=FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("Communities")
                    .child(CommunityID);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Join");
            builder.setMessage("Join this Cloud-Album. Proceed joining it ?");
            builder.setPositiveButton(" YES ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                        CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
                        currentDatabase.InsertUploadValues(CommunityID,0,1,0);
                        currentDatabase.close();


                        final DatabaseReference NewPhotographer = CommunityPhotographer.push();
                        UserData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                NewPhotographer.child("Photographer_UID").setValue(UserID);
                                NewPhotographer.child("Name").setValue(dataSnapshot.child("Name").getValue());
                                NewPhotographer.child("Profile_picture").setValue(dataSnapshot.child("Profile_picture").getValue());
                                NewPhotographer.child("Email_ID").setValue(dataSnapshot.child("Email").getValue());

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        databaseReference[0] = databaseReference[0].child("Users").child(UserID).child("Communities");
                        final DatabaseReference AddingAlbumToReference= databaseReference[0].push();
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                AddingAlbumToReference.child("AlbumTitle").setValue(dataSnapshot.child("AlbumTitle").getValue().toString());
                                AddingAlbumToReference.child("AlbumDescription").setValue(dataSnapshot.child("AlbumDescription").getValue().toString());
                                AddingAlbumToReference.child("AlbumCoverImage").setValue(dataSnapshot.child("AlbumCoverImage").getValue().toString());
                                AddingAlbumToReference.child("User_ID").setValue(dataSnapshot.child("User_ID").getValue().toString());
                                AddingAlbumToReference.child("PostedByProfilePic").setValue(dataSnapshot.child("PostedByProfilePic").getValue().toString());
                                AddingAlbumToReference.child("UserName").setValue(dataSnapshot.child("UserName").getValue().toString());
                                AddingAlbumToReference.child("Time").setValue(dataSnapshot.child("Time").getValue().toString());
                                AddingAlbumToReference.child("CommunityID").setValue(CommunityID);
                                StartServices();
                                setIntent(null);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),"Sorry network error...please try again",Toast.LENGTH_SHORT).show();
                            }
                        });





                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }

                private void StartServices() {
                    SharedPreferences sharedPreferences=getSharedPreferences("InCommunity.pref",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("UsingCommunity::",true);
                    editor.commit();
                    startService(new Intent(MainActivity.this,RecentImageService.class));
                    JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
                    builder.setPeriodic(15*60*1000);
                    builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                    builder.setPersisted(true);
                    jobInfo = builder.build();
                    jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    jobScheduler.schedule(jobInfo);



                }

            });
            builder.create().show();







    }

}

