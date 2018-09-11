package integrals.inlens.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import integrals.inlens.MainActivity;
import integrals.inlens.R;

//Need To Clear Notification for Invitation
// Created By Elson.................................................................................
public class SettingActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private TextView Settings_Image;
    private TextView  mSettings_Name;
    private TextView  mSetting_Email;
    private TextView  mSetings_Status;
    private ImageView imageButton;
    private static final int GALLERY_PICK=1 ;
    private TextView mText;
    public ImageView getImageButton() {
        return imageButton;
    }

    private StorageReference mStorageRef;
    private Boolean ProfileIndex=false;

    //mod ej////////////////////////////////////////////////////////////////////
    private Button Invite,Cancel;
    private DatabaseReference db , SenderRef , NotificationRef;
    private String AlbumName , rname ,rimage , ComID , sname , simage , MyId ;
    private ProgressBar progressBar;
    private Bundle Instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Instance = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Invite = findViewById(R.id.SendInviteBtn);
        Cancel = findViewById(R.id.CancelInviteBtn);

        imageButton = (ImageView) findViewById(R.id.image_changer);
        Settings_Image = (TextView) findViewById(R.id.setting_images);
        mSettings_Name = (TextView) findViewById(R.id.settings_display_name);
        mSetings_Status = (TextView) findViewById(R.id.photographer_name);
        mSetting_Email = (TextView) findViewById(R.id.settings_email);
        getSupportActionBar().setElevation(1);
        getSupportActionBar().setTitle("Setting up Profile Picture");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mText = (TextView) findViewById(R.id.DoneButtonUserSetting);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        MyId = getIntent().getStringExtra("id");
        db= FirebaseDatabase.getInstance().getReference().child("Invites");
        AlbumName = getIntent().getStringExtra("album");
        ComID = getIntent().getStringExtra("com");
        SenderRef = FirebaseDatabase.getInstance().getReference().child("Users");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");


        SenderRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                        sname = name;
                        simage = thumb;

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(TextUtils.isEmpty(MyId))
        {
            Invite.setVisibility(View.GONE);
            Cancel.setVisibility(View.GONE);


            mUserDatabase.child(current_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressBar.setVisibility(View.INVISIBLE);

                    String name = dataSnapshot.child("Name").getValue().toString();
                    final String image = dataSnapshot.child("Profile_picture").getValue().toString();
                    String status = dataSnapshot.child("bio").getValue().toString();
                    String email = dataSnapshot.child("Email").getValue().toString();
                    String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                    rimage = thumb;
                    rname = name;

                    mSettings_Name.setText(name);
                    mSetings_Status.setText(status);
                    mSetting_Email.setText(email);

                    if (!(image.equals("default"))) {


                        RequestOptions requestOptions=new RequestOptions()
                                .centerCrop()
                                .override(176,176);

                        Glide.with(getApplicationContext())
                                .load(image)
                                .thumbnail(0.1f)
                                .apply(requestOptions)
                                .into(imageButton);


                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {


            imageButton.setEnabled(false);
            mText.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Send Invite to");

            db.child("sent").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(MyId))
                            {
                                Cancel.setVisibility(View.VISIBLE);
                                Invite.setVisibility(View.GONE);
                            }
                            else
                            {
                                Cancel.setVisibility(View.GONE);
                                Invite.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            mUserDatabase.child(MyId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String name = dataSnapshot.child("Name").getValue().toString();
                    final String image = dataSnapshot.child("Profile_picture").getValue().toString();
                    String status = dataSnapshot.child("bio").getValue().toString();
                    String email = dataSnapshot.child("Email").getValue().toString();
                    String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                    rimage = thumb;
                    rname = name;


                    mSettings_Name.setText(name);
                    mSetings_Status.setText(status);
                    mSetting_Email.setText(email);

                    if (!(image.equals("default"))) {


                        RequestOptions requestOptions=new RequestOptions()
                                .centerCrop()
                                .override(176,176);

                        Glide.with(getApplicationContext())
                                .load(image)
                                .thumbnail(0.1f)
                                .apply(requestOptions)
                                .into(imageButton);


                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // deleted intent and one more call for set on click listener which called finish()

        mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingActivity.this);
            }
        });



        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.child("sent").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(MyId).removeValue();
                db.child("received").child(MyId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                Cancel.setVisibility(View.GONE);
            }
        });

        Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("UsingCommunity::",false) == true)
                {
                    final Map invite = new HashMap();
                    invite.put("time", ServerValue.TIMESTAMP);
                    invite.put("response","sent");
                    invite.put("album",AlbumName);
                    invite.put("name",rname);
                    invite.put("image",rimage);

                    final Map invitation = new HashMap();
                    invitation.put("time", ServerValue.TIMESTAMP);
                    invitation.put("response","requested");
                    invitation.put("album",AlbumName);
                    invitation.put("comID",ComID);
                    invitation.put("image",simage);
                    invitation.put("name",sname);




                    db.child("sent").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(MyId).
                            updateChildren(invite).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                NotificationRef.child(MyId).push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Toast.makeText(SettingActivity.this, "Sending Invite to "+rname, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    db.child("received").child(MyId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(invitation).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                                Toast.makeText(SettingActivity.this, "Invite Send to "+rname, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You need to be in a commnity to send request",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    @Override
    public void onBackPressed() {
        if(progressBar.isShown())
        {
            Toast.makeText(SettingActivity.this,"Wait till uploading is complete",Toast.LENGTH_LONG).show();
        }
        else if(!TextUtils.isEmpty(MyId))
        {
            super.onBackPressed();
        }

        else if(TextUtils.isEmpty(MyId))
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();

        }
        else
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);
            finish();
            ProfileIndex=true;

        }




        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressBar.setVisibility(View.VISIBLE);

                Uri resultUri = result.getUri();
                try {
                    InputStream stream = getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    imageButton.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                File thumb_filePath =new File(resultUri.getPath());
                final String current_u_i_d = mCurrentUser.getUid();
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

                        if(task.isSuccessful()){

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            com.google.firebase.storage.UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {


                                    String thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){


                                        Map update_Hashmap = new HashMap();
                                        update_Hashmap.put("Profile_picture",downloadUrl);
                                        update_Hashmap.put("thumb_image",thumb_downloadurl);

                                        mUserDatabase.child(current_u_i_d).updateChildren(update_Hashmap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(SettingActivity.this,"SUCCESSFULLY UPLOADED", Toast.LENGTH_LONG).show();

                                                            }
                                                        }else {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(SettingActivity.this,"FAILED TO SAVE TO DATABASE.MAKE SURE YOUR INTERNET IS CONNECTED AND TRY AGAIN.",Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                    }else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(SettingActivity.this,"FAILED TO UPLOAD THUMBNAIL",Toast.LENGTH_LONG).show();
                                    }

                                }

                            });
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SettingActivity.this,"FAILED TO UPLOAD", Toast.LENGTH_LONG).show();
                        }}



                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                progressBar.setVisibility(View.INVISIBLE);
            }





        }





    }



}