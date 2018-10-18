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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import integrals.inlens.MainActivity;
import integrals.inlens.R;

public class CoverEditActivity extends AppCompatActivity {

    private String PostKeyForEdit;
    private ImageView CoverImage;
    private Button UploadBtn;
    private static final int GALLERY_PICK=1 ;
    private Uri ImageUri = null;
    private StorageReference StorageRef;
    private ProgressBar HorizontalPbar;
    private TextView ProgressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_edit);

        PostKeyForEdit = getIntent().getStringExtra("PostKey");
        CoverImage = findViewById(R.id.covereditimgview);
        UploadBtn = findViewById(R.id.uploadbtn);
        UploadBtn.setVisibility(View.GONE);
        HorizontalPbar = findViewById(R.id.horizontalprogressbar);
        ProgressText = findViewById(R.id.ptext);

        StorageRef = FirebaseStorage.getInstance().getReference();


        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Communities")
                .child(PostKeyForEdit)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String Image = dataSnapshot.child("AlbumCoverImage").getValue().toString();
                        RequestOptions requestOptions=new RequestOptions()
                                .placeholder(R.drawable.image_avatar)
                                .fitCenter();

                        Glide.with(getApplicationContext())
                                .load(Image)
                                .thumbnail(0.1f)
                                .apply(requestOptions)
                                .into(CoverImage);

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
                        .setAspectRatio((int) 36,20)
                        .setFixAspectRatio(true)
                        .start(CoverEditActivity.this);
            }
        });

        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadCOverPhoto(ImageUri);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageUri = result.getUri();
                CoverImage.setImageURI(ImageUri);
                if(ImageUri!=null)
                {
                    UploadBtn.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(getApplicationContext(),"Crop failed. ",Toast.LENGTH_SHORT).show();
        }


    }


    private void UploadCOverPhoto(Uri imageUri) {

        HorizontalPbar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(PostKeyForEdit) && imageUri!=null)
        {

            StorageReference
                    FilePath = StorageRef
                    .child("CommunityCoverPhoto")
                    .child(imageUri.getLastPathSegment());

            FilePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                        FirebaseDatabase.getInstance().getReference().child("Communities").child(PostKeyForEdit)
                                .child("AlbumCoverImage")
                                .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(CoverEditActivity.this,"Changed in Communities",Toast.LENGTH_LONG).show();
                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Communities")
                                            .child(PostKeyForEdit)
                                            .child("AlbumCoverImage")
                                            .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(CoverEditActivity.this,"Changed in User Profile",Toast.LENGTH_LONG).show();
                                                Toast.makeText(CoverEditActivity.this,"Successfully uploaded new cover photo.",Toast.LENGTH_LONG).show();
                                                HorizontalPbar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    HorizontalPbar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(CoverEditActivity.this,"Unable to perform to change cover now.",Toast.LENGTH_LONG).show();

                                }

                            }
                        });
                    }
                    else
                    {
                        HorizontalPbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CoverEditActivity.this,"Unable to perform to change cover now.",Toast.LENGTH_LONG).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    HorizontalPbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CoverEditActivity.this,"Unable to perform to change cover now.",Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress =
                            (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());

                    ProgressText.setText("Uploaded "+String.valueOf((int)progress)+"%. Please wait till upload is complete.");
                    HorizontalPbar.setIndeterminate(false);
                    HorizontalPbar.setProgress((int)progress);


                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        ProgressText.setVisibility(View.GONE);
                    }
                }
            });

        }
        else
        {
            HorizontalPbar.setVisibility(View.INVISIBLE);
            Toast.makeText(CoverEditActivity.this,"Unable to perform to change cover now.",Toast.LENGTH_LONG).show();
        }

        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        if(HorizontalPbar.isShown())
        {
            Toast.makeText(CoverEditActivity.this,"Uploading cover photo. Please wait.",Toast.LENGTH_LONG).show();
        }
        else
        {
            super.onBackPressed();
        }
    }
}
