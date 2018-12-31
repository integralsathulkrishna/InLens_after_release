package integrals.inlens.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import integrals.inlens.R;

public class IntroActivity extends AppCompatActivity {

    private TextView AppName , AppWelcome , AppTagLine , AppRights;
    private ImageView AppIcon;
    private Button SignIn ,SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);


        AppName = findViewById(R.id.appname);
        AppWelcome = findViewById(R.id.appwelcome);
        AppTagLine = findViewById(R.id.apptagline);
        AppRights = findViewById(R.id.appabout);
        AppIcon = findViewById(R.id.appicon);

        SignIn = findViewById(R.id.introsignin);
        SignUp = findViewById(R.id.introsignup);

        AppTagLine.setText("\" Event memories with your loved ones in a single Cloud-Album \"");

        Animation FadeIn  = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);

        AppName.setAnimation(FadeIn);
        AppWelcome.setAnimation(FadeIn);
        AppTagLine.setAnimation(FadeIn);
        SignUp.setAnimation(FadeIn);
        SignIn.setAnimation(FadeIn);
        AppRights.setAnimation(FadeIn);

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this,LoginActivity.class));
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(IntroActivity.this,RegisterUser.class));
            }
        });
    }


}
