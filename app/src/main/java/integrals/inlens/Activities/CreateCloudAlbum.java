package integrals.inlens.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.Calendar;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.InLensJobScheduler.InLensJobScheduler;
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
    private DatabaseReference                   photographerReference;
    private String                              UserID;
    private Boolean                             PhotographerCreated = false;
    private ProgressBar                         UploadProgress;
    private TextView                            UploadProgressTextView;
    private Boolean                             OngoingTask =      false;
    private static final int                    GALLERY_PICK=1 ;
    private static final int                    JOB_ID=7907;
    private JobScheduler                        jobScheduler;
    private JobInfo                             jobInfo;
    private Boolean                             CloudAlbumDone=false;
    private TextView                            DateofCompletion;
    private String                              date;
    private String                              AlbumTime;
    private DatePickerDialog.OnDateSetListener  dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cloud_album);
        Boolean Default = false;
        getSupportActionBar().hide();
        InAuthentication = FirebaseAuth.getInstance();
        InUser = InAuthentication.getCurrentUser();
        CommunityDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Communities");
        InUserReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(InUser.getUid());
        UserID = InUser.getUid();
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
                         android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                         dateSetListener,
                         year,month,day
                 );
                 dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                 dialog.show();
             }
         });

         dateSetListener = new DatePickerDialog.OnDateSetListener() {
             @Override
             public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                 month=month+1;
                 AlbumTime = day + "/" + month + "/" + year;
                 DateofCompletion.setText("Album Active until " + AlbumTime + " midnight");
                 DateofCompletion.setTextSize(12);

             }
         };






         ComponentName componentName= new ComponentName(this, InLensJobScheduler.class);
         JobInfo.Builder builder= new JobInfo.Builder(JOB_ID,componentName);
         builder.setPeriodic(15*60*1000);
         builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
         builder.setPersisted(true);
         jobInfo=builder.build();
         jobScheduler=(JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);







        DatabaseTimeTaken = calender.get(Calendar.HOUR_OF_DAY) + " : "
                + calender.get(Calendar.MINUTE) + " : "
                + "        " + calender.get(Calendar.DAY_OF_MONTH) + "/"
                + calender.get(Calendar.MONTH) + 1 + "/"
                + calender.get(Calendar.YEAR);
        DisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OngoingTask==false) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setAspectRatio((int) 36,20)
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
                            .setAspectRatio((int) 36,24)
                            .setFixAspectRatio(true)
                            .start(CreateCloudAlbum.this);
                                       }
                }
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostingStarts();
            }
        });

    }

    private void PostingStarts() {
        final String TitleValue = CommunityAlbumTitle.getText().toString().trim();
        final String DescriptionValue = CommunityAlbumDescription.getText().toString().trim();
        if (!TextUtils.isEmpty(TitleValue) && (!TextUtils.isEmpty(DescriptionValue) &&
                (!TextUtils.isEmpty(AlbumTime)) && ImageUri != null)) {
            StorageReference
                    FilePath = PostStorageReference
                    .child("CommunityCoverPhoto")
                    .child(ImageUri.getLastPathSegment());
            FilePath
                    .putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri DownloadUri = taskSnapshot.getDownloadUrl();
                            final DatabaseReference CommunityPost = CommunityDatabaseReference.push();
                            final DatabaseReference NewPost = PostDatabaseReference.push();
                            InUserReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    CommunityPost.child("AlbumTitle").setValue(TitleValue);
                                    CommunityPost.child("AlbumDescription").setValue(DescriptionValue);
                                    CommunityPost.child("AlbumCoverImage").setValue((DownloadUri).toString());
                                    CommunityPost.child("User_ID").setValue(InUser.getUid());
                                    CommunityPost.child("PostedByProfilePic").setValue(dataSnapshot.child("Profile_picture").getValue());
                                    CommunityPost.child("UserName").setValue(dataSnapshot.child("Name").getValue());
                                    CommunityPost.child("Time").setValue(DatabaseTimeTaken);
                                    PostKey = CommunityPost.getKey().toString().trim();
                                    NewPost.child("AlbumTitle").setValue(TitleValue);
                                    NewPost.child("AlbumDescription").setValue(DescriptionValue);
                                    NewPost.child("AlbumCoverImage").setValue((DownloadUri).toString());
                                    NewPost.child("User_ID").setValue(InUser.getUid());
                                    NewPost.child("PostedByProfilePic").setValue(dataSnapshot.child("Profile_picture").getValue());
                                    NewPost.child("UserName").setValue(dataSnapshot.child("Name").getValue());
                                    NewPost.child("Time").setValue(DatabaseTimeTaken);
                                    NewPost.child("CommunityID").setValue(PostKey);
                                    InProgressDialog.setMessage("Saving new data....");
                                    CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
                                    currentDatabase.InsertUploadValues(PostKey,0,1,0);
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
                                        PhotographerCreated = true;


                                    } else {
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

                    if(task.isSuccessful()) {
                         OngoingTask = false;
                         InProgressDialog.dismiss();
                        if (CloudAlbumDone == false) {
                            setIntent(null);
                            UploadProgressTextView.setText("Cloud Album Created.");
                            UploadProgress.setVisibility(View.INVISIBLE);
                            SubmitButton.setVisibility(View.VISIBLE);
                                                finish();
                                                startActivity(new Intent(CreateCloudAlbum.this, MainActivity.class));

                            StartServices();
                            CloudAlbumDone = true;
                        }
                    }
                    else
                        {
                        Toast.makeText(getApplicationContext(),"Sorry Cloud-Album Creation failed,Please try again ",Toast.LENGTH_SHORT).show();
                        }




                }
            });
        } else {
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
         jobScheduler.schedule(jobInfo);
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
        else
            {
            finish();
            }
    }
}