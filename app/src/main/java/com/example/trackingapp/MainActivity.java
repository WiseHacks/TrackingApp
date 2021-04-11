package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button btnEnterAdmin,btnEnterUser,btnEnterDeveloper;
    private FirebaseAuth auth;
    private Integer role;
    private RelativeLayout mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        btnEnterAdmin = findViewById(R.id.btnEnterAdmin);
        btnEnterDeveloper = findViewById((R.id.btnEnterDeveloper));
        btnEnterUser = findViewById(R.id.btnEnterUser);
        mainActivity = findViewById(R.id.mainActivity);

        if(auth.getCurrentUser()==null) {
            btnEnterAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, AdminAuth.class));
                }
            });
            btnEnterDeveloper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, DeveloperAuth.class));
                }
            });
            btnEnterUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, UserAuth.class));
                }
            });
        }
        else {
            Log.d("firebase" , "checking : "+ auth.getCurrentUser().getUid().toString());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            btnEnterAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (auth.getCurrentUser().getUid() != null) {
                            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        role = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                        if (role == 0) {
                                            startActivity(new Intent(MainActivity.this, AdminDashboard.class));
                                            finishAffinity();
//                                        finish();
                                        } else {
                                            startActivity(new Intent(MainActivity.this, AdminAuth.class));
////                            Toast.makeText(DeveloperAuth.this, "You are not a User!", Toast.LENGTH_SHORT).show();
//                            auth.signOut();
////                                                    finish();
                                        }
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    }
                                }
                            });
                        }
                    }
                    catch (Exception e){
                        startActivity(new Intent(MainActivity.this, AdminAuth.class));
                    }
                }
            });
            btnEnterDeveloper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (auth.getCurrentUser().getUid() != null) {
                            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        role = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                        if (role == 2) {
                                            startActivity(new Intent(MainActivity.this, DeveloperDashboard.class));
                                            finishAffinity();
//                                    finish();
                                        } else {

                                            startActivity(new Intent(MainActivity.this, DeveloperAuth.class));
////                            Toast.makeText(DeveloperAuth.this, "You are not a User!", Toast.LENGTH_SHORT).show();
//                            auth.signOut();
////                                                    finish();
                                        }
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    }
                                }
                            });
                        }

                    }catch (Exception e){
                        startActivity(new Intent(MainActivity.this, DeveloperAuth.class));
                    }

                }
            });
            btnEnterUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (auth.getCurrentUser().getUid() != null) {
                            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        role = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                        if (role == 1) {
                                            startActivity(new Intent(MainActivity.this, UserDashboard.class));
                                            finishAffinity();
//                                    finish();
                                        } else {
                                            startActivity(new Intent(MainActivity.this, UserAuth.class));
////                            Toast.makeText(UserAuth.this, "You are not a User!", Toast.LENGTH_SHORT).show();
//                            auth.signOut();
////                                                    finish();
                                        }
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    }
                                }
                            });
                        }
                    }catch (Exception e)
                    {
                        startActivity(new Intent(MainActivity.this, UserAuth.class));
                    }
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            mainActivity.setVisibility(View.GONE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("role").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(!task.isSuccessful()){
//                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                        mainActivity.setVisibility(View.VISIBLE);
                        auth.signOut();
                    }
                    else{
                        try {
                            role = Integer.parseInt(String.valueOf(task.getResult().getValue()));

                            if (role == 0) {
                                startActivity(new Intent(MainActivity.this, AdminDashboard.class));
                                finish();
                            } else if (role == 1) {
                                startActivity(new Intent(MainActivity.this, UserDashboard.class));
                                finish();
                            } else if (role == 2) {
                                startActivity(new Intent(MainActivity.this, DeveloperDashboard.class));
                                finish();
                            } else {
                                mainActivity.setVisibility(View.VISIBLE);
                                auth.signOut();
                            }
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        }catch (Exception e){
                            mainActivity.setVisibility(View.VISIBLE);
                            auth.signOut();
                        }
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.btnAbout:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Issue Tracking App -");
                builder.setMessage("Contact\n\niit2019063@iiita.ac.in");
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Go to Web", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this,WebsiteActivity.class);
                        intent.putExtra("url","https://www.google.com/");
                        startActivity(intent);
                    }
                });
                builder.create().show();
                break;

            case R.id.btnFeed:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","",null));
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"iit2019063@iiita.ac.in"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Feedback for Issue-Tracking Application");
                emailIntent.putExtra(Intent.EXTRA_TEXT,"Any kind of Feedback ->");
                startActivity(Intent.createChooser(emailIntent,"Send Email.."));

                break;
            default:break;

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

}