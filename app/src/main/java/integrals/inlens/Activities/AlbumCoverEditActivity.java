package integrals.inlens.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import integrals.inlens.MainActivity;
import integrals.inlens.R;

public class AlbumCoverEditActivity extends AppCompatActivity {

    private String AlbumKey;
    private DatabaseReference CommunityDatabaseReference , UserRef;
    private StorageReference StorageRef;
    private ImageView CoverImage;
    private ProgressBar CoverEditProgressBar;
    private Button DoneEdit;
    private static final int GALLERY_PICK=1 ;
    private Uri ImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_cover_edit);

        CoverImage = findViewById(R.id.CoverPhoto);
        DoneEdit = findViewById(R.id.DoneButton);
        CoverEditProgressBar = findViewById(R.id.UploadProgress);

        AlbumKey = getIntent().getStringExtra("Albumkey");
        CommunityDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Communities");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Communities");
        StorageRef = FirebaseStorage.getInstance().getReference();


        CommunityDatabaseReference.child(AlbumKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("AlbumCoverImage").getValue().toString();
                if(!TextUtils.isEmpty(image))
                {
                    RequestOptions requestOptions=new RequestOptions()
                            .centerCrop()
                            .override(176,176);

                    Glide.with(getApplicationContext())
                            .load(image)
                            .thumbnail(0.1f)
                            .apply(requestOptions)
                            .into(CoverImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        CoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio((int) 36,20)
                        .setFixAspectRatio(true)
                        .start(AlbumCoverEditActivity.this);
            }
        });

        DoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ImageUri!=null)
                {
                    DoneEdit.setVisibility(View.GONE);
                    CoverEditProgressBar.setVisibility(View.VISIBLE);

                    StorageReference
                            FilePath = StorageRef
                            .child("CommunityCoverPhoto")
                            .child(ImageUri.getLastPathSegment());

                    FilePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                final String DownloadUrl = task.getResult().getDownloadUrl().toString();
                                CommunityDatabaseReference.child(AlbumKey).child("AlbumCoverImage").setValue(DownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> finaltask) {

                                        if(finaltask.isSuccessful())
                                        {
                                            UserRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                                    {
                                                        String key = snapshot.getKey();
                                                        String albumkey = snapshot.child("CommunityID").getValue().toString();
                                                        if(albumkey.equals(AlbumKey))
                                                        {
                                                            UserRef.child(key).child("AlbumCoverImage").setValue(DownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> qtask) {

                                                                    if(qtask.isSuccessful())
                                                                    {
                                                                        Toast.makeText(AlbumCoverEditActivity.this,"Successfully uploaded new cover.",Toast.LENGTH_LONG).show();
                                                                        CoverEditProgressBar.setVisibility(View.INVISIBLE);
                                                                        onBackPressed();

                                                                    }
                                                                    else
                                                                    {
                                                                        CoverEditProgressBar.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(AlbumCoverEditActivity.this,"Failed to upload new cover. Try again later.",Toast.LENGTH_LONG).show();

                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                        else
                                        {
                                            CoverEditProgressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(AlbumCoverEditActivity.this,"Failed to upload new cover. Try again later.",Toast.LENGTH_LONG).show();

                                        }

                                    }
                                });
                            }
                            else
                            {
                                CoverEditProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AlbumCoverEditActivity.this,"Failed to upload new cover. Try again later.",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(CoverEditProgressBar.isShown())
        {
            Toast.makeText(AlbumCoverEditActivity.this,"Wait till upload is complete.",Toast.LENGTH_LONG).show();

        }
        else
        {
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Flow();
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
                ImageUri = result.getUri();
                CoverImage.setImageURI(ImageUri);
            }
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(getApplicationContext(),"Crop failed. ",Toast.LENGTH_SHORT).show();
        }


    }








    private void Flow(){
        finish();
        startActivity(new Intent(AlbumCoverEditActivity.this,MainActivity.class));
        }



}
