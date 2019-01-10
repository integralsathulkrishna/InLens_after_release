package integrals.inlens.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vistrav.ask.Ask;

import integrals.inlens.MainActivity;
import integrals.inlens.R;

public class LoginActivity extends AppCompatActivity {
    private EditText          EmailField;
    private EditText          PassWordField;
    private Button            LoginButton;
    private FirebaseAuth      InAuth;
    private DatabaseReference InDatabaseUser;
    private ProgressDialog    InProgressDialogue;
    private FirebaseAuth      InAuthentication;
    private FirebaseUser      firebaseUser;
    private TextView          ForgotPassword;
    private RelativeLayout RootForLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RootForLoginActivity = findViewById(R.id.root_for_login_activity);


        EmailField=(EditText)findViewById(R.id.EmailEditText);
        PassWordField=(EditText)findViewById(R.id.PasswordField);
        LoginButton=(Button)findViewById(R.id.LogInButton);
        ForgotPassword=(TextView)findViewById(R.id.ForgotPassword);
        InAuth=FirebaseAuth.getInstance();
        InDatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        InDatabaseUser.keepSynced(true);

        InProgressDialogue=new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckLogIn();
            }
        });
        SharedPreferences sharedPreferences=getSharedPreferences("ProfileUploadIndex.pref",MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("ProfileUploadIndex::",false);
        editor.commit();

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPassword.class));
            }
        });


    }

/*Error fix 5
    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 123);
        }
    }
*/
    private void CheckLogIn() {

        String Email=EmailField.getText().toString().trim();
        String PassWord=PassWordField.getText().toString().trim();
        if(!TextUtils.isEmpty(Email)&&!TextUtils.isEmpty(PassWord)){
            InProgressDialogue.setMessage("Loging In....");
            InProgressDialogue.show();
            InProgressDialogue.setCancelable(false);
            InAuth.signInWithEmailAndPassword(Email,PassWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if(firebaseUser!=null && firebaseUser.isEmailVerified())
                        {
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
                            finish();
                        }
                        else
                        {
                            DisplaySnackBar("Verify Email Address");
                        }

                        InProgressDialogue.dismiss();

                    }
                    else{
                        InProgressDialogue.dismiss();
                        DisplaySnackBar("Email or password authentication failed");
                    }
                }
            });
        }
        else
        {
            if(TextUtils.isEmpty(Email))
            {
                DisplaySnackBar("Please enter your email.");
            }
            else
            {
                DisplaySnackBar("Please enter your password.");

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            startActivity(new Intent(this,IntroActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent(this,IntroActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void DisplaySnackBar(String message)
    {
        Snackbar.make(RootForLoginActivity,message,Snackbar.LENGTH_SHORT).show();
    }

}
