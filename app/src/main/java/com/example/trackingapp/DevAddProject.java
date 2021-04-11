package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DevAddProject extends AppCompatActivity {

    private EditText edtTextNewProjectName,edtTextNewProjectDesc,edtTextNewProjectKey;
    private Button btnNewProject;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_add_project);

        initValues();


        btnNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtTextNewProjectName.getText().toString(),Desc = edtTextNewProjectDesc.getText().toString(),Key = edtTextNewProjectKey.getText().toString().trim();
                if(name.trim().isEmpty() || Desc.trim().isEmpty() || Key.trim().isEmpty()){
                    Toast.makeText(DevAddProject.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                }
                else if(Key.trim().length()<5){
                    edtTextNewProjectKey.setError("Minimum length should be 5");
                    edtTextNewProjectKey.requestFocus();
                    return;
                }
                else{
                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c);
                    Map<String,Object> project = new HashMap<>();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    project.put("name",name);
                    project.put("description",Desc);
                    project.put("developer",user.getUid().toString());
                    project.put("date", formattedDate);

                    db.collection("Projects").document(Key).set(project).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DevAddProject.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DevAddProject.this,DevMyProjects.class));
                                finishAffinity();
                                DocumentReference documentReference = db.collection("Developer").document(user.getUid().toString());
                                documentReference.update("MyProjects", FieldValue.arrayUnion(Key));
                            }
                            else{
                                Toast.makeText(DevAddProject.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });






                }
            }
        });




    }

    private void initValues() {
        edtTextNewProjectName = findViewById(R.id.edtTextNewProjectName);
        edtTextNewProjectDesc = findViewById(R.id.edtTextNewProjectDesc);
        edtTextNewProjectKey = findViewById(R.id.edtTextNewProjectKey);
        btnNewProject = findViewById(R.id.btnNewProject);
    }
}