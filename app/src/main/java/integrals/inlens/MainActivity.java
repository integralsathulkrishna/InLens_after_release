package integrals.inlens;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import integrals.inlens.Activities.CloudAlbum;
import integrals.inlens.Activities.CoverEditActivity;
import integrals.inlens.Activities.CreateCloudAlbum;
import integrals.inlens.Activities.IntroActivity;
import integrals.inlens.Activities.LoginActivity;
import integrals.inlens.Activities.QRCodeReader;
import integrals.inlens.Activities.SettingActivity;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.InLensJobScheduler.InLensJobScheduler;
import integrals.inlens.Models.AlbumModel;
import integrals.inlens.Services.RecentImageService;
import integrals.inlens.ViewHolder.AlbumViewHolder;


public class MainActivity extends AppCompatActivity {
    private RecyclerView MemoryRecyclerView;
    private DatabaseReference InDatabaseReference;
    private String CommunityPostKey, AlbumCoverEditKey;
    private ComponentName componentName;
    private String CurrentUser;
    private FirebaseAuth InAuthentication;
    private FirebaseUser firebaseUser;
    private DatabaseReference participantDatabaseReference,
            getParticipantDatabaseReference, ComRef;
    private String CommunityID;
    private Intent intent;
    private static final int JOB_ID = 7907;
    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    private LayoutAnimationController animation;
    private Dialog PasteCloudAlbumLink;
    private ProgressBar MainLoadingProgressBar;

    private int EDIT_COUNT, CLICK_COUNT;
    private DatabaseReference ProgressRef;

