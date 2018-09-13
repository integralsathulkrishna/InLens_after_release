package integrals.inlens.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import integrals.inlens.R;

public class RegisterUser extends AppCompatActivity {
    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;

    private Button mCreateBtn;
    private Button VerifiedButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        getSupportActionBar().setTitle(" Register User");
        getSupportActionBar().setElevation(0);
            mAuth = FirebaseAuth.getInstance();
            mDisplayName = (EditText) findViewById(R.id.NameField);
            VerifiedButton=(Button)findViewById(R.id.VerifiedButton);
            mEmail = (EditText) findViewById(R.id.EmailField);
            mPassword = (EditText) findViewById(R.id.Password);
            mCreateBtn = (Button) findViewById(R.id.ProceedButton);
            progressDialog = new ProgressDialog(this);
            getSupportActionBar().setTitle("User Registration");
            VerifiedButton.setEnabled(false);
            mCreateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String display_name =mDisplayName.getText().toString();
                    String email =mEmail.getText().toString();
                    String password =mPassword.getText().toString();

                    if(TextUtils.isEmpty(display_name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {


                        Toast.makeText(RegisterUser.this,"Check the details you entered", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if(password.length()<=7){
                            Toast.makeText(getApplicationContext(),"Retype Password it  must contain minimum 8 charachters",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressDialog.setMessage("Registering user. Please wait...");
                            progressDialog.show();
                            register_user(display_name,email,password);
                        }


                    }

                }
            });


            VerifiedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseAuth.getInstance().getCurrentUser().reload();
                    checkEmailVerification();
                }
            });

        }



    private void register_user(final String display_name, final String email, String password) {

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){



                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = current_user.getUid();

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                        String device_token = FirebaseInstanceId.getInstance().getToken();

                        HashMap<String,String> usermap = new HashMap<>();
                        usermap.put("Name",display_name);

                        mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if(task.isSuccessful()){

                                    progressDialog.dismiss();
                                    sendEmailVerification();

                                }
                                else {

                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterUser.this,task.getResult().toString(),Toast.LENGTH_LONG).show();
                                }


                            }
                        });


                    }

                    else {

                        progressDialog.dismiss();
                        Toast.makeText(RegisterUser.this,"Cannot register,Please try again.. ",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        private void  sendEmailVerification(){
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"E-mail verification send. Please check your mail and click the link.",Toast.LENGTH_LONG).show();
                VerifiedButton.setEnabled(true);
                }
            });
        }

    }

        private void checkEmailVerification(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified())
        {
            startActivity(new Intent(RegisterUser.this,SettingActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"E-mail verification send. Please check your mail and click the link to verify.",Toast.LENGTH_LONG).show();

        }

    }



}

