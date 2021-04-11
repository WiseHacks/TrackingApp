package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText edtTextForgotEmail;
    private Button btnForgotReset;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initValues();

        auth = FirebaseAuth.getInstance();
        btnForgotReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtTextForgotEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    edtTextForgotEmail.setError("Enter valid email address");
                    edtTextForgotEmail.requestFocus();
                    return;
                }

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ForgotPassword.this, "We have sent you instruction to reset your password", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ForgotPassword.this,MainActivity.class));
                                }
                                else{
                                    Toast.makeText(ForgotPassword.this, "Failed to sent an email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void initValues() {
        edtTextForgotEmail = findViewById(R.id.edtTextForgotEmail);
        btnForgotReset = findViewById(R.id.btnForgotReset);
    }

}