    private String PostKeyForEdit;
    private Activity activity;
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
        ComponentName componentName = new ComponentName(this, InLensJobScheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
        builder.setPeriodic(15 * 60 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        jobInfo = builder.build();
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        activity = this;
        //1.Service Running Continuation
        RecentImageService recentImageService;
        recentImageService = new RecentImageService(getApplicationContext());
        if (!isMyServiceRunning(recentImageService.getClass()) && firebaseUser != null) {
            startService(new Intent(getApplicationContext(), RecentImageService.class));
        }


        //User Authentication
        InAuthentication = FirebaseAuth.getInstance();
        ProgressRef = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser = InAuthentication.getCurrentUser();
        try {
            if (firebaseUser == null) {
                startActivity(new Intent(MainActivity.this, IntroActivity.class));
                finish();
            } else {
                if (firebaseUser.isEmailVerified() == false) {

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                } else {
                    CurrentUser = firebaseUser.getUid();
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Participant Database Reference
        participantDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //Setting Recycler View
        MemoryRecyclerView = (RecyclerView) findViewById(R.id.CloudAlbumRecyclerView);
        MemoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        MemoryRecyclerView.setLayoutManager(linearLayoutManager);
        MainLoadingProgressBar = findViewById(R.id.mainloadingpbar);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            ProgressRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild("Communities")) {
                        MainLoadingProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

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
        EDIT_COUNT = 0;
        CLICK_COUNT = 0;
        // Downloading Recycler View
        MainLoadingProgressBar.setVisibility(View.VISIBLE);
        try {

            InDatabaseReference =
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("Users")
                            .child(CurrentUser)
                            .child("Communities");

            final FirebaseRecyclerAdapter<AlbumModel, AlbumViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AlbumModel, AlbumViewHolder>(
                    AlbumModel.class,
                    R.layout.cloud_album_card,
                    AlbumViewHolder.class,
                    InDatabaseReference

            ) {
                @Override
                protected void populateViewHolder(final AlbumViewHolder viewHolder, final AlbumModel model, final int position) {
                    viewHolder.SetAlbumCover(getApplicationContext(), model.getAlbumCoverImage());
                    viewHolder.SetTitle(model.getAlbumTitle());
                    viewHolder.SetAlbumTime("Event occured on  " + model.getTime());
                    viewHolder.SetProfilePic(getApplicationContext(), model.getPostedByProfilePic());
                    viewHolder.SetAlbumDescription(model.getAlbumDescription());
                    try {
                        InDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    CommunityID = dataSnapshot.child(getRef(position).getKey().toString()).child("CommunityID").getValue().toString().trim();
                                    getParticipantDatabaseReference = participantDatabaseReference.child("Communities").child(CommunityID).child("CommunityPhotographer");
                                    viewHolder.SetParticipants(getApplicationContext(), getParticipantDatabaseReference);

                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } catch (NullPointerException e) {
                    }
                    viewHolder.ShareButton.setOnClickListener(new View.OnClickListener() {
                        final String PostKeyS = getRef(position).getKey().trim();


                        @Override
                        public void onClick(View v) {

                            final Intent SharingIntent = new Intent(Intent.ACTION_SEND);
                            SharingIntent.setType("text/plain");
                            InDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CommunityPostKey = dataSnapshot.child(PostKeyS).child("CommunityID").getValue().toString().trim();
                                    SharingIntent.putExtra(Intent.EXTRA_TEXT, "https://inlens.in/watch/" + CommunityPostKey);
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
                            final String PostKey = getRef(position).getKey().trim();
                            CLICK_COUNT++;
                            if (CLICK_COUNT == 1 && !TextUtils.isEmpty(PostKey)) {
                                try {
                                    startActivity(new Intent(MainActivity.this, CloudAlbum.class)
                                            .putExtra("AlbumName", model.getAlbumTitle())
                                            .putExtra("GlobalID::", PostKey)
                                            .putExtra("LocalID::", PostKey)
                                            .putExtra("UserID::", CurrentUser));

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    });

                    viewHolder.AlbuymCoverEditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            PostKeyForEdit = getRef(position).getKey().trim();
                            startActivity(new Intent(MainActivity.this, CoverEditActivity.class).putExtra("PostKey", PostKeyForEdit));
                        }
                    });

                    MainLoadingProgressBar.setVisibility(View.INVISIBLE);
                }


            };

            MemoryRecyclerView.setAdapter(firebaseRecyclerAdapter);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        try {
            InDatabaseReference.keepSynced(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Add Participant")
                .setIcon(R.drawable.menu_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            new BottomSheet.Builder(this).title(" Options").sheet(R.menu.main_menu).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.new_album:
                            SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                            if (sharedPreferences.getBoolean("UsingCommunity::", false) == true) {
                                Toast.makeText(getApplicationContext(), "Sorry.You can't create a new Cloud-Album before you quit the current one.", Toast.LENGTH_LONG).show();
                            } else {
                                startActivity(new Intent(MainActivity.this, CreateCloudAlbum.class));
                            }


                            break;
                        case R.id.scan_qr:
                            SharedPreferences sharedPreferences1 = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                            if (sharedPreferences1.getBoolean("UsingCommunity::", false) == true) {
                                Toast.makeText(getApplicationContext(), "Sorry,You can't scan a new Cloud-Album before you quit the current one.", Toast.LENGTH_LONG).show();
                            } else

                            {
                                startActivity(new Intent(MainActivity.this, QRCodeReader.class));

                            }
                            break;
                        case R.id.upload_activity:
                            startActivity(new Intent(MainActivity.this, integrals.inlens.GridView.MainActivity.class));
                            break;
                        case R.id.profile_pic:
                            startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            break;
                        case R.id.quit_cloud_album:
                            SharedPreferences sharedPreferences3 = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                            if (sharedPreferences3.getBoolean("UsingCommunity::", false) == true) {
                                CurrentDatabase currentDatabase1 = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                if (currentDatabase1.GetUploadingTargetColumn() >= currentDatabase1.GetUploadingTotal()) {
                                    QuitCloudAlbum(0);
                                } else {
                                    QuitCloudAlbum(1);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "No Active Cloud-Album to quit.", Toast.LENGTH_SHORT).show();
                            }

                            break;


                        case R.id.paste_album_link:
                            SharedPreferences sharedPreferences2 = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                            if (sharedPreferences2.getBoolean("UsingCommunity::", false) == true) {
                                Toast.makeText(getApplicationContext(), "Sorry,You can't participate in a new Cloud-Album before you quit the current one.", Toast.LENGTH_LONG).show();
                            } else

                            {   // To paste invite link
                                PasteCloudAlbumLink = new Dialog(MainActivity.this);
                                PasteCloudAlbumLink.setContentView(R.layout.paste_link_layout);
                                PasteCloudAlbumLink.setCancelable(true);
                                final EditText Link = PasteCloudAlbumLink.findViewById(R.id.cloud_album_link_edittext);

                                Link.requestFocus();
                                Button Done, Cancel;
                                final ProgressBar progressBar;
                                Done = PasteCloudAlbumLink.findViewById(R.id.done_btn_paste_link_layout);
                                Cancel = PasteCloudAlbumLink.findViewById(R.id.cancel_btn_paste_link_layout);
                                progressBar = PasteCloudAlbumLink.findViewById(R.id.cloud_album_link_progress_bar);
                                Done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            String Data = Link.getText().toString();
                                            String str = Data.substring(18, 23);
                                            if (str.contentEquals("joins")) {
                                                Toast.makeText(getApplicationContext(), "Join " + Data.substring(24), Toast.LENGTH_SHORT).show();
                                                SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                                                if (sharedPreferences.getBoolean("UsingCommunity::", false) == true) {
                                                    Toast.makeText(getApplicationContext(), "Sorry.You can't join to a new Cloud-Album, " +
                                                            "before you quit the current one.", Toast.LENGTH_SHORT)
                                                            .show();
                                                } else {
                                                    AddToCloud(Data.substring(24), progressBar, PasteCloudAlbumLink);
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Invalid Link", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (StringIndexOutOfBoundsException e) {
                                            Toast.makeText(getApplicationContext(), "Invalid Link", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PasteCloudAlbumLink.hide();
                                    }
                                });
                                PasteCloudAlbumLink.show();
                                break;
                            }


                    }
                }
            }).show();
        }


        return true;
    }


    private void QuitCloudAlbum(int XYZ) {
        String Txt = " ";
        if (XYZ == 1) {
            Txt = "Selected images will not be uploaded to the Cloud-Album.";
        }
        final SharedPreferences sharedPreferences4 = getSharedPreferences("Owner.pref", MODE_PRIVATE);
        CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Communities")
                .child(currentDatabase.GetLiveCommunityID())
                .child("ActiveIndex");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Quit Cloud-Album");
        builder.setMessage("Are you sure you want to quit the current community ." + Txt);
        builder.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (sharedPreferences4.getBoolean("ThisOwner::", false) == true) {
                    databaseReference.setValue("F")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                    currentDatabase.DeleteDatabase();
                                    RecentImageDatabase recentImageDatabase = new RecentImageDatabase(getApplicationContext(), "", null, 1);
                                    recentImageDatabase.DeleteDatabase();
                                    UploadDatabaseHelper uploadDatabaseHelper = new UploadDatabaseHelper(getApplicationContext(), "", null, 1);
                                    uploadDatabaseHelper.DeleteDatabase();
                                    SharedPreferences sharedPreferencesC = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                                    SharedPreferences.Editor editorC = sharedPreferencesC.edit();
                                    editorC.putBoolean("UsingCommunity::", false);
                                    editorC.commit();
                                    stopService(new Intent(MainActivity.this, RecentImageService.class));
                                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                                    jobScheduler.cancel(7907);
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();
                                    Toast.makeText(getApplicationContext(), "Successfully left from the current Cloud-Album", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error . Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                    currentDatabase.DeleteDatabase();
                    RecentImageDatabase recentImageDatabase = new RecentImageDatabase(getApplicationContext(), "", null, 1);
                    recentImageDatabase.DeleteDatabase();
                    UploadDatabaseHelper uploadDatabaseHelper = new UploadDatabaseHelper(getApplicationContext(), "", null, 1);
                    uploadDatabaseHelper.DeleteDatabase();
                    SharedPreferences sharedPreferencesC = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                    SharedPreferences.Editor editorC = sharedPreferencesC.edit();
                    editorC.putBoolean("UsingCommunity::", false);
                    editorC.commit();
                    stopService(new Intent(MainActivity.this, RecentImageService.class));
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    jobScheduler.cancel(7907);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Toast.makeText(getApplicationContext(), "Successfully left from the current Cloud-Album", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }


    /*
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
*/
    private void AddToCloud(String substring, final ProgressBar progressBar, final Dialog dialog) {
        progressBar.setVisibility(View.VISIBLE);
        final DatabaseReference CommunityPhotographer;
        FirebaseAuth CommunityPhotographerAuthentication;
        final String UserID;
        final DatabaseReference UserData;
        final String CommunityID = substring;
        final DatabaseReference[] databaseReference = new DatabaseReference[1];
        final DatabaseReference databaseReference2, databaseReference3, databaseReference4;
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
        databaseReference2 = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Communities")
                .child(CommunityID);
        databaseReference4 = databaseReference2.child("AlbumExpiry");
        databaseReference3 = databaseReference2.child("ActiveIndex");
        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("T")) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(true);
                    builder.setTitle("Join");
                    builder.setMessage("Join this Cloud-Album. Proceed joining it ?");
                    builder.setPositiveButton(" YES ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


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
                            final DatabaseReference AddingAlbumToReference = databaseReference[0].child(CommunityID);
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


                                    databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String ALBT = dataSnapshot.getValue().toString();
                                            CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
                                            currentDatabase.InsertUploadValues(CommunityID, 0, 1, 0, ALBT, 1, 1);
                                            currentDatabase.close();
                                            Toast.makeText(getApplicationContext(), "Setting up database till " + ALBT, Toast.LENGTH_SHORT).show();
                                            StartServices();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    setIntent(null);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Sorry network error...please try again", Toast.LENGTH_SHORT).show();
                                }
                            });


                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                        }

                        private void StartServices() {
                            SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("UsingCommunity::", true);
                            editor.commit();
                            SharedPreferences sharedPreferences1 = getSharedPreferences("Owner.pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                            editor1.putBoolean("ThisOwner::", false);
                            editor1.commit();

                            startService(new Intent(MainActivity.this, RecentImageService.class));
                            jobScheduler.schedule(jobInfo);


                        }

                    });
                    builder.create().show();
                    progressBar.setVisibility(View.INVISIBLE);
                    dialog.hide();


                } else {
                    Toast.makeText(getApplicationContext(), "Album time expired. You can't participate in this Cloud-Album.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    dialog.hide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}


