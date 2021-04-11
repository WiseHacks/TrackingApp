package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeveloperDashboard extends AppCompatActivity {

    private Button btnDeveloperLogout;
    private DrawerLayout drawerLayoutDev;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView txtDevName, txtDevEmail, txtDevPhone;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_dashboard);

        txtDevName = findViewById(R.id.txtDevName);
        txtDevEmail = findViewById(R.id.txtDevEmail);
        txtDevPhone = findViewById(R.id.txtDevPhone);
        {
            drawerLayoutDev = findViewById(R.id.drawerLayoutDev);
            navigationView = findViewById(R.id.devNavigation);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutDev, R.string.dev_nav_open, R.string.dev_nav_close);
            drawerLayoutDev.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.dev_nav_account:
                            startActivity(new Intent(DeveloperDashboard.this,DeveloperDashboard.class));
                            finish();
                            break;
                        case R.id.dev_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DeveloperDashboard.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(DeveloperDashboard.this, MainActivity.class));
                                    finish();
                                }
                            });
                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();

                            break;
//                        case R.id.dev_nav_recentTicket:
//                            startActivity(new Intent(DeveloperDashboard.this,DevRecentTicket.class));
//                            finish();
//                            break;
                        case R.id.dev_nav_assignedTickets:
                            startActivity(new Intent(DeveloperDashboard.this,DevAssignedTickets.class));
                            finish();
                            break;
                        case R.id.dev_nav_project:
                            startActivity(new Intent(DeveloperDashboard.this,DevMyProjects.class));
                            finish();
                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });


        }


        auth = FirebaseAuth.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
//                    Log.d("test","OK" + user.fullname);
                    txtDevName.setText(user.fullname);
                    txtDevEmail.setText(user.email);
                    txtDevPhone.setText(user.phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeveloperDashboard.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}