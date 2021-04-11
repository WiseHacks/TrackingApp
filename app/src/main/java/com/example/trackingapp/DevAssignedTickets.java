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

public class DevAssignedTickets extends AppCompatActivity {

    private DrawerLayout drawerLayoutDev;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;

    private FirestoreRecyclerAdapter adapter;
    private RecyclerView rcViewAssign;
    private ImageView noticket;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_assigned_tickets);
        noticket = findViewById(R.id.noticket1);

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
                            startActivity(new Intent(DevAssignedTickets.this,DeveloperDashboard.class));
                            finish();
                            break;
                        case R.id.dev_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevAssignedTickets.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(DevAssignedTickets.this, MainActivity.class));
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
//                            startActivity(new Intent(DevAssignedTickets.this,DevRecentTicket.class));
//                            finish();
//                            break;
                        case R.id.dev_nav_assignedTickets:
                            startActivity(new Intent(DevAssignedTickets.this,DevAssignedTickets.class));
                            finish();
                            break;
                        case R.id.dev_nav_project:
                            startActivity(new Intent(DevAssignedTickets.this,DevMyProjects.class));
                            finish();
                            break;

                        default:
                            break;
                    }
                    return true;
                }
            });


        }

        rcViewAssign = findViewById(R.id.rcViewAssign);
        Query query = db.collection("Tickets").whereEqualTo("assignedToDevId",FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("ticketStatusByDev","Unsolved");
        FirestoreRecyclerOptions<Ticket> options = new FirestoreRecyclerOptions.Builder<Ticket>()
                .setQuery(query,Ticket.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Ticket, DevAssignedTickets.ProjectViewHolder>(options) {
            @NonNull
            @Override
            public DevAssignedTickets.ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_tickets,parent,false);
                return new ProjectViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DevAssignedTickets.ProjectViewHolder holder, int position, @NonNull Ticket model) {


                db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                holder.txtATicketProject.setText(document.get("name").toString());
                            }

                        }
                    }
                });
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Users").child(model.getRaisedByUserId()).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot = task.getResult();
                            if(snapshot.exists()){
                                holder.txtATicketUser.setText(String.valueOf(snapshot.getValue()));
                            }
                        }
                    }
                });
                holder.txtATicketSub.setText(model.getTicketSubject());
                holder.txtATicketDesc.setText(model.getTicketDesc());

                holder.txtATicketdate.setText(model.getTicketModified());
                holder.txtATicketPr.setText(model.getTicketPriority());

                holder.imgexpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionManager.beginDelayedTransition(holder.cardView);
                        holder.expandedDetails.setVisibility(View.VISIBLE);
                        holder.imgexpand.setVisibility(View.GONE);
                        holder.imgcompress.setVisibility(View.VISIBLE);
                    }
                });

                holder.imgcompress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        TransitionManager.beginDelayedTransition(holder.cardView);
                        holder.expandedDetails.setVisibility(View.GONE);
                        holder.imgexpand.setVisibility(View.VISIBLE);
                        holder.imgcompress.setVisibility(View.GONE);

                    }
                });

                holder.btnSolve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = holder.edtPatch.getText().toString().trim();
                        if(s.isEmpty()){
                            Toast.makeText(DevAssignedTickets.this, "Add PatchNote to solve the ticket", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevAssignedTickets.this);
                            builder.setMessage("Release Patch-note?");
                            builder.setPositiveButton("Release", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Date c = Calendar.getInstance().getTime();
                                    System.out.println("Current time => " + c);
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                    String formattedDate = df.format(c);
                                    db.collection("Tickets").document(getSnapshots().getSnapshot(position).getId().toString())
                                            .update("ticketStatusByDev","Solved",
                                                    "ticketPatchByDev",holder.edtPatch.getText().toString(),
                                                    "ticketModified",formattedDate
                                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(DevAssignedTickets.this, "Solved!", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(DevAssignedTickets.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.create().show();




                        }
                    }
                });
//                holder.btnDiscardTicket.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
//                        builder.setMessage("Are you sure you want to discard this?");
//                        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                db.collection("Tickets").document(getSnapshots()
//                                        .getSnapshot(position).getId()).update("ticketStatusByAdmin","Discarded");
//                            }
//                        });
//                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        });
//                        builder.create().show();
//                    }
//                });
//                holder.btnAssignTicket.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
//                        builder.setMessage("Assign to developer?");
//                        builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if(task.isSuccessful()){
//                                            DocumentSnapshot document = task.getResult();
//                                            if(document.exists()){
//                                                Date c = Calendar.getInstance().getTime();
//                                                System.out.println("Current time => " + c);
//                                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//                                                String formattedDate = df.format(c);
//                                                db.collection("Tickets").document(getSnapshots()
//                                                        .getSnapshot(position).getId()).update("ticketStatusByAdmin","Assigned",
//                                                        "assignedToDevId",document.get("developer"),
//                                                        "ticketStatusByDev","Unsolved",
//                                                        "ticketModified",formattedDate);
//
//                                                db.collection("Developer").document(document.get("developer").toString())
//                                                        .update("RecentTicket",getSnapshots().getSnapshot(position).getId().toString());
//
//
//                                            }
//                                        }
//                                    }
//                                });
//
//                            }
//                        });
//                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        });
//                        builder.create().show();
//                    }
//                });
            }
            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)noticket.setVisibility(View.VISIBLE);
                else noticket.setVisibility(View.GONE);

            }
        };

        rcViewAssign.setLayoutManager(new LinearLayoutManager(this));
        rcViewAssign.setAdapter(adapter);

    }


    static class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView txtATicketProject, txtATicketSub, txtATicketUser, txtATicketPr, txtATicketdate, txtATicketDesc;
        private ImageView imgexpand,imgcompress;
        private RelativeLayout expandedDetails,imgs;
        private EditText edtPatch;
        private Button btnSolve;
        private CardView cardView;
        private ImageView btnDiscardTicket,btnAssignTicket;
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            txtATicketProject = itemView.findViewById(R.id.txtATicketProject);
            txtATicketSub = itemView.findViewById(R.id.txtATicketSub);
            txtATicketPr = itemView.findViewById(R.id.txtATicketPr);
            txtATicketDesc = itemView.findViewById(R.id.txtATicketDesc);
            txtATicketdate = itemView.findViewById(R.id.txtATicketDate);
            txtATicketUser = itemView.findViewById(R.id.txtATicketUser);
            edtPatch = itemView.findViewById(R.id.edtPatch);
            btnSolve = itemView.findViewById(R.id.btnSolve);

            imgcompress=itemView.findViewById(R.id.imgcompress);
            imgexpand = itemView.findViewById(R.id.imgexpand);
            expandedDetails = itemView.findViewById(R.id.expandedDetails);
            cardView = itemView.findViewById(R.id.cardView2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}