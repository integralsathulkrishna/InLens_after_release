package integrals.inlens;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.cocosw.bottomsheet.BottomSheet;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import integrals.inlens.Activities.CloudAlbum;
import integrals.inlens.Activities.CreateCloudAlbum;
import integrals.inlens.Activities.IntroActivity;
import integrals.inlens.Activities.LoginActivity;
import integrals.inlens.Activities.QRCodeReader;
import integrals.inlens.Activities.SharedImageActivity;
import integrals.inlens.Helper.CurrentDatabase;

import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.Models.AlbumModel;
import integrals.inlens.Models.Participants;
import integrals.inlens.Services.RecentImageService;
import integrals.inlens.ViewHolder.AlbumViewHolder;
import integrals.inlens.ViewHolder.ParticipantsViewHolder;


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
    private LayoutAnimationController animation;
    private Dialog PasteCloudAlbumLink;
    private ProgressBar MainLoadingProgressBar;

    private int EDIT_COUNT, CLICK_COUNT;
    private DatabaseReference ProgressRef;

    private String PostKeyForEdit;
    private Activity activity;
    private Dialog ProfileDialog, AlbumCoverEditDialog , PasteImageLink;
    private String dbname = "", dbimage = "", dbemail = "";
    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;
    private ProgressBar progressBar;
    private CircleImageView UserImage;
    private ImageButton ChangeuserImage, CloseProfileDialog;
    private TextView ProfileuserName, ProfileUserEmail;

    private ProgressBar AlbumCoverEditprogressBar;
    private CircleImageView AlbumCoverEditUserImage;
    private ImageButton AlbumCoverEditChangeuserImage, AlbumCoverEditCloseProfileDialog;
    private TextView AlbumCoverEditProfileuserName, AlbumCoverEditProfileUserEmail;
    private static final int COVER_GALLERY_PICK = 78;

    // Static boolean for cover and profile
    // do not delete
    private static boolean COVER_CHANGE = false, PROFILE_CHANGE = false;

    //For All ParticipantsBottomSheet
    private Dialog ParticpantsBottomSheetDialog;
    private RecyclerView ParticpantsBottomSheetDialogRecyclerView;
    private ImageButton ParticpantsBottomSheetDialogCloseBtn;
    private TextView ParticpantsBottomSheetDialogTitle;
    private ProgressBar ParticpantsBottomSheetDialogProgressbar;

    //For snackbar about Connectivity Info;
    private RelativeLayout RootForMainActivity;
    private static boolean SEARCH_IN_PROGRESS = false;
    private Menu MainMenu;
    private IBinder WindowToken;
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


        activity = this;


        //1.Service Running Continuation
        RecentImageService recentImageService;
        recentImageService = new RecentImageService(getApplicationContext());
        if (!isMyServiceRunning(recentImageService.getClass()) && firebaseUser != null) {
            startService(new Intent(getApplicationContext(), RecentImageService.class));
        }

        //Snackbar
        RootForMainActivity = findViewById(R.id.root_for_main_activity);

        //ProfileDialog
        ProfileDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        ProfileDialog.setCancelable(true);
        ProfileDialog.setCanceledOnTouchOutside(true);
        ProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ProfileDialog.setContentView(R.layout.custom_profile_dialog);
        ProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ProfileDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window ProfileDialogwindow = ProfileDialog.getWindow();
        ProfileDialogwindow.setGravity(Gravity.BOTTOM);
        ProfileDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        ProfileDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ProfileDialogwindow.setDimAmount(0.75f);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = ProfileDialog.findViewById(R.id.custom_profile_dialog_progressbar);
        UserImage = ProfileDialog.findViewById(R.id.custom_profile_dialog_userprofilepic);
        ChangeuserImage = ProfileDialog.findViewById(R.id.custom_profile_dialog_profilechangebtn);
        ProfileUserEmail = ProfileDialog.findViewById(R.id.custom_profile_dialog_useremail);
        ProfileuserName = ProfileDialog.findViewById(R.id.custom_profile_dialog_username);

        CloseProfileDialog = ProfileDialog.findViewById(R.id.custom_profile_dialog_closebtn);

        CloseProfileDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProfileDialog.dismiss();

            }
        });

        ChangeuserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IsConnectedToNet()) {
                    COVER_CHANGE = false;
                    PROFILE_CHANGE = true;
                    GetStartedWithNewProfileImage();
                } else {
                    Snackbar.make(RootForMainActivity, "Unable to connect to internet. Try again.", Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        //AlbumCoverEditDialog
        AlbumCoverEditDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        AlbumCoverEditDialog.setCancelable(true);
        AlbumCoverEditDialog.setCanceledOnTouchOutside(true);
        AlbumCoverEditDialog.setContentView(R.layout.custom_profile_dialog);
        AlbumCoverEditDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window AlbumCoverEditwindow = AlbumCoverEditDialog.getWindow();
        AlbumCoverEditwindow.setGravity(Gravity.BOTTOM);
        AlbumCoverEditwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        AlbumCoverEditwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        AlbumCoverEditwindow.setDimAmount(0.75f);

        AlbumCoverEditprogressBar = AlbumCoverEditDialog.findViewById(R.id.custom_profile_dialog_progressbar);
        AlbumCoverEditUserImage = AlbumCoverEditDialog.findViewById(R.id.custom_profile_dialog_userprofilepic);
        AlbumCoverEditChangeuserImage = AlbumCoverEditDialog.findViewById(R.id.custom_profile_dialog_profilechangebtn);
        AlbumCoverEditProfileUserEmail = AlbumCoverEditDialog.findViewById(R.id.custom_profile_dialog_useremail);
        AlbumCoverEditProfileuserName = AlbumCoverEditDialog.findViewById(R.id.custom_profile_dialog_username);
        AlbumCoverEditCloseProfileDialog = AlbumCoverEditDialog.findViewById(R.id.custom_profile_dialog_closebtn);

        AlbumCoverEditCloseProfileDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlbumCoverEditDialog.dismiss();

            }
        });

        AlbumCoverEditChangeuserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IsConnectedToNet()) {

                    COVER_CHANGE = true;
                    PROFILE_CHANGE = false;

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio((int) 390, 285)
                            .setFixAspectRatio(true)
                            .start(MainActivity.this);

                } else {
                    Snackbar.make(RootForMainActivity, "Unable to connect to internet. Try again.", Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        //ParticipantsBottomSheetDialog
        ParticpantsBottomSheetDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        ParticpantsBottomSheetDialog.setCancelable(true);
        ParticpantsBottomSheetDialog.setCanceledOnTouchOutside(true);
        ParticpantsBottomSheetDialog.setContentView(R.layout.participants_bottomsheet_layout);
        ParticpantsBottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window ParticpantsBottomSheetDialogwindow = ParticpantsBottomSheetDialog.getWindow();
        ParticpantsBottomSheetDialogwindow.setGravity(Gravity.BOTTOM);
        ParticpantsBottomSheetDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        ParticpantsBottomSheetDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ParticpantsBottomSheetDialogwindow.setDimAmount(0.75f);

        ParticpantsBottomSheetDialogRecyclerView = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_recyclerview);
        ParticpantsBottomSheetDialogRecyclerView.setHasFixedSize(true);
        GridLayoutManager Gridmanager = new GridLayoutManager(MainActivity.this, 3);
        ParticpantsBottomSheetDialogRecyclerView.setLayoutManager(Gridmanager);

        ParticpantsBottomSheetDialogTitle = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_title);

        ParticpantsBottomSheetDialogCloseBtn = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_closebtn);
        ParticpantsBottomSheetDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParticpantsBottomSheetDialog.dismiss();
            }
        });

        ParticpantsBottomSheetDialogProgressbar = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_progressbar);

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
        MemoryRecyclerView.setVisibility(View.VISIBLE);
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
                    viewHolder.SetProfilePic(getApplicationContext(), model.getPostedByProfilePic());
                    viewHolder.SetAlbumDescription(model.getAlbumDescription());


                    try {
                        InDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    CommunityID = dataSnapshot.child(getRef(position).getKey()).child("CommunityID").getValue().toString().trim();
                                    getParticipantDatabaseReference = participantDatabaseReference.child("Communities").child(CommunityID).child("CommunityPhotographer");
                                    viewHolder.SetParticipants(MainActivity.this, getParticipantDatabaseReference, FirebaseDatabase.getInstance().getReference().child("Users"));


                                    // for timestamp

                                    String key = dataSnapshot.child(getRef(position).getKey()).getKey();

                                    if (dataSnapshot.child(key).hasChild("CreatedTimestamp")) {
                                        String timestamp = dataSnapshot.child(key).child("CreatedTimestamp").getValue().toString();
                                        long time = Long.parseLong(timestamp);
                                        CharSequence Time = DateUtils.getRelativeDateTimeString(getApplicationContext(), time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
                                        String timesubstring = Time.toString().substring(Time.length() - 8);
                                        Date date = new Date(time);
                                        String dateformat = DateFormat.format("dd-MM-yyyy", date).toString();
                                        viewHolder.SetAlbumTime("Event occured on : " + dateformat + " @ " + timesubstring);
                                    } else {
                                        viewHolder.SetAlbumTime("Event occured on : " + model.getTime());

                                    }

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

                            AlbumCoverEditprogressBar.setVisibility(View.VISIBLE);
                            AlbumCoverEditProfileuserName.setText(model.getAlbumTitle());
                            AlbumCoverEditProfileUserEmail.setTextSize(13);
                            AlbumCoverEditProfileUserEmail.setText(String.format("Change Cover for the album \" %s \"", model.getAlbumTitle()));

                            PostKeyForEdit = getRef(position).getKey().trim();
                            FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Communities")
                                    .child(PostKeyForEdit)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String Image = dataSnapshot.child("AlbumCoverImage").getValue().toString();
                                            RequestOptions requestOptions = new RequestOptions()
                                                    .fitCenter();

                                            Glide.with(getApplicationContext())
                                                    .load(Image)
                                                    .thumbnail(0.1f)
                                                    .apply(requestOptions)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            AlbumCoverEditprogressBar.setVisibility(View.GONE);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            AlbumCoverEditprogressBar.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(AlbumCoverEditUserImage);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            AlbumCoverEditDialog.show();


                        }
                    });

                    // more participants

                    viewHolder.ParticipantsMoreBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ParticpantsBottomSheetDialogProgressbar.setVisibility(View.VISIBLE);
                            PostKeyForEdit = getRef(position).getKey().trim();
                            DisplayAllParticipantsAsBottomSheet(PostKeyForEdit, FirebaseDatabase.getInstance().getReference());

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

    private void DisplayAllParticipantsAsBottomSheet(String postKeyForEdit, DatabaseReference getParticipantDatabaseReference) {

        ParticpantsBottomSheetDialog.show();

        final Dialog BottomSheetUserDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        BottomSheetUserDialog.setCancelable(true);
        BottomSheetUserDialog.setCanceledOnTouchOutside(true);
        BottomSheetUserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BottomSheetUserDialog.setContentView(R.layout.custom_profile_dialog);
        BottomSheetUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BottomSheetUserDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window BottomSheetUserDialogWindow = BottomSheetUserDialog.getWindow();
        BottomSheetUserDialogWindow.setGravity(Gravity.BOTTOM);
        BottomSheetUserDialogWindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        BottomSheetUserDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        BottomSheetUserDialogWindow.setDimAmount(0.75f);

        final ProgressBar progressBar = BottomSheetUserDialog.findViewById(R.id.custom_profile_dialog_progressbar);
        final CircleImageView UserImage = BottomSheetUserDialog.findViewById(R.id.custom_profile_dialog_userprofilepic);
        ImageButton ChangeuserImage = BottomSheetUserDialog.findViewById(R.id.custom_profile_dialog_profilechangebtn);
        ChangeuserImage.setVisibility(View.GONE);
        final TextView ProfileUserEmail = BottomSheetUserDialog.findViewById(R.id.custom_profile_dialog_useremail);
        final TextView ProfileuserName = BottomSheetUserDialog.findViewById(R.id.custom_profile_dialog_username);
        ImageButton CloseProfileDialog = BottomSheetUserDialog.findViewById(R.id.custom_profile_dialog_closebtn);

        CloseProfileDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetUserDialog.dismiss();
            }
        });


        final FirebaseRecyclerAdapter<Participants, ParticipantsViewHolder> BottomSheetRecyclerAdapter =
                new FirebaseRecyclerAdapter<Participants, ParticipantsViewHolder>(
                        Participants.class,
                        R.layout.member_card,
                        ParticipantsViewHolder.class,
                        getParticipantDatabaseReference.child("Communities").child(postKeyForEdit).child("CommunityPhotographer")
                ) {

                    @Override
                    public int getItemCount() {
                        return super.getItemCount();
                    }

                    @Override
                    protected void populateViewHolder(ParticipantsViewHolder viewHolder,
                                                      final Participants model,
                                                      int position) {


                        viewHolder.setProfile_picture(getApplicationContext(), model.getProfile_picture());
                        viewHolder.setUserName(model.getName());

                        viewHolder.InView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                BottomSheetUserDialog.dismiss();

                                ProfileUserEmail.setText(String.format("Email : %s", model.getEmail_ID()));

                                if (!TextUtils.isEmpty(model.getProfile_picture())) {
                                    RequestOptions requestOptions = new RequestOptions()
                                            .fitCenter();

                                    Glide.with(MainActivity.this)
                                            .load(model.getProfile_picture())
                                            .apply(requestOptions)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .into(UserImage);
                                } else {
                                    Glide.with(MainActivity.this).load(R.drawable.ic_account_200dp).into(UserImage);
                                    progressBar.setVisibility(View.GONE);
                                }
                                ProfileuserName.setText(model.getName());
                                BottomSheetUserDialog.show();


                            }
                        });

                    }

                };
        ParticpantsBottomSheetDialogRecyclerView.setAdapter(BottomSheetRecyclerAdapter);
        ParticpantsBottomSheetDialogProgressbar.setVisibility(View.GONE);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MainMenu = menu;

        menu.add(0, 1, 1, "Menu")
                .setIcon(R.drawable.menu_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 0, 0, "Search")
                .setIcon(R.drawable.ic_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==0)
        {
            MainMenu.setGroupVisible(0,false);
            MainMenu.setGroupVisible(1,false);

            getSupportActionBar().setDisplayShowCustomEnabled(true);
            SEARCH_IN_PROGRESS = true;
            View SearchActionbarView = LayoutInflater.from(getSupportActionBar().getThemedContext()).inflate(R.layout.search_layout,null);
            android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            getSupportActionBar().setCustomView(SearchActionbarView,params);

            ImageButton SearchBack = SearchActionbarView.findViewById(R.id.search_back_btn);
            final EditText SearchEditText = SearchActionbarView.findViewById(R.id.search_edittext);
            WindowToken = SearchEditText.getWindowToken();
            SearchEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(SearchEditText,InputMethodManager.SHOW_IMPLICIT);

            SearchBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(SearchEditText.getWindowToken(),0);
                    onStart();
                    getSupportActionBar().setDisplayShowCustomEnabled(false);
                    MainMenu.setGroupVisible(0,true);
                    MainMenu.setGroupVisible(1,true);
                }
            });

            SearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(!TextUtils.isEmpty(editable.toString()))
                    {
                        MemoryRecyclerView.setVisibility(View.VISIBLE);
                        ShowSearchResults(editable.toString());
                    }

                }
            });

        }

        else if (item.getItemId() == 1) {

            if (IsConnectedToNet()) {
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
                                DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild("Name")) {
                                            dbname = dataSnapshot.child("Name").getValue().toString();
                                        }
                                        if (dataSnapshot.hasChild("Profile_picture")) {
                                            dbimage = dataSnapshot.child("Profile_picture").getValue().toString();
                                        }
                                        if (dataSnapshot.hasChild("Email")) {
                                            dbemail = dataSnapshot.child("Email").getValue().toString();
                                        }
                                        int count = 0;
                                        if (dataSnapshot.hasChild("Communities")) {
                                            for (DataSnapshot snapshot : dataSnapshot.child("Communities").getChildren()) {
                                                count++;
                                            }
                                        }

                                        ProfileUserEmail.setText(String.format("Email : %s", dbemail));

                                        if (!TextUtils.isEmpty(dbimage)) {
                                            RequestOptions requestOptions = new RequestOptions()
                                                    .fitCenter();

                                            Glide.with(MainActivity.this)
                                                    .load(dbimage)
                                                    .apply(requestOptions)
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            progressBar.setVisibility(View.GONE);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            progressBar.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(UserImage);
                                        } else {
                                            Glide.with(MainActivity.this).load(R.drawable.ic_account_200dp).into(UserImage);
                                        }

                                        ProfileuserName.setText(dbname);
                                        ProfileDialog.show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

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
                                    PasteCloudAlbumLink = new Dialog(MainActivity.this, android.R.style.Theme_Light_NoTitleBar);
                                    PasteCloudAlbumLink.setCancelable(true);
                                    PasteCloudAlbumLink.setCanceledOnTouchOutside(true);
                                    PasteCloudAlbumLink.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    PasteCloudAlbumLink.setContentView(R.layout.paste_link_layout);
                                    PasteCloudAlbumLink.getWindow().getAttributes().windowAnimations = R.style.UpBottomSlideDialogAnimation;

                                    Window PasteCloudAlbumLinkWindow = PasteCloudAlbumLink.getWindow();
                                    PasteCloudAlbumLinkWindow.setGravity(Gravity.TOP);
                                    PasteCloudAlbumLinkWindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
                                    PasteCloudAlbumLinkWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    PasteCloudAlbumLinkWindow.setDimAmount(0.75f);
                                    PasteCloudAlbumLinkWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

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

                                }

                                break;


                            case R.id.restart_service:{
                                SharedPreferences sharedPreferencesS = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                                if (sharedPreferencesS.getBoolean("UsingCommunity::", false)) {
                                    startService(new Intent(getApplicationContext(),RecentImageService.class));
                                }

                            }
                            break;
                            case R.id.paste_image_link:
                            {
                                PasteImageLink = new Dialog(MainActivity.this,android.R.style.Theme_Light_NoTitleBar);
                                PasteImageLink.setCancelable(true);
                                PasteImageLink.setCanceledOnTouchOutside(true);
                                PasteImageLink.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                PasteImageLink.setContentView(R.layout.create_new_situation_layout);
                                PasteImageLink.getWindow().getAttributes().windowAnimations = R.style.UpBottomSlideDialogAnimation;

                                Window PasteCloudAlbumLinkWindow = PasteImageLink.getWindow();
                                PasteCloudAlbumLinkWindow.setGravity(Gravity.TOP);
                                PasteCloudAlbumLinkWindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
                                PasteCloudAlbumLinkWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                PasteCloudAlbumLinkWindow.setDimAmount(0.75f);
                                PasteCloudAlbumLinkWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                                TextView PasteImageLinkTitle , PasteImageLinkMessage;
                                PasteImageLinkMessage = PasteImageLink.findViewById(R.id.message);
                                PasteImageLinkTitle = PasteImageLink.findViewById(R.id.title);
                                PasteImageLinkTitle.setText("Album Image");
                                PasteImageLinkMessage.setText("Paste your image here to decrypt and open it. Open Web Links coming son.");
                                final EditText LinkEdit = PasteImageLink.findViewById(R.id.situation_name);
                                LinkEdit.setHint("Paste Link Here");
                                LinkEdit.requestFocus();
                                Button Done ,Cancel;
                                Done =   PasteImageLink.findViewById(R.id.done_btn);
                                Cancel = PasteImageLink.findViewById(R.id.cancel_btn);

                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PasteImageLink.dismiss();
                                    }
                                });
                                Done.setText("Decrypt");
                                Done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if(!TextUtils.isEmpty(LinkEdit.getText().toString()))
                                        {

                                            String realdata = LinkEdit.getText().toString().replace("https://inlens.in/","");
                                            String SubString = "https://firebasestorage.googleapis.com/v0/b/inlens-f0ce2.appspot.com/o/OriginalImage_thumb";
                                            String ImageUrl=SubString+realdata;
                                            if(!ImageUrl.contains("joins"))
                                                startActivity(new Intent(MainActivity.this,SharedImageActivity.class).putExtra("url",ImageUrl));
                                            else
                                                Toast.makeText(getApplicationContext(),"Album link detected.",Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Empty link.",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                PasteImageLink.show();
                            }
                            break;

                        }
                    }
                }).show();
            } else {
                Snackbar.make(RootForMainActivity, "Unable to connect to internet. Try again.", Snackbar.LENGTH_SHORT).show();
            }

        }


        return true;
    }

    private void ShowSearchResults(final String s) {

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

                    if(model.getAlbumTitle().toLowerCase().contains(s.toLowerCase()))
                    {
                        viewHolder.SetAlbumCover(getApplicationContext(), model.getAlbumCoverImage());
                        viewHolder.SetTitle(model.getAlbumTitle());
                        viewHolder.SetProfilePic(getApplicationContext(), model.getPostedByProfilePic());
                        viewHolder.SetAlbumDescription(model.getAlbumDescription());


                        try {
                            InDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        CommunityID = dataSnapshot.child(getRef(position).getKey()).child("CommunityID").getValue().toString().trim();
                                        getParticipantDatabaseReference = participantDatabaseReference.child("Communities").child(CommunityID).child("CommunityPhotographer");
                                        viewHolder.SetParticipants(MainActivity.this, getParticipantDatabaseReference, FirebaseDatabase.getInstance().getReference().child("Users"));


                                        // for timestamp

                                        String key = dataSnapshot.child(getRef(position).getKey()).getKey();

                                        if (dataSnapshot.child(key).hasChild("CreatedTimestamp")) {
                                            String timestamp = dataSnapshot.child(key).child("CreatedTimestamp").getValue().toString();
                                            long time = Long.parseLong(timestamp);
                                            CharSequence Time = DateUtils.getRelativeDateTimeString(getApplicationContext(), time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
                                            String timesubstring = Time.toString().substring(Time.length() - 8);
                                            Date date = new Date(time);
                                            String dateformat = DateFormat.format("dd-MM-yyyy", date).toString();
                                            viewHolder.SetAlbumTime("Event occured on : " + dateformat + " @ " + timesubstring);
                                        } else {
                                            viewHolder.SetAlbumTime("Event occured on : " + model.getTime());

                                        }

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

                                AlbumCoverEditprogressBar.setVisibility(View.VISIBLE);
                                AlbumCoverEditProfileuserName.setText(model.getAlbumTitle());
                                AlbumCoverEditProfileUserEmail.setTextSize(13);
                                AlbumCoverEditProfileUserEmail.setText(String.format("Change Cover for the album \" %s \"", model.getAlbumTitle()));

                                PostKeyForEdit = getRef(position).getKey().trim();
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("Communities")
                                        .child(PostKeyForEdit)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String Image = dataSnapshot.child("AlbumCoverImage").getValue().toString();
                                                RequestOptions requestOptions = new RequestOptions()
                                                        .fitCenter();

                                                Glide.with(getApplicationContext())
                                                        .load(Image)
                                                        .thumbnail(0.1f)
                                                        .apply(requestOptions)
                                                        .listener(new RequestListener<Drawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                AlbumCoverEditprogressBar.setVisibility(View.GONE);
                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                AlbumCoverEditprogressBar.setVisibility(View.GONE);
                                                                return false;
                                                            }
                                                        })
                                                        .into(AlbumCoverEditUserImage);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                AlbumCoverEditDialog.show();


                            }
                        });

                        // more participants

                        viewHolder.ParticipantsMoreBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ParticpantsBottomSheetDialogProgressbar.setVisibility(View.VISIBLE);
                                PostKeyForEdit = getRef(position).getKey().trim();
                                DisplayAllParticipantsAsBottomSheet(PostKeyForEdit, FirebaseDatabase.getInstance().getReference());

                            }
                        });

                        MainLoadingProgressBar.setVisibility(View.INVISIBLE);


                    }
                    else
                    {
                        MemoryRecyclerView.setVisibility(View.GONE);
                       Toast.makeText(getApplicationContext(),"No album found",Toast.LENGTH_SHORT).show();
                    }

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
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });
        builder.setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
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
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Toast.makeText(getApplicationContext(), "Successfully left from the current Cloud-Album", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }


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
                                            currentDatabase.InsertUploadValues(CommunityID, 0, 1, 0, ALBT, 1, 1, "NILL");
                                            currentDatabase.close();
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

    public void GetStartedWithNewProfileImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COVER_GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
            finish();
        } else if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
            finish();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && COVER_CHANGE && !PROFILE_CHANGE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri ImageUri = result.getUri();
                AlbumCoverEditUserImage.setImageURI(ImageUri);
                AlbumCoverEditprogressBar.setVisibility(View.VISIBLE);
                AlbumCoverEditDialog.setCancelable(false);
                UploadCOverPhoto(ImageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && !COVER_CHANGE && PROFILE_CHANGE) {

            ProfileDialog.setCancelable(false);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                progressBar.setVisibility(View.VISIBLE);

                Uri resultUri = result.getUri();
                try {
                    InputStream stream = getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    UserImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                File thumb_filePath = new File(resultUri.getPath());
                final String current_u_i_d = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(100)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mStorageRef.child("profile_images").child(current_u_i_d + ".jpg");
                final StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(current_u_i_d + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            com.google.firebase.storage.UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {


                                    String thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {


                                        Map update_Hashmap = new HashMap();
                                        update_Hashmap.put("Profile_picture", downloadUrl);
                                        update_Hashmap.put("thumb_image", thumb_downloadurl);

                                        FirebaseDatabase.getInstance().getReference().child("Users").child(current_u_i_d).updateChildren(update_Hashmap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            {
                                                                Toast.makeText(MainActivity.this, "SUCCESSFULLY UPLOADED", Toast.LENGTH_LONG).show();
                                                                progressBar.setVisibility(View.GONE);
                                                                ProfileDialog.setCancelable(true);

                                                            }
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);
                                                            ProfileDialog.setCancelable(true);
                                                            Toast.makeText(MainActivity.this, "FAILED TO SAVE TO DATABASE.MAKE SURE YOUR INTERNET IS CONNECTED AND TRY AGAIN.", Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        ProfileDialog.setCancelable(true);
                                        Toast.makeText(MainActivity.this, "FAILED TO UPLOAD THUMBNAIL", Toast.LENGTH_LONG).show();
                                    }

                                }

                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            ProfileDialog.setCancelable(true);
                            Toast.makeText(MainActivity.this, "FAILED TO UPLOAD", Toast.LENGTH_LONG).show();
                        }
                    }


                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                ProfileDialog.setCancelable(true);
            }
        }

    }

    private void UploadCOverPhoto(Uri imageUri) {

        AlbumCoverEditDialog.setCancelable(false);
        AlbumCoverEditprogressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(PostKeyForEdit) && imageUri != null) {

            StorageReference
                    FilePath = mStorageRef
                    .child("CommunityCoverPhoto")
                    .child(imageUri.getLastPathSegment());

            FilePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Communities")
                                .child(PostKeyForEdit)
                                .child("AlbumCoverImage")
                                .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AlbumCoverEditDialog.setCancelable(true);
                                    AlbumCoverEditprogressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Succesfully changed the Cover-Photo.", Toast.LENGTH_LONG).show();
                                } else {
                                    AlbumCoverEditDialog.setCancelable(true);
                                    AlbumCoverEditprogressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Unable to perform to change cover now.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    } else {
                        AlbumCoverEditDialog.setCancelable(true);
                        AlbumCoverEditprogressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Unable to perform to change cover now.", Toast.LENGTH_LONG).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlbumCoverEditDialog.setCancelable(true);
                    AlbumCoverEditprogressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Unable to perform to change cover now.", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            AlbumCoverEditDialog.setCancelable(true);
            AlbumCoverEditprogressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, "Unable to perform to change cover now.", Toast.LENGTH_LONG).show();
        }


    }

    private boolean IsConnectedToNet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

    }

    @Override
    public void onBackPressed() {

        if(SEARCH_IN_PROGRESS)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(WindowToken,0);
            MainMenu.setGroupVisible(0,true);
            MainMenu.setGroupVisible(1,true);
            SEARCH_IN_PROGRESS = false;
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            onStart();
        }
        else
        {
            super.onBackPressed();
        }
    }
}


