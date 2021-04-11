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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeveloperAuth extends AppCompatActivity {

    private EditText edtTextDeveloperLoginEmail, edtTextDeveloperLoginPassword;
    private TextView btnDeveloperRegister,btnDeveloperLogin,btnDeveloperForgotPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBarDeveloper;
    private Integer role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_auth);

        initValues();

        btnDeveloperRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeveloperAuth.this,DeveloperRegistration.class));
            }
        });
        btnDeveloperForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeveloperAuth.this,ForgotPassword.class));
            }
        });

        auth = FirebaseAuth.getInstance();

        btnDeveloperLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtTextDeveloperLoginEmail.getText().toString();
                final String password = edtTextDeveloperLoginPassword.getText().toString();
                if(email.trim().isEmpty()){
                    Toast.makeText(DeveloperAuth.this, "Check Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()){
                    edtTextDeveloperLoginEmail.setError("Enter valid email address");
                    edtTextDeveloperLoginEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(DeveloperAuth.this, "Check Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    edtTextDeveloperLoginPassword.setError("Minimum length should be 6");
                    edtTextDeveloperLoginPassword.requestFocus();
                    return;
                }
                progressBarDeveloper.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(DeveloperAuth.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarDeveloper.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    Toast.makeText(DeveloperAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                                } else {

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(DeveloperAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                role = Integer.parseInt( String.valueOf(task.getResult().getValue()));
                                                if(role==2){
                                                    startActivity(new Intent(DeveloperAuth.this,DeveloperDashboard.class));
                                                    finishAffinity();
//                                                    finish();
//                                                    notify();
                                                }
                                                else{
                                                    Toast.makeText(DeveloperAuth.this, "You are not a Developer!", Toast.LENGTH_SHORT).show();
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
        edtTextDeveloperLoginEmail = findViewById(R.id.edtTextDeveloperLoginEmail);
        edtTextDeveloperLoginPassword = findViewById(R.id.edtTextDeveloperLoginPassword);
        btnDeveloperLogin = findViewById(R.id.btnDeveloperLogin);
        btnDeveloperRegister = findViewById(R.id.btnDeveloperRegister);
        btnDeveloperForgotPassword = findViewById(R.id.btnDeveloperForgotPassword);
        progressBarDeveloper = findViewById(R.id.progressBarDeveloper);
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
//                        Toast.makeText(DeveloperAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();
//                    }
//                    else{
//                        role = Integer.parseInt( String.valueOf(task.getResult().getValue()));
//                        if(role==2){
//                            startActivity(new Intent(DeveloperAuth.this,DeveloperDashboard.class));
//                            finish();
//                        }
////                        else{
//////                            Toast.makeText(DeveloperAuth.this, "You are not a User!", Toast.LENGTH_SHORT).show();
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