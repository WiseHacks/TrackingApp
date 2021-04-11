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

public class UserDashboard extends AppCompatActivity {

    private Button btnUserLogout;
    public DrawerLayout drawerLayoutUser;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView txtUserName,txtUserEmail,txtUserPhone;
    private FirebaseAuth auth;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserPhone = findViewById(R.id.txtDevPhone);

        {
            drawerLayoutUser = findViewById(R.id.drawerLayoutUser);
            navigationView = findViewById(R.id.userNavigation);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutUser, R.string.user_nav_open, R.string.user_nav_close);
            drawerLayoutUser.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.user_nav_account:
                            startActivity(new Intent(UserDashboard.this, UserDashboard.class));
                            finish();
                            break;


                        case R.id.user_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserDashboard.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(UserDashboard.this, MainActivity.class));
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

                        case R.id.user_nav_myTickets:
                            startActivity(new Intent(UserDashboard.this, UserMyTickets.class));
                            finish();
                            break;
//                        case R.id.user_nav_recentTicket:
//                            startActivity(new Intent(UserDashboard.this, UserRecentTicket.class));
//                            finish();
//                            break;

                        case R.id.user_nav_raise:
                            startActivity(new Intent(UserDashboard.this, UserRaiseTicket.class));
                            finish();
                            break;
//                        case R.id.user_nav_loginAs:
//                            startActivity(new Intent(UserDashboard.this, MainActivity.class));
//                            FirebaseAuth.getInstance().signOut();
//                            finishAffinity();
//                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });

        }

        //to show profile
        auth = FirebaseAuth.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
//                    Log.d("test","OK" + user.fullname);
                    txtUserName.setText(user.fullname);
                    txtUserEmail.setText(user.email);
                    txtUserPhone.setText(user.phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDashboard.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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