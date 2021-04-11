package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DevRecentTicket extends AppCompatActivity {

    private DrawerLayout drawerLayoutDev;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView txtATicketProject, txtATicketSub, txtATicketUser, txtATicketPr, txtATicketdate, txtATicketDesc,warning;
    private ImageView imgexpand,imgcompress;
    private RelativeLayout expandedDetails,imgs;
    private EditText edtPatch;
    private Button btnSolve;
    private CardView cardView;
    private ImageView btnDiscardTicket,btnAssignTicket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_recent_ticket);

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
                            startActivity(new Intent(DevRecentTicket.this,DeveloperDashboard.class));
                            finish();
                            break;
                        case R.id.dev_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevRecentTicket.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(DevRecentTicket.this, MainActivity.class));
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
//                            startActivity(new Intent(DevRecentTicket.this,DevRecentTicket.class));
//                            finish();
//                            break;
                        case R.id.dev_nav_assignedTickets:
                            startActivity(new Intent(DevRecentTicket.this,DevAssignedTickets.class));
                            finish();
                            break;

                        case R.id.dev_nav_project:
                            startActivity(new Intent(DevRecentTicket.this,DevMyProjects.class));
                            finish();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });


        }

        initValues();
        auth = FirebaseAuth.getInstance();

//        db.collection("Developer").document(auth.getCurrentUser().getUid())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot snapshot = (DocumentSnapshot) task.getResult();
//                    if(snapshot.exists()){
//                        String ticket = snapshot.get("RecentTicket").toString();
//                        if(!Objects.equals(ticket, "")){
//                            db.collection("Tickets").document(ticket).get()
//                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                            if(task.isSuccessful()){
//                                                DocumentSnapshot document = task.getResult();
//                                                if(document.exists()){
//                                                    txtATicketProject.setText(document.get("ticketProject").toString());
//                                                    txtATicketDesc.setText(document.get("ticketDesc").toString());
//                                                    txtATicketdate.setText(document.get("ticketModified").toString());
//                                                    txtATicketPr.setText(document.get("ticketPriority").toString());
//                                                    txtATicketSub.setText(document.get("ticketSubject").toString());
//                                                    txtATicketUser.setText(document.get("raisedByUserId").toString());
//
//                                                    btnSolve.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            String s = edtPatch.getText().toString().trim();
//                                                            if(s.isEmpty()){
//                                                                Toast.makeText(DevRecentTicket.this, "Add PatchNote to solve the ticket", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                            else{
//
//                                                                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevRecentTicket.this);
//                                                                builder.setMessage("Release Patch-note?");
//                                                                builder.setPositiveButton("Release", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                        Date c = Calendar.getInstance().getTime();
//                                                                        System.out.println("Current time => " + c);
//                                                                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//                                                                        String formattedDate = df.format(c);
//                                                                        db.collection("Tickets").document(ticket)
//                                                                                .update("ticketStatusByDev","Solved",
//                                                                                        "ticketPatchByDev",edtPatch.getText().toString(),
//                                                                                        "ticketModified",formattedDate
//                                                                                ).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                if(task.isSuccessful()){
//                                                                                    Toast.makeText(DevRecentTicket.this, "Solved!", Toast.LENGTH_SHORT).show();
//                                                                                }
//                                                                                else{
//                                                                                    Toast.makeText(DevRecentTicket.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                                                                                }
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                });
//                                                                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                    }
//                                                                });
//                                                                builder.create().show();
//
//
//
//
//                                                            }
//                                                        }
//                                                    });
//                                                }
//                                                else {
//                                                    cardView.setVisibility(View.GONE);
//                                                    warning.setVisibility(View.VISIBLE);
//                                                }
//                                            }
//                                        }
//                                    });
//                        }
//                        else {
//                            cardView.setVisibility(View.GONE);
//                            warning.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//            }
//        });



    }

    private void initValues() {
        txtATicketProject = findViewById(R.id.txtATicketProject);
        txtATicketSub = findViewById(R.id.txtATicketSub);
        txtATicketPr = findViewById(R.id.txtATicketPr);
        txtATicketDesc = findViewById(R.id.txtATicketDesc);
        txtATicketdate = findViewById(R.id.txtATicketDate);
        txtATicketUser = findViewById(R.id.txtATicketUser);
        edtPatch = findViewById(R.id.edtPatch);
        btnSolve = findViewById(R.id.btnSolve);
        warning = findViewById(R.id.warning);
        imgcompress=findViewById(R.id.imgcompress);
        imgexpand = findViewById(R.id.imgexpand);
        expandedDetails = findViewById(R.id.expandedDetails);
        cardView = findViewById(R.id.cardView2);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}