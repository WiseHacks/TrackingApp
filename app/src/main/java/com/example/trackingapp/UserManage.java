package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserManage extends AppCompatActivity {

    private EditText edtEmail,edtPassword;
    private Button btnManage;
    private FirebaseAuth auth;
    private ProgressBar pbar;
    private ScrollView usermanage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnManage = findViewById(R.id.btnManage);
        auth = FirebaseAuth.getInstance();
        pbar = findViewById(R.id.pbar);
        usermanage = findViewById(R.id.usermanage);
        btnManage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString(), Pass=  edtPassword.getText().toString();

                if(email.trim().isEmpty()){
                    edtEmail.setError("Cannot be empty");
                    edtEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtEmail.setError("Enter valid email");
                    edtEmail.requestFocus();
                    return;
                }
                if(Pass.length()<6){
                    edtPassword.setError("Minimum length should be 6");
                    edtPassword.requestFocus();
                    return;
                }
                pbar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            pbar.setVisibility(View.GONE);
                            db.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        auth.signOut();
                                        Toast.makeText(UserManage.this, "You don't have rights!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        startActivity(new Intent(UserManage.this,UserManagement.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(UserManage.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }


}