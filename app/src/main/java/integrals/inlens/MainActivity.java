package integrals.inlens;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vistrav.ask.Ask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import integrals.inlens.Activities.CloudAlbum;
import integrals.inlens.Activities.CreateCloudAlbum;
import integrals.inlens.Activities.IntroActivity;
import integrals.inlens.Activities.IssueActivity;
import integrals.inlens.Activities.LoginActivity;
import integrals.inlens.Activities.QRCodeReader;
import integrals.inlens.Activities.SharedImageActivity;
import integrals.inlens.Helper.CurrentDatabase;

import integrals.inlens.Helper.JobSchedulerHelper;
import integrals.inlens.Helper.RecentImageDatabase;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.Models.AlbumModel;
import integrals.inlens.Models.Participants;
import integrals.inlens.Services.OreoService;
import integrals.inlens.Services.RecentImageService;
import integrals.inlens.ViewHolder.AlbumViewHolder;
import integrals.inlens.ViewHolder.ParticipantsViewHolder;


public class MainActivity extends AppCompatActivity {

    private RecyclerView MemoryRecyclerView;
    private DatabaseReference InDatabaseReference;

    private String CommunityPostKey;
    private String CurrentUser;
    private FirebaseAuth InAuthentication;
    private FirebaseUser firebaseUser;
    private DatabaseReference participantDatabaseReference;
    private ProgressBar MainLoadingProgressBar;

    private DatabaseReference ProgressRef;

    private String PostKeyForEdit;
    private Activity activity;
    private Dialog ProfileDialog;
    private Dialog AlbumCoverEditDialog, DetailsDialog;
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
    private Dialog ParticpantsBottomSheetDialog, QRCodeDialog;
    private RecyclerView ParticpantsBottomSheetDialogRecyclerView;
    private ImageButton ParticpantsBottomSheetDialogCloseBtn;
    private ProgressBar ParticpantsBottomSheetDialogProgressbar;

    //For snackbar about Connectivity Info;
    private RelativeLayout RootForMainActivity;

    //For Searching
    private static boolean SEARCH_IN_PROGRESS = false;
    private Menu MainMenu;
    private MainSearchAdapter MainAdapterForSearch;
    private List<AlbumModel> SearchedAlbums = new ArrayList<>();
    private List<String> AlbumKeys = new ArrayList<>();
    private Boolean QRCodeVisible = false;
    private int INTID = 3939;
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

    //for lastclick album
    private SharedPreferences AlbumClickDetails;
    private FloatingActionButton MainFab, CreateAlbumFab, ScanQrFab;
    private Animation FabOpen, FabClose, FabRotateForward, FabRotateBackward, AlbumCardOpen, AlbumCardClose;
    private boolean isOpen = false;
    private TextView MainCreateAlbumTxtview, MainScanQrTxtview;
    private RelativeLayout MainDimBackground;

    //for details Dialog
    private TextView AlbumTitle,
            AlbumDesc, AlbumOwner,
            AlbumType, AlbumStartTime,
            AlbumEndTime, AlbumPostCount, AlbumMemberCount;
    private int PostCount, MemberCount;
    private TextView NoAlbumTextView;
    private SharedPreferences FirstRunMain;

    private JobSchedulerHelper jobSchedulerHelper;
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(25);

        QRCodeInit();
        PermissionsInit();
        FabAnimationAndButtonsInit();
        ProfileDialogInit();
        AlbumCoverEditDialogInit();
        ParticipantsBottomSheetDialogInit();
        DetailsDialogInit();

        NoAlbumTextView = findViewById(R.id.nocloudalbumtextview);
        MainDimBackground = findViewById(R.id.main_dim_background);
        MainDimBackground.setVisibility(View.GONE);

