package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import static java.lang.String.valueOf;

public class AdminAuth extends AppCompatActivity {
    private EditText edtTextAdminEmail, edtTextAdminPassword;
    private Button btnAdminLogin;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBarAdmin;
    private Integer role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_auth);
        initValues();

        // Now to login them ....
        auth = FirebaseAuth.getInstance();

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtTextAdminEmail.getText().toString();
                final String password = edtTextAdminPassword.getText().toString();
                if(email.trim().isEmpty()){
                    Toast.makeText(AdminAuth.this, "Check Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()){
                    edtTextAdminEmail.setError("Enter valid email address");
                    edtTextAdminEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(AdminAuth.this, "Check Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    edtTextAdminPassword.setError("Minimum length should be 6");
                    edtTextAdminPassword.requestFocus();
                    return;
                }
                progressBarAdmin.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(AdminAuth.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarAdmin.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                        Toast.makeText(AdminAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                                } else {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(AdminAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                role = Integer.parseInt( String.valueOf(task.getResult().getValue()));
                                                if(role==0){
                                                    startActivity(new Intent(AdminAuth.this,AdminDashboard.class));
                                                    finishAffinity();
//                                                    finish();
//                                                    notify();
                                                }

                                                else{
                                                    Toast.makeText(AdminAuth.this, "You are not an Admin!", Toast.LENGTH_SHORT).show();
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

                //
                // Admin login is working
            }
        });

    }

    private void initValues() {
        edtTextAdminEmail = findViewById(R.id.edtTextAdminEmail);
        edtTextAdminPassword = findViewById(R.id.edtTextAdminPassword);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        progressBarAdmin = findViewById(R.id.progressBarAdmin);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if(!task.isSuccessful()){
//                        Toast.makeText(AdminAuth.this, "Authentication Failed", Toast.LENGTH_LONG).show();
//                    }
//                    else{
//                        role = Integer.parseInt( String.valueOf(task.getResult().getValue()));
//                        if(role==0){
//                            startActivity(new Intent(AdminAuth.this,AdminDashboard.class));
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
//    }

}