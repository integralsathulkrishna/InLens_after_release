package integrals.inlens.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import integrals.inlens.MainActivity;
import integrals.inlens.R;

public class DisplayPhotographer extends AppCompatActivity {
    private TextView PhotographerName;
    private TextView PhotographerEmail;
    private ImageView PhotographerProfilePic;
    private String Name,Email,ProfilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photographer);
        Name=getIntent().getExtras().getString("USERNAME");
        Email=getIntent().getExtras().getString("EMAIL_ID");
        ProfilePic=getIntent().getExtras().getString("PROFILE_PIC");
        PhotographerName=(TextView)findViewById(R.id.photographer_name);
        PhotographerEmail=(TextView)findViewById(R.id.photographer_email);
        PhotographerProfilePic=(ImageView)findViewById(R.id.image_profile_pic);
        PhotographerName.setText(Name);
        PhotographerEmail.setText(Email);

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestOptions requestOptions= new RequestOptions()
                .override(300,300);
        Glide.with(getApplicationContext())
                .load(ProfilePic)
                .apply(requestOptions)
                .into(PhotographerProfilePic);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Flow();
    }

    private void Flow(){
        finish();
        startActivity(new Intent(DisplayPhotographer.this,MainActivity.class));
    }

}
