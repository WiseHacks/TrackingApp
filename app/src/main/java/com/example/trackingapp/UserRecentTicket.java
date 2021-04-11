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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserRecentTicket extends AppCompatActivity {

    public DrawerLayout drawerLayoutUser;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recent_ticket);

        {
            drawerLayoutUser = findViewById(R.id.drawerLayoutUser3);
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
                            startActivity(new Intent(UserRecentTicket.this, UserDashboard.class));
                            finish();
                            break;

                        case R.id.user_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserRecentTicket.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(UserRecentTicket.this, MainActivity.class));
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
                            startActivity(new Intent(UserRecentTicket.this, UserMyTickets.class));
                            finish();
                            break;
//                        case R.id.user_nav_recentTicket:
//                            startActivity(new Intent(UserRecentTicket.this, UserRecentTicket.class));
//                            finish();
//                            break;
                        case R.id.user_nav_raise:
                            startActivity(new Intent(UserRecentTicket.this, UserRaiseTicket.class));
                            finish();
                            break;
//                        case R.id.user_nav_loginAs:
//                            startActivity(new Intent(UserRecentTicket.this, MainActivity.class));
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}