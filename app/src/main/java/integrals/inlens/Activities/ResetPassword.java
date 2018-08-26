package integrals.inlens.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import integrals.inlens.R;

public class ResetPassword extends AppCompatActivity {
    private EditText ResetEmail;
    private Button   ResetButton;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ResetEmail=(EditText)findViewById(R.id.ResetEmailEditText);
        ResetButton=(Button)findViewById(R.id.PasswordResetButton);
        progressBar=(ProgressBar)findViewById(R.id.SendingProgress);
        firebaseAuth=FirebaseAuth.getInstance();

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String Email=ResetEmail.getText().toString().trim();
                 if(Email==null){
                     Toast.makeText(getApplicationContext(),"Please provide the registered E-mail above.",Toast.LENGTH_SHORT).show();
                    }else {
                     ResetEmail.setEnabled(false);
                     ResetButton.setEnabled(false);
                     progressBar.setVisibility(View.VISIBLE);
                     ResetButton.setVisibility(View.INVISIBLE);
                     firebaseAuth.sendPasswordResetEmail(Email)
                               .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     progressBar.setVisibility(View.INVISIBLE);
                                     ResetButton.setVisibility(View.VISIBLE);
                                     ResetEmail.setEnabled(true);
                                     ResetButton.setEnabled(true);
                                     Toast.makeText(getApplicationContext(),"Failed. Please check your internet connection and try again.",Toast.LENGTH_LONG).show();

                                 }
                             }).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                             Toast.makeText(getApplicationContext(),"Password Reset E-mail send. Please check your mail.",Toast.LENGTH_LONG).show();
                             progressBar.setVisibility(View.INVISIBLE);
                             ResetButton.setVisibility(View.VISIBLE);
                             ResetEmail.setEnabled(true);
                             ResetButton.setEnabled(true);

                           }
                       }).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             finish();
                         }
                     });



                 }

                 }
        });

    }

}
