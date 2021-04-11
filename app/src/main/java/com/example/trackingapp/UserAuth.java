package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAuth extends AppCompatActivity {

    private EditText edtTextUserLoginEmail,edtTextUserLoginPassword;
    private TextView btnUserRegister,btnUserLogin,btnUserForgotPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBarUser;
    private Integer role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        initValues();
        btnUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAuth.this,UserRegistration.class));
            }
        });
        btnUserForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAuth.this,ForgotPassword.class));
            }
        });

        auth = FirebaseAuth.getInstance();

        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtTextUserLoginEmail.getText().toString();
                final String password = edtTextUserLoginPassword.getText().toString();
                if(email.trim().isEmpty()){
                    Toast.makeText(UserAuth.this, "Check Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()){
                    edtTextUserLoginEmail.setError("Enter valid email address");
                    edtTextUserLoginEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(UserAuth.this, "Check Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    edtTextUserLoginPassword.setError("Minimum length should be 6");
                    edtTextUserLoginPassword.requestFocus();
                    return;
                }
                progressBarUser.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(UserAuth.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarUser.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                        Toast.makeText(UserAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                                } else {

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(UserAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                role = Integer.parseInt( String.valueOf(task.getResult().getValue()));
                                                if(role==1) {
                                                    startActivity(new Intent(UserAuth.this, UserDashboard.class));
                                                    finishAffinity();
//                                                    finishFromChild(UserAuth.this);
//                                                    finish();
//                                                    notify();
//                                                    notifyAll();
//                                                    UserAuth.this.notify();
                                                }
                                                else{
                                                    Toast.makeText(UserAuth.this, "You are not a User!", Toast.LENGTH_SHORT).show();
                                                    auth.signOut();
//                                                    finish();
                                                }
                                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                            }
                                        }
                                    });
                                }
                            }
                        });

            }
        });

    }

    private void initValues() {
        edtTextUserLoginEmail = findViewById(R.id.edtTextUserLoginEmail);
        edtTextUserLoginPassword = findViewById(R.id.edtTextUserLoginPassword);
        btnUserLogin = findViewById(R.id.btnUserLogin);
        btnUserRegister = findViewById(R.id.btnUserRegister);
        btnUserForgotPassword = findViewById(R.id.btnUserForgotPassword);
        progressBarUser = findViewById(R.id.progressBarUser);
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if(!task.isSuccessful()){
//                        Toast.makeText(UserAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();
//                    }
//                    else{
//                        role = Integer.parseInt( String.valueOf(task.getResult().getValue()));
//                        if(role==1){
//                            startActivity(new Intent(UserAuth.this,UserDashboard.class));
//                            finish();
//                        }
////                        else{
//////                            Toast.makeText(UserAuth.this, "You are not a User!", Toast.LENGTH_SHORT).show();
////                            auth.signOut();
//////                                                    finish();
////                        }
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                    }
//                }
//            });
//        }
//
//    }
}