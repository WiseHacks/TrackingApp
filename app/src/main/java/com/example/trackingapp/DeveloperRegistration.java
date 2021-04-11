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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeveloperRegistration extends AppCompatActivity {

    private EditText edtTextDeveloperRegistrationName,edtTextDeveloperRegistrationEmail,edtTextDeveloperRegistrationPhone,edtTextDeveloperRegistrationPassword;
    private Button btnDeveloperRegistration;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_registration);

        initValues();

        mAuth = FirebaseAuth.getInstance();

        btnDeveloperRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dev_name = edtTextDeveloperRegistrationName.getText().toString() , dev_email = edtTextDeveloperRegistrationEmail.getText().toString(),
                        dev_password = edtTextDeveloperRegistrationPassword.getText().toString(),dev_phone = edtTextDeveloperRegistrationPhone.getText().toString();

                if(dev_name.trim().isEmpty() || dev_email.trim().isEmpty() || dev_phone.trim().isEmpty() || dev_password.isEmpty()){
                    Toast.makeText(DeveloperRegistration.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(dev_email.trim()).matches()){
                    edtTextDeveloperRegistrationEmail.setError("Enter valid email address");
                    edtTextDeveloperRegistrationEmail.requestFocus();
                    return;
                }
                if(dev_phone.trim().length()!=10){
                    edtTextDeveloperRegistrationPhone.setError("Enter valid phone number");
                    edtTextDeveloperRegistrationPhone.requestFocus();
                    return;
                }
                if(dev_password.length()<6){
                    edtTextDeveloperRegistrationPassword.setError("Minimum length should be 6");
                    edtTextDeveloperRegistrationPassword.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(dev_email,dev_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
//                                    User user =new User(2,dev_name,dev_email,dev_phone,dev_password);
                                    User user =new User(2,dev_name,dev_email,dev_phone);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(DeveloperRegistration.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(DeveloperRegistration.this,DeveloperDashboard.class));
                                                finishAffinity();
                                            }
                                            else{
                                                Toast.makeText(DeveloperRegistration.this, "Failed!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });


                                    Map<String,Object> data = new HashMap<>();

                                    data.put("MyProjects", Arrays.asList());

                                    db.collection("Developer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(data);

                                }
                                else{
                                    Toast.makeText(DeveloperRegistration.this, "Failed!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });
    }

    private void initValues() {
        edtTextDeveloperRegistrationName = findViewById(R.id.edtTextDeveloperRegistrationName);
        edtTextDeveloperRegistrationEmail = findViewById(R.id.edtTextDeveloperRegistrationEmail);
        edtTextDeveloperRegistrationPassword = findViewById(R.id.edtTextDeveloperRegistrationPassword);
        edtTextDeveloperRegistrationPhone = findViewById(R.id.edtTextDeveloperRegistrationPhone);
        btnDeveloperRegistration = findViewById(R.id.btnDeveloperRegistration);
    }
}