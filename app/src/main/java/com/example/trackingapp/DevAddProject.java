package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongFunction;
import java.util.regex.Matcher;

import static com.example.trackingapp.R.layout.activity_listview;
import static com.example.trackingapp.R.layout.activity_user_raise_ticket;

public class DevAddProject extends AppCompatActivity {

    private EditText edtTextNewProjectName,edtTextNewProjectDesc,edtTextNewProjectKey, edtMemberEmail;
    private Button btnNewProject, btnAddMember;
    private RelativeLayout AddTeamLayout;
    private TextView AddTeam;
    private ListView MemberList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase users  = FirebaseDatabase.getInstance();
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> TeamUIDS = new ArrayList<>();
//    ArrayList<String > TeamNames = new ArrayList<>();
    ArrayAdapter<String > adapter;
    Map<String,String> mp = new HashMap<>(); // to add in db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_add_project);

        initValues();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String cur_dev = document.getString("name");
                    arrayList.add(cur_dev);
                    TeamUIDS.add(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    mp.put(FirebaseAuth.getInstance().getCurrentUser().getUid() , cur_dev);
                    adapter = new ArrayAdapter<String>(DevAddProject.this, activity_listview,R.id.txtitem,arrayList);
                    MemberList.setAdapter(adapter);
                }
            }
        });

        AddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AddTeamLayout.getVisibility() == View.GONE)AddTeamLayout.setVisibility(View.VISIBLE);
                else AddTeamLayout.setVisibility(View.GONE);

            }
        });
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtMemberEmail.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()){
                    edtMemberEmail.setError("Enter valid email address");
                    edtMemberEmail.requestFocus();
                    return;
                }
                if(email.trim().isEmpty()){
                    edtMemberEmail.setError("Enter valid email address");
                    edtMemberEmail.requestFocus();
                    return;
                }
                db.collection("Users").whereEqualTo("email",email.toString()).whereEqualTo("role",2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                // TODO: 29-06-2021 Add in list;
                                if(TeamUIDS.contains(document.getId().toString())){
                                    Toast.makeText(DevAddProject.this, "Already in team", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                arrayList.add(document.getString("name"));
                                TeamUIDS.add(document.getId());
                                mp.put(document.getId(),document.getString("name"));
                                adapter.notifyDataSetChanged();
                                edtMemberEmail.getText().clear();
                                return;
                            }
                        }
                        Toast.makeText(DevAddProject.this, "Dev does not exist", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


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
                    db.collection("Projects").document(edtTextNewProjectKey.getText().toString()).get().addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            edtTextNewProjectKey.setError("This key already exists");
                                            edtTextNewProjectKey.requestFocus();
                                            return;
                                        }
                                        // adding in database;
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
                                            project.put("team",mp);
//                                            project.put("teambyname",arrayList);
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
                                        project.put("team",mp);
//                                        project.put("teambyname",arrayList);
//                                        Log.d("Users","size = " + TeamUIDS.size());
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
                            }
                    );







                }
            }
        });


        MemberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)return;
                else{
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevAddProject.this);
                    builder.setMessage("Remove from Team?");
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            arrayList.remove(position);
                            mp.remove(TeamUIDS.get(position));
                            TeamUIDS.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(DevAddProject.this, "Removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();


                }
            }
        });
    }

    private void initValues() {
        edtTextNewProjectName = findViewById(R.id.edtTextNewProjectName);
        edtTextNewProjectDesc = findViewById(R.id.edtTextNewProjectDesc);
        edtTextNewProjectKey = findViewById(R.id.edtTextNewProjectKey);
        AddTeamLayout = findViewById(R.id.AddTeamLayout);
        edtMemberEmail = findViewById(R.id.edtMemberEmail);
        btnNewProject = findViewById(R.id.btnNewProject);
        btnAddMember = findViewById(R.id.btnAddMember);
        AddTeam = findViewById(R.id.AddTeam);
        MemberList = findViewById((R.id.MemberList));

    }
}