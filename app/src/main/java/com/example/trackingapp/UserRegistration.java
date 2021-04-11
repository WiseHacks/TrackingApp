package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegistration extends AppCompatActivity {

    private EditText edtTextUserRegistrationName,edtTextUserRegistrationEmail,edtTextUserRegistrationPhone,edtTextUserRegistrationPassword;
    private Button btnUserRegistration;
    private FirebaseAuth mAuth;
    private ProgressBar progressBarUserRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        initValues();

        mAuth = FirebaseAuth.getInstance();

        btnUserRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = edtTextUserRegistrationName.getText().toString() , user_email = edtTextUserRegistrationEmail.getText().toString(),
                        user_password = edtTextUserRegistrationPassword.getText().toString(),user_phone = edtTextUserRegistrationPhone.getText().toString();

                if(user_name.trim().isEmpty() || user_email.trim().isEmpty() || user_phone.trim().isEmpty() || user_password.isEmpty()){
                    Toast.makeText(UserRegistration.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(user_email.trim()).matches()){
                    edtTextUserRegistrationEmail.setError("Enter valid email address");
                    edtTextUserRegistrationEmail.requestFocus();
                    return;
                }
                if(user_phone.trim().length()!=10){
                    edtTextUserRegistrationPhone.setError("Enter valid phone number");
                    edtTextUserRegistrationPhone.requestFocus();
                    return;
                }
                if(user_password.length()<6){
                    edtTextUserRegistrationPassword.setError("Minimum length should be 6");
                    edtTextUserRegistrationPassword.requestFocus();
                    return;
                }
                progressBarUserRegistration.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(user_email,user_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarUserRegistration.setVisibility(View.GONE);
                                if(task.isSuccessful()){
//                                    Log.d("divyesh",user_password+"hi");
//                                    User user =new User(1,user_name,user_email,user_phone,user_password);
                                    User user =new User(1,user_name,user_email,user_phone);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(UserRegistration.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(UserRegistration.this,UserDashboard.class));
                                                finishAffinity();
                                            }
                                            else{
                                                Toast.makeText(UserRegistration.this, "Failed!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(UserRegistration.this, "Failed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });

    }

    private void initValues() {
        edtTextUserRegistrationName = findViewById(R.id.edtTextUserRegistrationName);
        edtTextUserRegistrationEmail = findViewById(R.id.edtTextUserRegistrationEmail);
        edtTextUserRegistrationPassword = findViewById(R.id.edtTextUserRegistrationPassword);
        edtTextUserRegistrationPhone = findViewById(R.id.edtTextUserRegistrationPhone);
        btnUserRegistration = findViewById(R.id.btnUserRegistration);
        progressBarUserRegistration = findViewById(R.id.progressBarUserRegistration);
    }
}