        QRCodeVisible = getIntent().getBooleanExtra("QRCodeVisible", false);
        if (QRCodeVisible) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    QRCodeDialog.show();
                }
            }, 600);
        }

        activity = this;

        // to handle album clicks

        AlbumClickDetails = getSharedPreferences("LastClickedAlbum", MODE_PRIVATE);
        FirstRunMain = getSharedPreferences("MainActivityPref", MODE_PRIVATE);
        //Snackbar
        RootForMainActivity = findViewById(R.id.root_for_main_activity);

        //User Authentication
        InAuthentication = FirebaseAuth.getInstance();
        ProgressRef = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser = InAuthentication.getCurrentUser();
        try {
            if (firebaseUser == null) {
                startActivity(new Intent(MainActivity.this, IntroActivity.class));
                finish();
            } else {
                if (!firebaseUser.isEmailVerified()) {

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();

                } else {
                    CurrentUser = firebaseUser.getUid();
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        participantDatabaseReference = FirebaseDatabase.getInstance().getReference();
        MemoryRecyclerView = (RecyclerView) findViewById(R.id.CloudAlbumRecyclerView);
        MemoryRecyclerView.setHasFixedSize(true);
        MemoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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


        DecryptDeepLink();
        if (FirstRunMain.getBoolean("FirstRun", true)) {
            ShowAllTapTargets();
            SharedPreferences.Editor FirstRunMainEditor = FirstRunMain.edit();
            FirstRunMainEditor.putBoolean("FirstRun", false);
            FirstRunMainEditor.apply();
        }

        jobSchedulerHelper=new JobSchedulerHelper(getApplicationContext());

    }

    private void ShowAllTapTargets() {

        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.main_fab_btn), "Adding New Albums", "Click here to create new album or join one.")
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorPrimaryDark)
                        .textColor(R.color.white),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        AnimateFab();
                        TapTargetView.showFor(MainActivity.this,
                                TapTarget.forView(findViewById(R.id.main_create_album_fab_btn), "Create New Albums", "Click here to create new album.")
                                        .tintTarget(false)
                                        .outerCircleColor(R.color.colorPrimaryDark)
                                        .textColor(R.color.white),
                                new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);

                                        TapTargetView.showFor(MainActivity.this,
                                                TapTarget.forView(findViewById(R.id.main_scan_qr_fab_btn), "Join Albums", "Click here to join a new album.")
                                                        .tintTarget(false)
                                                        .outerCircleColor(R.color.colorPrimaryDark)
                                                        .textColor(R.color.white),
                                                new TapTargetView.Listener() {
                                                    @Override
                                                    public void onTargetClick(TapTargetView view) {
                                                        super.onTargetClick(view);
                                                        AnimateFab();

                                                        TapTargetView.showFor(MainActivity.this,
                                                                TapTarget.forView(findViewById(0), "Search", "Click here perform a search on albums.")
                                                                        .tintTarget(false)
                                                                        .outerCircleColor(R.color.colorPrimaryDark)
                                                                        .textColor(R.color.white)
                                                                        .targetCircleColor(R.color.black),
                                                                new TapTargetView.Listener() {
                                                                    @Override
                                                                    public void onTargetClick(TapTargetView view) {
                                                                        super.onTargetClick(view);

                                                                        TapTargetView.showFor(MainActivity.this,
                                                                                TapTarget.forView(findViewById(1), "More Options", "Click here get more options.")
                                                                                        .tintTarget(false)
                                                                                        .targetCircleColor(R.color.black)
                                                                                        .outerCircleColor(R.color.colorPrimaryDark)
                                                                                        .textColor(R.color.white),
                                                                                new TapTargetView.Listener() {
                                                                                    @Override
                                                                                    public void onTargetClick(TapTargetView view) {
                                                                                        super.onTargetClick(view);



                                                                                    }
                                                                                });

                                                                    }
                                                                });
                                                    }
                                                });

                                    }
                                });

                    }
                });


    }


    private void DecryptDeepLink() {

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                Uri DeepLink;
                if (pendingDynamicLinkData != null) {
                    DeepLink = pendingDynamicLinkData.getLink();
                    if (DeepLink != null) {


                        if (DeepLink.toString().contains("comid=")) {

                            String UrlOrDComId = (DeepLink.toString().substring(DeepLink.toString().length() - 27)).substring(0, 26);

                            SharedPreferences sharedPreferences2 = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                            if (sharedPreferences2.getBoolean("UsingCommunity::", false) == true) {

                                Toast.makeText(getApplicationContext(), "Sorry,You can't participate in a new Cloud-Album before you quit the current one.", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Join " + UrlOrDComId.substring(6, 26), Toast.LENGTH_SHORT).show();
                                AddToCloud(UrlOrDComId.substring(6, 26), progressBar);
                            }


                        } else if (DeepLink.toString().contains("imagelink") && DeepLink.toString().contains("linkimage")) {

                            String first = DeepLink.toString().replace("https://integrals.inlens.in/", "");
                            String second = first.replace("imagelink", "https://firebasestorage.googleapis.com/v0/b/inlens-f0ce2.appspot.com/o/OriginalImage_thumb%2F");
                            String third = second.replace("linkimage", "media&token=");
                            String ImageUrl = third.substring(0, third.length() - 1);

                            startActivity(new Intent(MainActivity.this, SharedImageActivity.class).putExtra("url", ImageUrl));

                        }
                    }
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), "Invite Link Failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void DetailsDialogInit() {

        DetailsDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        DetailsDialog.setCancelable(true);
        DetailsDialog.setCanceledOnTouchOutside(true);
        DetailsDialog.setContentView(R.layout.album_details_dialog_layout);
        DetailsDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window DetailsDialogwindow = DetailsDialog.getWindow();
        DetailsDialogwindow.setGravity(Gravity.BOTTOM);
        DetailsDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        DetailsDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        DetailsDialogwindow.setDimAmount(0.50f);

        ImageButton DetailsDialogCloseBtn = DetailsDialog.findViewById(R.id.details_dialog_close_btn);
        DetailsDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsDialog.dismiss();
            }
        });

        AlbumTitle = DetailsDialog.findViewById(R.id.details_dialog_albumtitle);
        AlbumDesc = DetailsDialog.findViewById(R.id.details_dialog_albumdesc);
        AlbumOwner = DetailsDialog.findViewById(R.id.details_dialog_albumowner);
        AlbumType = DetailsDialog.findViewById(R.id.details_dialog_albumtype);

        AlbumStartTime = DetailsDialog.findViewById(R.id.details_dialog_albumstarttime);
        AlbumEndTime = DetailsDialog.findViewById(R.id.details_dialog_albumendtime);

        AlbumPostCount = DetailsDialog.findViewById(R.id.details_dialog_albumpostcount);
        AlbumMemberCount = DetailsDialog.findViewById(R.id.details_dialog_albumparticipantscount);

    }

    private void ParticipantsBottomSheetDialogInit() {
        ParticpantsBottomSheetDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        ParticpantsBottomSheetDialog.setCancelable(true);
        ParticpantsBottomSheetDialog.setCanceledOnTouchOutside(true);
        ParticpantsBottomSheetDialog.setContentView(R.layout.participants_bottomsheet_layout);
        ParticpantsBottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window ParticpantsBottomSheetDialogwindow = ParticpantsBottomSheetDialog.getWindow();
        ParticpantsBottomSheetDialogwindow.setGravity(Gravity.BOTTOM);
        ParticpantsBottomSheetDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        ParticpantsBottomSheetDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ParticpantsBottomSheetDialogwindow.setDimAmount(0.50f);

        ParticpantsBottomSheetDialogRecyclerView = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_recyclerview);
        ParticpantsBottomSheetDialogRecyclerView.setHasFixedSize(true);
        GridLayoutManager Gridmanager = new GridLayoutManager(MainActivity.this, 3);
        ParticpantsBottomSheetDialogRecyclerView.setLayoutManager(Gridmanager);


        ParticpantsBottomSheetDialogCloseBtn = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_closebtn);
        ParticpantsBottomSheetDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParticpantsBottomSheetDialog.dismiss();
            }
        });

        ParticpantsBottomSheetDialogProgressbar = ParticpantsBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_progressbar);

    }

    private void AlbumCoverEditDialogInit() {

        AlbumCoverEditDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        AlbumCoverEditDialog.setCancelable(true);
        AlbumCoverEditDialog.setCanceledOnTouchOutside(true);
        AlbumCoverEditDialog.setContentView(R.layout.custom_profile_dialog);
        AlbumCoverEditDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window AlbumCoverEditwindow = AlbumCoverEditDialog.getWindow();
        AlbumCoverEditwindow.setGravity(Gravity.BOTTOM);
        AlbumCoverEditwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        AlbumCoverEditwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        AlbumCoverEditwindow.setDimAmount(0.50f);

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
    }

    private void ProfileDialogInit() {

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
        ProfileDialogwindow.setDimAmount(0.50f);

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
    }

    private void FabAnimationAndButtonsInit() {

        AlbumCardOpen = AnimationUtils.loadAnimation(this, R.anim.album_card_open);
        AlbumCardClose = AnimationUtils.loadAnimation(this, R.anim.album_card_close);

        FabOpen = AnimationUtils.loadAnimation(this, R.anim.main_fab_open);
        FabClose = AnimationUtils.loadAnimation(this, R.anim.main_fab_close);
        FabRotateForward = AnimationUtils.loadAnimation(this, R.anim.main_fab_rotate_forward);
        FabRotateBackward = AnimationUtils.loadAnimation(this, R.anim.main_fab_rotate_backward);

        MainFab = findViewById(R.id.main_fab_btn);
        ScanQrFab = findViewById(R.id.main_scan_qr_fab_btn);
        CreateAlbumFab = findViewById(R.id.main_create_album_fab_btn);
        MainCreateAlbumTxtview = findViewById(R.id.main_create_album_fab_txtview);
        MainScanQrTxtview = findViewById(R.id.main_scan_qr_fab_textview);

        MainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
            }
        });

        ScanQrFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();

                SharedPreferences sharedPreferences1 = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                if (sharedPreferences1.getBoolean("UsingCommunity::", false) == true) {
                    Toast.makeText(getApplicationContext(), "Sorry,You can't scan a new Cloud-Album before you quit the current one.", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(MainActivity.this, QRCodeReader.class));

                }
            }
        });

        CreateAlbumFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateFab();
                SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("UsingCommunity::", false)) {
                    Toast.makeText(getApplicationContext(), "Sorry.You can't create a new Cloud-Album before you quit the current one.", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(MainActivity.this, CreateCloudAlbum.class));
                    finish();
                }
            }
        });

    }

    private void CloseFabs()
    {
        if(MainDimBackground.isShown()){

            MainDimBackground.setVisibility(View.GONE);
            ScanQrFab.clearAnimation();
            ScanQrFab.setAnimation(FabClose);
            ScanQrFab.getAnimation().start();

            CreateAlbumFab.clearAnimation();
            CreateAlbumFab.setAnimation(FabClose);
            CreateAlbumFab.getAnimation().start();

            MainScanQrTxtview.clearAnimation();
            MainScanQrTxtview.setAnimation(FabClose);
            MainScanQrTxtview.getAnimation().start();

            MainCreateAlbumTxtview.clearAnimation();
            MainCreateAlbumTxtview.setAnimation(FabClose);
            MainCreateAlbumTxtview.getAnimation().start();

            CreateAlbumFab.setVisibility(View.INVISIBLE);
            ScanQrFab.setVisibility(View.INVISIBLE);
            MainCreateAlbumTxtview.setVisibility(View.INVISIBLE);
            MainScanQrTxtview.setVisibility(View.INVISIBLE);

            MainFab.clearAnimation();
            MainFab.setAnimation(FabRotateBackward);
            MainFab.getAnimation().start();
        }
        isOpen = false;
    }

    private void AnimateFab() {

        if (isOpen) {

            MainDimBackground.setVisibility(View.GONE);
            ScanQrFab.clearAnimation();
            ScanQrFab.setAnimation(FabClose);
            ScanQrFab.getAnimation().start();

            CreateAlbumFab.clearAnimation();
            CreateAlbumFab.setAnimation(FabClose);
            CreateAlbumFab.getAnimation().start();

            MainScanQrTxtview.clearAnimation();
            MainScanQrTxtview.setAnimation(FabClose);
            MainScanQrTxtview.getAnimation().start();

            MainCreateAlbumTxtview.clearAnimation();
            MainCreateAlbumTxtview.setAnimation(FabClose);
            MainCreateAlbumTxtview.getAnimation().start();

            CreateAlbumFab.setVisibility(View.INVISIBLE);
            ScanQrFab.setVisibility(View.INVISIBLE);
            MainCreateAlbumTxtview.setVisibility(View.INVISIBLE);
            MainScanQrTxtview.setVisibility(View.INVISIBLE);

            MainFab.clearAnimation();
            MainFab.setAnimation(FabRotateBackward);
            MainFab.getAnimation().start();

            isOpen = false;
        } else {

            ScanQrFab.clearAnimation();
            ScanQrFab.setAnimation(FabOpen);
            ScanQrFab.getAnimation().start();

            CreateAlbumFab.clearAnimation();
            CreateAlbumFab.setAnimation(FabOpen);
            CreateAlbumFab.getAnimation().start();

            MainScanQrTxtview.clearAnimation();
            MainScanQrTxtview.setAnimation(FabOpen);
            MainScanQrTxtview.getAnimation().start();

            MainCreateAlbumTxtview.clearAnimation();
            MainCreateAlbumTxtview.setAnimation(FabOpen);
            MainCreateAlbumTxtview.getAnimation().start();

            CreateAlbumFab.setVisibility(View.VISIBLE);
            ScanQrFab.setVisibility(View.VISIBLE);
            MainScanQrTxtview.setVisibility(View.VISIBLE);
            MainCreateAlbumTxtview.setVisibility(View.VISIBLE);

            MainFab.clearAnimation();
            MainFab.setAnimation(FabRotateForward);
            MainFab.getAnimation().start();
            isOpen = true;

            MainDimBackground.setVisibility(View.VISIBLE);

        }

    }

    private void PermissionsInit() {
        Ask.on(this)
                .id(INTID) // in case you are invoking multiple time Ask from same activity or fragment
                .forPermissions(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                        , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , android.Manifest.permission.INTERNET
                        , android.Manifest.permission.CAMERA
                        , android.Manifest.permission.ACCESS_FINE_LOCATION
                        , android.Manifest.permission.RECORD_AUDIO
                        , android.Manifest.permission.VIBRATE
                        , Manifest.permission.SYSTEM_ALERT_WINDOW
                )
                .go();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ShowAllAlbums();
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


        if (item.getItemId() == 0) {
            MainMenu.setGroupVisible(0, false);
            MainMenu.setGroupVisible(1, false);

            getSupportActionBar().setDisplayShowCustomEnabled(true);
            SEARCH_IN_PROGRESS = true;
            View SearchActionbarView = LayoutInflater.from(getSupportActionBar().getThemedContext()).inflate(R.layout.search_layout, null);
            android.support.v7.app.ActionBar.LayoutParams params = new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getSupportActionBar().setCustomView(SearchActionbarView, params);

            ImageButton SearchBack = SearchActionbarView.findViewById(R.id.search_back_btn);
            final EditText SearchEditText = SearchActionbarView.findViewById(R.id.search_edittext);
            SearchEditText.requestFocus();


            SearchBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    onStart();
                    getSupportActionBar().setDisplayShowCustomEnabled(false);
                    MainMenu.setGroupVisible(0, true);
                    MainMenu.setGroupVisible(1, true);
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

                    if (!TextUtils.isEmpty(editable.toString())) {
                        MemoryRecyclerView.setVisibility(View.VISIBLE);
                        ShowSearchResults(editable.toString());
                    } else {
                        SearchedAlbums.clear();
                        MemoryRecyclerView.removeAllViews();
                        onStart();
                    }

                }
            });

        } else if (item.getItemId() == 1) {

            if (IsConnectedToNet()) {
                new BottomSheet.Builder(this).title(" Options").sheet(R.menu.main_menu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.upload_activity:
                                startActivity(new Intent(MainActivity.this, integrals.inlens.GridView.MainActivity.class));
                                break;
                            case R.id.profile_pic:
                                DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild("Name")) {
                                            String dbname = dataSnapshot.child("Name").getValue().toString();
                                            ProfileuserName.setText(dbname);

                                        } else {
                                            ProfileuserName.setText("-NA-");
                                        }
                                        if (dataSnapshot.hasChild("Profile_picture")) {
                                            String image = dataSnapshot.child("Profile_picture").getValue().toString();

                                            if (!TextUtils.isEmpty(image)) {
                                                RequestOptions requestOptions = new RequestOptions()
                                                        .fitCenter();

                                                Glide.with(MainActivity.this)
                                                        .load(image)
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

                                            } else if (image.equals("default")) {

                                                Glide.with(MainActivity.this).load(R.drawable.ic_account_200dp).into(UserImage);
                                            } else {
                                                Glide.with(MainActivity.this).load(R.drawable.ic_account_200dp).into(UserImage);
                                            }
                                        } else {
                                            Glide.with(MainActivity.this).load(R.drawable.ic_account_200dp).into(UserImage);
                                        }
                                        if (dataSnapshot.hasChild("Email")) {

                                            String dbemail = dataSnapshot.child("Email").getValue().toString();
                                            ProfileUserEmail.setText(String.format("Email : %s", dbemail));
                                        } else {
                                            ProfileUserEmail.setText("Email : -NA-");
                                        }

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


                            case R.id.restart_service: {
                                SharedPreferences sharedPreferencesS = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                                if (sharedPreferencesS.getBoolean("UsingCommunity::", false)) {
                                    startService(getApplicationContext(), new Intent(getApplicationContext(), RecentImageService.class));
                                }

                            }
                            break;
                            case R.id.create_issues: {
                                startActivity(new Intent(MainActivity.this, IssueActivity.class));
                                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
                                finish();
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

    private void ShowAllAlbums() {
        MainLoadingProgressBar.setVisibility(View.VISIBLE);
        MemoryRecyclerView.setVisibility(View.GONE);

        InDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser).child("Communities");
        InDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SearchedAlbums.clear();
                MemoryRecyclerView.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String AlbumName = snapshot.child("AlbumTitle").getValue().toString();


                    final String AlbumKey = snapshot.getKey();
                    String AlbumCoverImage = snapshot.child("AlbumCoverImage").getValue().toString();
                    String PostedByProfilePic = snapshot.child("PostedByProfilePic").getValue().toString();
                    String AlbumDescription = snapshot.child("AlbumDescription").getValue().toString();
                    String DateandTime = "";
                    String User_ID = snapshot.child("User_ID").getValue().toString();
                    String UserName = snapshot.child("UserName").getValue().toString();
                    AlbumModel Album = new AlbumModel(AlbumCoverImage, AlbumDescription, AlbumName, PostedByProfilePic, DateandTime, UserName, User_ID);
                    SearchedAlbums.add(Album);
                    AlbumKeys.add(AlbumKey);

                }

                Collections.reverse(SearchedAlbums);
                Collections.reverse(AlbumKeys);

                if (AlbumKeys.size() == 0) {
                    NoAlbumTextView.setVisibility(View.VISIBLE);
                } else {
                    NoAlbumTextView.setVisibility(View.GONE);
                }


                MainAdapterForSearch = new MainSearchAdapter(getApplicationContext(), SearchedAlbums, AlbumKeys, FirebaseDatabase.getInstance().getReference().child("Communities"));
                MemoryRecyclerView.setAdapter(MainAdapterForSearch);
                MemoryRecyclerView.scrollToPosition(AlbumClickDetails.getInt("last_clicked_position", 0));
                MainLoadingProgressBar.setVisibility(View.GONE);
                MemoryRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ShowSearchResults(final String s) {

        MainLoadingProgressBar.setVisibility(View.VISIBLE);
        MemoryRecyclerView.setVisibility(View.GONE);

        InDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser).child("Communities");
        InDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SearchedAlbums.clear();
                MemoryRecyclerView.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String AlbumName = snapshot.child("AlbumTitle").getValue().toString();
                    if (AlbumName.toLowerCase().contains(s.toLowerCase())) {
                        final String AlbumKey = snapshot.getKey();
                        String AlbumCoverImage = snapshot.child("AlbumCoverImage").getValue().toString();
                        String PostedByProfilePic = snapshot.child("PostedByProfilePic").getValue().toString();
                        String AlbumDescription = snapshot.child("AlbumDescription").getValue().toString();
                        String DateandTime = "";
                        String User_ID = snapshot.child("User_ID").getValue().toString();
                        String UserName = snapshot.child("UserName").getValue().toString();
                        AlbumModel Album = new AlbumModel(AlbumCoverImage, AlbumDescription, AlbumName, PostedByProfilePic, DateandTime, UserName, User_ID);
                        SearchedAlbums.add(Album);
                        AlbumKeys.add(AlbumKey);
                    }
                }

                if (SearchedAlbums.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No albums found.", Toast.LENGTH_SHORT).show();
                }

                Collections.reverse(SearchedAlbums);
                Collections.reverse(AlbumKeys);

                MainAdapterForSearch = new MainSearchAdapter(getApplicationContext(), SearchedAlbums, AlbumKeys, FirebaseDatabase.getInstance().getReference().child("Communities"));
                MemoryRecyclerView.setAdapter(MainAdapterForSearch);
                MainLoadingProgressBar.setVisibility(View.GONE);
                MemoryRecyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        stopService(new Intent(getApplicationContext(), OreoService.class));
                                    } else {
                                        jobSchedulerHelper.stopJobScheduler();
                                        stopService(new Intent(getApplicationContext(), RecentImageService.class));

                                    }
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
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        stopService(new Intent(getApplicationContext(), OreoService.class));
                    } else {
                        stopService(new Intent(getApplicationContext(), RecentImageService.class));
                        jobSchedulerHelper.stopJobScheduler();
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Toast.makeText(getApplicationContext(), "Successfully left from the current Cloud-Album", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }


    private void AddToCloud(String substring, final ProgressBar progressBar) {
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
                                    AddingAlbumToReference.child("CreatedTimestamp").setValue(dataSnapshot.child("CreatedTimestamp").getValue().toString());
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
                            startService(getApplicationContext(), new Intent(getApplicationContext(), RecentImageService.class));

                        }

                    });
                    builder.create().show();
                    progressBar.setVisibility(View.INVISIBLE);


                } else {
                    Toast.makeText(getApplicationContext(), "Album time expired. You can't participate in this Cloud-Album.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, OreoService.class);
            serviceIntent.putExtra("inputExtra", "Ongoing InLens Recent-Image service.");
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            jobSchedulerHelper.startJobScheduler();
            context.startService(intent);
        }
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

        if (SEARCH_IN_PROGRESS) {
            MainMenu.setGroupVisible(0, true);
            MainMenu.setGroupVisible(1, true);
            SEARCH_IN_PROGRESS = false;
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            onStart();
        } else {
            super.onBackPressed();
        }
    }

    private class MainSearchAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

        Context context;
        List<AlbumModel> AlbumList;
        List<String> AlbumKeyIDs;
        DatabaseReference CommunityRef;

        public MainSearchAdapter(Context context, List<AlbumModel> albumList, List<String> albumKeyIDs, DatabaseReference communityRef) {
            this.context = context;
            AlbumList = albumList;
            AlbumKeyIDs = albumKeyIDs;
            CommunityRef = communityRef;
        }

        @NonNull
        @Override
        public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View SearchLayoutView = LayoutInflater.from(context).inflate(R.layout.cloud_album_card, parent, false);
            return new AlbumViewHolder(SearchLayoutView);
        }

        @Override
        public void onBindViewHolder(@NonNull final AlbumViewHolder holder, final int position) {

            holder.SetAlbumCover(getApplicationContext(), AlbumList.get(position).getAlbumCoverImage());
            holder.SetTitle(AlbumList.get(position).getAlbumTitle());
            holder.SetProfilePic(getApplicationContext(), AlbumList.get(position).getPostedByProfilePic());
            if (holder.AlbumContainer.isShown()) {


                holder.DetailsAlbumn.clearAnimation();
                holder.DetailsAlbumn.setAnimation(AlbumCardClose);
                holder.DetailsAlbumn.getAnimation().start();

                holder.AlbumCoverEditBtn.clearAnimation();
                holder.AlbumCoverEditBtn.setAnimation(AlbumCardClose);
                holder.AlbumCoverEditBtn.getAnimation().start();

                holder.ParticipantsAlbum.clearAnimation();
                holder.ParticipantsAlbum.setAnimation(AlbumCardClose);
                holder.ParticipantsAlbum.getAnimation().start();

                holder.DetailsAlbumn.setVisibility(View.INVISIBLE);
                holder.AlbumCoverEditBtn.setVisibility(View.INVISIBLE);
                holder.ParticipantsAlbum.setVisibility(View.INVISIBLE);

            } else {


                holder.DetailsAlbumn.clearAnimation();
                holder.DetailsAlbumn.setAnimation(AlbumCardOpen);
                holder.DetailsAlbumn.getAnimation().start();

                holder.AlbumCoverEditBtn.clearAnimation();
                holder.AlbumCoverEditBtn.setAnimation(AlbumCardOpen);
                holder.AlbumCoverEditBtn.getAnimation().start();

                holder.ParticipantsAlbum.clearAnimation();
                holder.ParticipantsAlbum.setAnimation(AlbumCardOpen);
                holder.ParticipantsAlbum.getAnimation().start();

                holder.DetailsAlbumn.setVisibility(View.VISIBLE);
                holder.AlbumCoverEditBtn.setVisibility(View.VISIBLE);
                holder.ParticipantsAlbum.setVisibility(View.VISIBLE);

            }


            holder.DetailsAlbumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CloseFabs();

                    AlbumTitle.setText(String.format("Album Title : %s", AlbumList.get(position).getAlbumTitle()));
                    AlbumDesc.setText(String.format("Album About : %s", AlbumList.get(position).getAlbumDescription()));
                    AlbumOwner.setText("Created By : " + AlbumList.get(position).getUserName());


                    CommunityRef.child(AlbumKeyIDs.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            PostCount = 0;
                            MemberCount = 0;

                            if (dataSnapshot.hasChild("CommunityPhotographer")) {
                                for (DataSnapshot PostSnapShot : dataSnapshot.child("CommunityPhotographer").getChildren()) {
                                    MemberCount++;
                                }
                            }

                            if (dataSnapshot.hasChild("Situations")) {
                                for (DataSnapshot PostSnapShot : dataSnapshot.child("Situations").getChildren()) {
                                    PostCount++;
                                }
                            }

                            AlbumMemberCount.setText(String.format("Total Members : %d", MemberCount));
                            AlbumPostCount.setText(String.format("Total Situations : %d", PostCount));

                            if (dataSnapshot.hasChild("ActiveIndex")) {
                                if (dataSnapshot.child("ActiveIndex").getValue().toString().equals("T")) {
                                    if (dataSnapshot.hasChild("AlbumExpiry")) {
                                        String DateEnd = "Event expires on : " + dataSnapshot.child("AlbumExpiry").getValue().toString() + " @ 11:59 PM";
                                        AlbumEndTime.setText(DateEnd);
                                    } else {
                                        String DateEnd = "Data not available";
                                        AlbumEndTime.setText(String.format("Album End Time : %s", DateEnd));
                                    }
                                } else {
                                    if (dataSnapshot.hasChild("AlbumExpiry")) {
                                        String DateEnd = "Event expired on :" + dataSnapshot.child("AlbumExpiry").getValue().toString() + " @ 11:59 PM";
                                        AlbumEndTime.setText(DateEnd);
                                    } else {
                                        String DateEnd = "Data not available";
                                        AlbumEndTime.setText(String.format("Album End Time : %s", DateEnd));
                                    }
                                }
                            } else {
                                String DateEnd = "Data not available";
                                AlbumEndTime.setText(String.format("Album End Time : %s", DateEnd));
                            }


                            if (dataSnapshot.hasChild("CreatedTimestamp")) {
                                String timestamp = dataSnapshot.child("CreatedTimestamp").getValue().toString();
                                long time = Long.parseLong(timestamp);
                                CharSequence Time = DateUtils.getRelativeDateTimeString(getApplicationContext(), time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
                                String timesubstring = Time.toString().substring(Time.length() - 8);
                                Date date = new Date(time);
                                String dateformat = DateFormat.format("dd-MM-yyyy", date).toString();
                                String DateandTime = "Event started on : " + dateformat + " @ " + timesubstring;
                                AlbumStartTime.setText(DateandTime);
                            } else if (dataSnapshot.hasChild("Time")) {
                                String DateandTime = dataSnapshot.child("Time").getValue().toString();
                                AlbumStartTime.setText(DateandTime);
                            } else {
                                String DateEnd = "Data not available";
                                AlbumEndTime.setText(String.format("Album Start Time : %s", DateEnd));
                            }

                            if (dataSnapshot.hasChild("AlbumType")) {
                                String EventType = dataSnapshot.child("AlbumType").getValue().toString();
                                AlbumType.setText("Event Type : " + EventType);
                            } else {
                                String EventType = "Data not available";
                                AlbumType.setText("Event Type : " + EventType);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    DetailsDialog.show();

                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CloseFabs();
                    getSupportActionBar().setDisplayShowCustomEnabled(false);

                    final String PostKey = AlbumKeyIDs.get(position);
                    if (!TextUtils.isEmpty(PostKey)) {
                        try {

                            SharedPreferences.Editor AlbumEditor = AlbumClickDetails.edit();
                            AlbumEditor.putInt("last_clicked_position", position);
                            AlbumEditor.apply();

                            startActivity(new Intent(MainActivity.this, CloudAlbum.class)
                                    .putExtra("AlbumName", AlbumList.get(position).getAlbumTitle())
                                    .putExtra("GlobalID::", PostKey)
                                    .putExtra("LocalID::", PostKey)
                                    .putExtra("UserID::", CurrentUser));

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                }
            });

            holder.AlbumCoverEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CloseFabs();
                    AlbumCoverEditprogressBar.setVisibility(View.VISIBLE);
                    AlbumCoverEditProfileuserName.setText(AlbumList.get(position).getAlbumTitle());
                    AlbumCoverEditProfileUserEmail.setTextSize(13);
                    AlbumCoverEditProfileUserEmail.setText(String.format("Change Cover for the album \" %s \"", AlbumList.get(position).getAlbumTitle()));

                    PostKeyForEdit = AlbumKeyIDs.get(position);
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

            holder.ParticipantsAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CloseFabs();
                    ParticpantsBottomSheetDialogProgressbar.setVisibility(View.VISIBLE);
                    PostKeyForEdit = AlbumKeyIDs.get(position);
                    DisplayAllParticipantsAsBottomSheet(PostKeyForEdit, FirebaseDatabase.getInstance().getReference());

                }
            });

            MainLoadingProgressBar.setVisibility(View.INVISIBLE);
            MemoryRecyclerView.setVisibility(View.VISIBLE);

        }

        @Override
        public int getItemCount() {
            return AlbumList.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences AlbumClickDetails = getSharedPreferences("LastClickedAlbum", MODE_PRIVATE);
        SharedPreferences.Editor AlbumEditor = AlbumClickDetails.edit();
        AlbumEditor.putInt("last_clicked_position", 0);
        AlbumEditor.apply();
    }

    private void QRCodeInit() {

        QRCodeDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        QRCodeDialog.setCancelable(false);
        QRCodeDialog.setCanceledOnTouchOutside(false);
        QRCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        QRCodeDialog.setContentView(R.layout.activity_qrcode_generator);
        QRCodeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        QRCodeDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window QRCodewindow = QRCodeDialog.getWindow();
        QRCodewindow.setGravity(Gravity.BOTTOM);
        QRCodewindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        QRCodewindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        QRCodewindow.setDimAmount(0.75f);
        QRCodewindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        CurrentDatabase currentDatabase = new CurrentDatabase(getApplicationContext(), "", null, 1);
        final String QRCommunityID = currentDatabase.GetLiveCommunityID();
        currentDatabase.close();

        Button InviteLinkButton = QRCodeDialog.findViewById(R.id.InviteLinkButton);
        String QRPhotographerID = QRCommunityID;

        ImageButton QRCodeCloseBtn = QRCodeDialog.findViewById(R.id.QR_dialog_closebtn);
        QRCodeCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QRCodeDialog.dismiss();

            }
        });
        TextView textView = QRCodeDialog.findViewById(R.id.textViewAlbumQR);
        ImageView QRCodeImageView = QRCodeDialog.findViewById(R.id.QR_Display);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(QRPhotographerID, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            QRCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            QRCodeImageView.setVisibility(View.INVISIBLE);
            textView.setText("You must be in an album to generate QR code");
        } catch (NullPointerException e) {
            QRCodeImageView.setVisibility(View.INVISIBLE);
            textView.setText("You must be in an album to generate QR code");

        }
        InviteLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent SharingIntent = new Intent(Intent.ACTION_SEND);
                SharingIntent.setType("text/plain");
                String CommunityPostKey = QRCommunityID;
                SharingIntent.putExtra(Intent.EXTRA_TEXT, "InLens Cloud-Album Invite Link \n\n" + GenarateDeepLinkForInvite(CommunityPostKey));
                startActivity(SharingIntent);

            }
        });
    }


    private static String GenarateDeepLinkForInvite(String CommunityID) {
        return "https://inlens.page.link/?link=https://integrals.inlens.in/comid=" + CommunityID + "/&apn=integrals.inlens";
    }

}


