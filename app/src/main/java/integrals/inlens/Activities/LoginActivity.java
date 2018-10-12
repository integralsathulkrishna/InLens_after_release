package integrals.inlens.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
    private Button            CreateAccountButton;
    private ProgressDialog    InProgressDialogue;
    private int               INTID=3939;
    private FirebaseAuth      InAuthentication;
    private FirebaseUser      firebaseUser;
    private TextView          ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        Ask.on(this)
                .id(INTID) // in case you are invoking multiple time Ask from same activity or fragment
                .forPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                         , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.INTERNET
                        ,Manifest.permission.CAMERA
                        ,Manifest.permission.ACCESS_FINE_LOCATION
                        ,Manifest.permission.RECORD_AUDIO
                        ,Manifest.permission.VIBRATE
                        ,Manifest.permission.SYSTEM_ALERT_WINDOW
                         )
                .go();

        EmailField=(EditText)findViewById(R.id.EmailEditText);
        PassWordField=(EditText)findViewById(R.id.PasswordField);
        LoginButton=(Button)findViewById(R.id.LogInButton);
        CreateAccountButton=(Button)findViewById(R.id.CreateUser);
        ForgotPassword=(TextView)findViewById(R.id.ForgotPassword);
        InAuth=FirebaseAuth.getInstance();
        InDatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        InDatabaseUser.keepSynced(true);

        InProgressDialogue=new ProgressDialog(this);
        getSupportActionBar().setElevation(0);

        CreateAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this,RegisterUser.class));
            }

        });
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
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Verify Email Address",Toast.LENGTH_LONG).show();
                        }

                        InProgressDialogue.dismiss();

                    }
                    else{
                        InProgressDialogue.dismiss();
                        Toast.makeText(LoginActivity.this,"Email or password authentication failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else
        {
            if(TextUtils.isEmpty(Email))
            {
                Toast.makeText(LoginActivity.this,"Please enter your email.",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(LoginActivity.this,"Please enter your password.",Toast.LENGTH_LONG).show();

            }
        }

    }



}
