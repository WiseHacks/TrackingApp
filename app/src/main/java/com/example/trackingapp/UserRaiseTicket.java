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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserRaiseTicket extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public DrawerLayout drawerLayoutUser;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private EditText edtTextRaiseProjectKey,edtTextRaiseTicketSubject,edtTextRaiseTicketDesc;
    private Button btnTicketRaised;
    private Spinner spinnerPriority;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String Pr;



    String[] Priorities = {"Low","Medium","High"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_raise_ticket);
        //Creating the ArrayAdapter instance having the country list

        initValues();

        spinnerPriority.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Priorities);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerPriority.setAdapter(aa);

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
                            startActivity(new Intent(UserRaiseTicket.this, UserDashboard.class));
                            finish();
                            break;

                        case R.id.user_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserRaiseTicket.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(UserRaiseTicket.this, MainActivity.class));
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
                            startActivity(new Intent(UserRaiseTicket.this, UserMyTickets.class));
                            finish();
                            break;
//                        case R.id.user_nav_recentTicket:
//                            startActivity(new Intent(UserRaiseTicket.this, UserRecentTicket.class));
//                            finish();
//                            break;

                        case R.id.user_nav_raise:
                            startActivity(new Intent(UserRaiseTicket.this, UserRaiseTicket.class));
                            finish();
                            break;
//                        case R.id.user_nav_loginAs:
//
//                            startActivity(new Intent(UserRaiseTicket.this, MainActivity.class));
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
        btnTicketRaised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Key=edtTextRaiseProjectKey.getText().toString(),Desc = edtTextRaiseTicketDesc.getText().toString(),
                        Sub = edtTextRaiseTicketSubject.getText().toString();
                if(Key.trim().isEmpty() || Desc.trim().isEmpty() || Sub.trim().isEmpty()){
                    Toast.makeText(UserRaiseTicket.this, "Enter valid details", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.collection("Projects").document(edtTextRaiseProjectKey.getText().toString()).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            Date c = Calendar.getInstance().getTime();
                                            System.out.println("Current time => " + c);

                                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                            String formattedDate = df.format(c);
                                            Map<String, Object> ticket = new HashMap<>();
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            ticket.put("assignedToDevId", "xxxx");
                                            ticket.put("raisedByUserId", user.getUid());
                                            ticket.put("ticketDesc", Desc);
                                            ticket.put("ticketModified", formattedDate);
                                            ticket.put("ticketPatchByDev", "");
                                            ticket.put("ticketProjectId", Key);
                                            ticket.put("ticketStatusByAdmin", "NA");
                                            ticket.put("ticketStatusByDev", "NA");
                                            ticket.put("ticketSubject", Sub);
                                            ticket.put("ticketPriority",Pr);


                                            db.collection("Tickets").add(ticket).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(UserRaiseTicket.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(UserRaiseTicket.this, UserDashboard.class));
                                                        finishAffinity();
                                                    } else {
                                                        Toast.makeText(UserRaiseTicket.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                        }
                                        else {
                                            edtTextRaiseProjectKey.setError("Project does not exist");
                                            edtTextRaiseProjectKey.requestFocus();
                                        }
                                    } else {
                                        Toast.makeText(UserRaiseTicket.this, "Unknown Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void initValues() {
        edtTextRaiseProjectKey = findViewById(R.id.edtTextRaiseProjectKey);
        edtTextRaiseTicketSubject = findViewById(R.id.edtTextRaiseTicketSubject);
        edtTextRaiseTicketDesc = findViewById(R.id.edtTextRaiseTicketDesc);
        btnTicketRaised = findViewById(R.id.btnTicketRaised);
        spinnerPriority = findViewById(R.id.spinnerPriority);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Pr = Priorities[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Pr = "Low";
    }
}