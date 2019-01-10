package integrals.inlens.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.MainActivity;
import integrals.inlens.R;
import integrals.inlens.Services.RecentImageService;


public class CreateCloudAlbum extends AppCompatActivity {
    private ImageView                           SetPostImage;
    private EditText                            CommunityAlbumTitle;
    private EditText                            CommunityAlbumDescription;
    private Button                              SubmitButton;
    private ImageButton                         DisplayButton;
    private Uri                                 ImageUri;
    private DatabaseReference                   PostDatabaseReference;
    private DatabaseReference                   CommunityDatabaseReference;
    private StorageReference                    PostStorageReference;
    private static final int                    GALLERY_REQUEST = 3;
    private ProgressDialog                      InProgressDialog;
    private FirebaseAuth                        InAuthentication;
    private FirebaseUser                        InUser;
    private DatabaseReference                   InUserReference;
    private String                              PostKey;
    private String                              DatabaseTimeTaken;
    private DatabaseReference                   photographerReference,databaseReference,ComNotyRef;
    private String                              UserID;
    private Boolean                             PhotographerCreated = false;
    private ProgressBar                         UploadProgress;
    private TextView                            UploadProgressTextView;
    private Boolean                             OngoingTask =      false;
    private static final int                    GALLERY_PICK=1 ;
    private Boolean                             CloudAlbumDone=false;
    private TextView                            DateofCompletion;
    private String                              date;
    private String                              AlbumTime;
    private DatePickerDialog.OnDateSetListener  dateSetListener;
    private Calendar calendar;
    private TextView EventPicker ;
    private Dialog EventDialog,QRCodeDialog;
    private String EventType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cloud_album);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EventDialogInit();

        InAuthentication = FirebaseAuth.getInstance();
        InUser = InAuthentication.getCurrentUser();
        CommunityDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Communities");
        InUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(InUser.getUid());
        UserID = InUser.getUid();

        EventPicker = findViewById(R.id.EventTypeText);
        DisplayButton = (ImageButton) findViewById(R.id.DisplayImage);
        UploadProgressTextView = (TextView) findViewById(R.id.UploadProgressTextView);
        CommunityAlbumTitle = (EditText) findViewById(R.id.AlbumTitleEditText);
        CommunityAlbumDescription = (EditText) findViewById(R.id.AlbumDescriptionEditText);
        SubmitButton = (Button) findViewById(R.id.DoneButton);
        SetPostImage = (ImageView) findViewById(R.id.CoverPhoto);
        UploadProgress = (ProgressBar) findViewById(R.id.UploadProgress);
        PostStorageReference = FirebaseStorage.getInstance().getReference();
        PostDatabaseReference = InUserReference.child("Communities");
        InProgressDialog = new ProgressDialog(this);
        Calendar calender = Calendar.getInstance();
        //Created By Elson Jose
        DateofCompletion = findViewById(R.id.TimeEditText);
        DateofCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateCloudAlbum.this,
                        dateSetListener,
                        year,month,day
                );
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month=month+1;
                AlbumTime = day + "-" + month + "-" + year;
                DateofCompletion.setText("Album Active until " + AlbumTime + " midnight");
                DateofCompletion.setTextSize(12);

            }
        };



        int Month = calender.get(Calendar.MONTH);
        Month++;

        DatabaseTimeTaken = calender.get(Calendar.HOUR_OF_DAY)+":"
                + calender.get(Calendar.MINUTE)+""
                +"         "+calender.get(Calendar.DAY_OF_MONTH) + "/"
                + String.valueOf(Month) + "/"+calender.get(Calendar.YEAR)
        ;
        DisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OngoingTask==false) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio((int) 390,285)
                            .setFixAspectRatio(true)
                            .start(CreateCloudAlbum.this);
                }

            }
        });
        SetPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(OngoingTask==false) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio((int) 390,285)
                            .setFixAspectRatio(true)
                            .start(CreateCloudAlbum.this);
                }
            }
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitButton.setEnabled(false);
                PostingStarts();
            }
        });

        EventPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventDialog.show();

            }
        });

    }

    private void EventDialogInit() {

        EventDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar);
        EventDialog.setCancelable(true);
        EventDialog.setCanceledOnTouchOutside(false);
        EventDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        EventDialog.setContentView(R.layout.event_type_layout);
        EventDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window EventDialogwindow = EventDialog.getWindow();
        EventDialogwindow.setGravity(Gravity.BOTTOM);
        EventDialogwindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        EventDialogwindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        EventDialogwindow.setDimAmount(0.75f);

        final RadioButton EventWedding = EventDialog.findViewById(R.id.event_type_wedding_btn);
        final RadioButton EventCeremony = EventDialog.findViewById(R.id.event_type_ceremony_btn);
        final RadioButton EventOthers = EventDialog.findViewById(R.id.event_type_others_btn);
        final RadioButton EventParty = EventDialog.findViewById(R.id.event_type_party_btn);
        final RadioButton EventTravel = EventDialog.findViewById(R.id.event_type_travel_btn);
        final RadioButton EventHangout = EventDialog.findViewById(R.id.event_type_hangouts_btn);

        ImageButton EventTypeDone  = EventDialog.findViewById(R.id.event_done_btn);

        EventTypeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(EventType))
                {
                    EventDialog.dismiss();
                    EventPicker.setText(String.format("Event Selected : %s", EventType));
                    EventPicker.setTextSize(12);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please select an event type.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        EventCeremony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetCheckFalse(EventWedding,EventOthers,EventParty,EventTravel,EventHangout);
                EventType = "Ceremony";
            }
        });

        EventWedding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetCheckFalse(EventCeremony,EventOthers,EventParty,EventTravel,EventHangout);
                EventType = "Wedding";
            }
        });

        EventOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetCheckFalse(EventWedding,EventCeremony,EventParty,EventTravel,EventHangout);
                EventType = "Others";
            }
        });

        EventParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetCheckFalse(EventWedding,EventOthers,EventCeremony,EventTravel,EventHangout);
                EventType = "Party";
            }
        });

        EventTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetCheckFalse(EventWedding,EventOthers,EventParty,EventCeremony,EventHangout);
                EventType = "Travel";
            }
        });

        EventHangout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetCheckFalse(EventWedding,EventOthers,EventParty,EventCeremony,EventParty);
                EventType = "Hangouts";
            }
        });


    }

    private void SetCheckFalse(RadioButton btn1, RadioButton btn2, RadioButton btn3, RadioButton btn4, RadioButton btn5) {

        btn1.setChecked(false);
        btn2.setChecked(false);
        btn3.setChecked(false);
        btn4.setChecked(false);
        btn5.setChecked(false);

    }

    private void PostingStarts() {
        final String TitleValue = CommunityAlbumTitle.getText().toString().trim();
        final String DescriptionValue = CommunityAlbumDescription.getText().toString().trim();
        if (!TextUtils.isEmpty(TitleValue) && !(TextUtils.isEmpty(EventType)) &&(!TextUtils.isEmpty(DescriptionValue) &&
                (!TextUtils.isEmpty(AlbumTime)))) {

            if(ImageUri==null)
            {
                Toast.makeText(CreateCloudAlbum.this,"Cover photo can be added later.",Toast.LENGTH_LONG).show();
                ImageUri= Uri.parse("android.resource://" + getPackageName() + "/drawable/image_avatar");
            }
            StorageReference
                    FilePath = PostStorageReference
                    .child("CommunityCoverPhoto")
                    .child(ImageUri.getLastPathSegment());
            FilePath
                    .putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri DownloadUri = taskSnapshot.getDownloadUrl();
                            String pushid = CommunityDatabaseReference.push().getKey();
                            final DatabaseReference CommunityPost = CommunityDatabaseReference.child(pushid);
                            final DatabaseReference NewPost = PostDatabaseReference.child(pushid);
                            InUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CommunityPost.child("AlbumTitle").setValue(TitleValue);
                                    CommunityPost.child("AlbumDescription").setValue(DescriptionValue);
                                    CommunityPost.child("AlbumCoverImage").setValue((DownloadUri).toString());
                                    CommunityPost.child("User_ID").setValue(InUser.getUid());
                                    CommunityPost.child("PostedByProfilePic").setValue(dataSnapshot.child("Profile_picture").getValue());
                                    CommunityPost.child("UserName").setValue(dataSnapshot.child("Name").getValue());
                                    CommunityPost.child("Time").setValue(DatabaseTimeTaken);
                                    CommunityPost.child("ActiveIndex").setValue("T");
                                    CommunityPost.child("AlbumExpiry").setValue(AlbumTime);
                                    CommunityPost.child("AlbumType").setValue(EventType);

                                    PostKey = CommunityPost.getKey().trim();

                                    NewPost.child("AlbumTitle").setValue(TitleValue);
                                    NewPost.child("AlbumDescription").setValue(DescriptionValue);
                                    NewPost.child("AlbumCoverImage").setValue((DownloadUri).toString());
                                    NewPost.child("User_ID").setValue(InUser.getUid());
                                    NewPost.child("PostedByProfilePic").setValue(dataSnapshot.child("Profile_picture").getValue());
                                    NewPost.child("UserName").setValue(dataSnapshot.child("Name").getValue());
                                    NewPost.child("Time").setValue(DatabaseTimeTaken);
                                    NewPost.child("CommunityID").setValue(PostKey);
                                    NewPost.child("CreatedTimestamp").setValue(ServerValue.TIMESTAMP);
                                    NewPost.child("AlbumType").setValue(EventType);

                                    InProgressDialog.setMessage("Saving new data....");
                                    CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
                                    currentDatabase.InsertUploadValues(PostKey,0,1,0,AlbumTime,1,1,"CUREE");
                                    currentDatabase.close();
                                    InProgressDialog.setMessage("Finishing....");
                                    if (PhotographerCreated == false) {
                                        photographerReference = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Communities")
                                                .child(PostKey)
                                                .child("CommunityPhotographer");

                                        DatabaseReference databaseReference = photographerReference.push();
                                        databaseReference.child("Photographer_UID").setValue(UserID);
                                        databaseReference.child("Name").setValue(dataSnapshot.child("Name").getValue());
                                        databaseReference.child("Profile_picture").setValue(dataSnapshot.child("Profile_picture").getValue());
                                        databaseReference.child("Email_ID").setValue(dataSnapshot.child("Email").getValue());
                                        CreateSituation();
                                        PhotographerCreated = true;

                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(CreateCloudAlbum.this, "Sorry database error ...please try again", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                 @Override
                                                 public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                     OngoingTask = true;
                                                     UploadProgressTextView.setVisibility(View.VISIBLE);
                                                     SubmitButton.setVisibility(View.INVISIBLE);
                                                     UploadProgress.setVisibility(View.VISIBLE);
                                                     double progress =
                                                             (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                                     .getTotalByteCount());
                                                     String UploadIndex = "Creating new Cloud-Album, "+ (int) progress + "%" + " completed.";
                                                     UploadProgressTextView.setText(UploadIndex);

                                                 }
                                             }
            ).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.isComplete()==true){
                            if(CloudAlbumDone==false) {
                                InProgressDialog.dismiss();
                                UploadProgressTextView.setText("Cloud Album Created.");
                                UploadProgress.setVisibility(View.INVISIBLE);
                                SubmitButton.setVisibility(View.VISIBLE);
                                CloudAlbumDone=true;
                                StartServices();
                                SharedPreferences AlbumClickDetails = getSharedPreferences("LastClickedAlbum",MODE_PRIVATE);
                                SharedPreferences.Editor  AlbumEditor = AlbumClickDetails.edit();
                                AlbumEditor.putInt("last_clicked_position",0);
                                AlbumEditor.apply();


                            }

                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),"Error Detected",Toast.LENGTH_SHORT).show();
                    SubmitButton.setEnabled(true);
                }
            });




        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Please fill up all the provided fields " +
                            "and add album cover photo ", Toast.LENGTH_SHORT).show();
        }
    }




    private void StartServices() {
        SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("UsingCommunity::", true);
        editor.commit();
        SharedPreferences sharedPreferences1 = getSharedPreferences("Owner.pref", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putBoolean("ThisOwner::", true);
        editor1.commit();
        startService(new Intent(CreateCloudAlbum.this, RecentImageService.class));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
            finish();

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                DisplayButton.setVisibility(View.INVISIBLE);
                ImageUri = result.getUri();
                SetPostImage.setImageURI(ImageUri);
            }
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(getApplicationContext(),"Crop failed. ",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {

        if (OngoingTask == true) {
            Toast.makeText(getApplicationContext(), "Creating your Cloud-Album. Please wait.", Toast.LENGTH_SHORT).show();
        }

    }



    private void CreateSituation()
    {

        CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
        final String CommunityID=currentDatabase.GetLiveCommunityID();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("Situations");
        ComNotyRef = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("CommunityPhotographer");
        calendar=Calendar.getInstance();
        String SituationTimeIntervel=calendar.get(Calendar.YEAR)+ "-"
                +calendar.get(Calendar.MONTH)+"-"
                +calendar.get(Calendar.DAY_OF_MONTH)+"T"
                +calendar.get(Calendar.HOUR_OF_DAY)+"-"
                +calendar.get(Calendar.MINUTE)+"-"
                +calendar.get(Calendar.SECOND);

        Map situationmap = new HashMap();
        situationmap.put("name","Event Started");
        situationmap.put("time", ServerValue.TIMESTAMP);
        situationmap.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String push_id =databaseReference.push().getKey();
        // Added by Athul Krishna For Implementation of Situation Upload
        situationmap.put("SituationKey",push_id);
        situationmap.put("SituationTime",SituationTimeIntervel);
        final Map member = new HashMap();
        member.put("memid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("ComNoty");
        ComNotyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String id = snapshot.child("Photographer_UID").getValue().toString();
                    dref.child(id).push().child("comid").setValue(CommunityID);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child(push_id).setValue(situationmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    databaseReference.child(push_id).child("members").push().setValue(member);
                    Toast.makeText(CreateCloudAlbum.this,"New Situation Created : "+"Event Started",Toast.LENGTH_SHORT).show();
                    CloudAlbumDone=false;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e.toString().contains("FirebaseNetworkException"))
                    Toast.makeText(CreateCloudAlbum.this,"Not Connected to Internet.",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(CreateCloudAlbum.this,"Unable to create new Situation.", Toast.LENGTH_SHORT).show();
            }
        });



       currentDatabase.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            startActivity(new Intent(CreateCloudAlbum.this,MainActivity.class).putExtra("QRCodeVisible",false));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent(CreateCloudAlbum.this,MainActivity.class).putExtra("QRCodeVisible",false));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}