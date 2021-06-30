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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserMyTickets extends AppCompatActivity {


    public DrawerLayout drawerLayoutUser;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView rcViewUTicket;
    private ImageView noticket;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_tickets);
        noticket = findViewById(R.id.noticket2);
        {

            drawerLayoutUser = findViewById(R.id.drawerLayoutUser2);
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
                            startActivity(new Intent(UserMyTickets.this, UserDashboard.class));
                        finish();
                            break;

                        case R.id.user_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserMyTickets.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(UserMyTickets.this, MainActivity.class));
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
                            startActivity(new Intent(UserMyTickets.this, UserMyTickets.class));
                        finish();
                            break;
//                        case R.id.user_nav_recentTicket:
//                            startActivity(new Intent(UserMyTickets.this, UserRecentTicket.class));
//                            finish();
//                            break;

                        case R.id.user_nav_raise:
                            startActivity(new Intent(UserMyTickets.this, UserRaiseTicket.class));
                            finish();
                            break;
//                        case R.id.user_nav_loginAs:
//
//                            startActivity(new Intent(UserMyTickets.this, MainActivity.class));
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

        rcViewUTicket = findViewById(R.id.rcViewUTicket);
        Query query = db.collection("Tickets").whereEqualTo("raisedByUserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirestoreRecyclerOptions<Ticket> options = new FirestoreRecyclerOptions.Builder<Ticket>()
                .setQuery(query,Ticket.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Ticket, UserMyTickets.ProjectViewHolder>(options) {
            @NonNull
            @Override
            public UserMyTickets.ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_ticket_list,parent,false);
                return new ProjectViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserMyTickets.ProjectViewHolder holder, int position, @NonNull Ticket model) {


                db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                holder.txtUTicketProject.setText(document.get("name").toString());
                            }

                        }
                    }
                });

                db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child(document.get("developer").toString()).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DataSnapshot snapshot = task.getResult();
                                            if(snapshot.exists()){
                                                holder.txtUTicketUser.setText(String.valueOf(snapshot.getValue()));
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }
                });



                holder.txtUTicketSub.setText(model.getTicketSubject());
                holder.txtUTicketDesc.setText(model.getTicketDesc());

                holder.txtUTicketdate.setText(model.getTicketModified());
                holder.txtUTicketPr.setText(model.getTicketPriority());
                holder.txtPatchNote.setText(model.getTicketPatchByDev());


                String sAdmin = model.getTicketStatusByAdmin(),sDev=model.getTicketStatusByDev();

                if(sAdmin.equals("NA")){
                    holder.pending.setVisibility(View.VISIBLE);
                    holder.solved.setVisibility(View.INVISIBLE);
                    holder.unsolved.setVisibility(View.INVISIBLE);
                    holder.discarded.setVisibility(View.INVISIBLE);
                    holder.tpnote.setVisibility(View.GONE);
                    holder.txtPatchNote.setVisibility(View.GONE);
                }
                else if(sAdmin.equals("Discarded")){
                    holder.pending.setVisibility(View.INVISIBLE);
                    holder.solved.setVisibility(View.INVISIBLE);
                    holder.unsolved.setVisibility(View.INVISIBLE);
                    holder.discarded.setVisibility(View.VISIBLE);
                    holder.tpnote.setVisibility(View.GONE);
                    holder.txtPatchNote.setVisibility(View.GONE);
                }
                else{
                    if(sDev.equals("Unsolved")){
                        holder.pending.setVisibility(View.INVISIBLE);
                        holder.solved.setVisibility(View.INVISIBLE);
                        holder.unsolved.setVisibility(View.VISIBLE);
                        holder.discarded.setVisibility(View.INVISIBLE);
                        holder.tpnote.setVisibility(View.GONE);
                        holder.txtPatchNote.setVisibility(View.GONE);
                    }
                    else{
                        holder.pending.setVisibility(View.INVISIBLE);
                        holder.solved.setVisibility(View.VISIBLE);
                        holder.unsolved.setVisibility(View.INVISIBLE);
                        holder.discarded.setVisibility(View.INVISIBLE);
                        holder.tpnote.setVisibility(View.VISIBLE);
                        holder.txtPatchNote.setVisibility(View.VISIBLE);

                    }
                }

                holder.imgdevinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserMyTickets.this);

                        builder.setTitle("Dev Info -");
                        db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()) {

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("Users").child(document.get("developer").toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User user = snapshot.getValue(User.class);
                                                if(user!=null){
                                                    builder.setMessage("Name - " + user.fullname.toString() +
                                                            "\n\nEmail - " + user.email +
                                                            "\n\nPhone - " + user.phone
                                                    );
                                                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    });
                                                    builder.create().show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
                holder.btnDeleteTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserMyTickets.this);
                        builder.setMessage("Are you sure you want to delete this?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().delete();
                                Toast.makeText(UserMyTickets.this, "Removed successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();
                    }
                });

                holder.imgdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserMyTickets.this);
                        builder.setMessage("Are you sure you want to delete this?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().delete();
                                Toast.makeText(UserMyTickets.this, "Removed successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();
                    }
                });

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


            }
            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)noticket.setVisibility(View.VISIBLE);
                else noticket.setVisibility(View.GONE);

            }
        };

        rcViewUTicket.setLayoutManager(new LinearLayoutManager(this));
        rcViewUTicket.setAdapter(adapter);
    }


    static class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView TAG,btnDeleteTicket,txtUTicketProject, txtUTicketSub, txtUTicketUser, txtUTicketPr, txtUTicketdate, txtUTicketDesc,tpnote,txtPatchNote,solved,unsolved,discarded,pending;
        private ImageView imgexpand,imgcompress,imgdelete,imgdevinfo;
        private RelativeLayout expandedDetails,imgs;
        private TextView ad,dev;
        private CardView cardView;
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUTicketProject = itemView.findViewById(R.id.txtUTicketProject);
            txtUTicketSub = itemView.findViewById(R.id.txtUTicketSub);
            txtUTicketPr = itemView.findViewById(R.id.txtUTicketPr);
            txtUTicketDesc = itemView.findViewById(R.id.txtUTicketDesc);
            txtUTicketdate = itemView.findViewById(R.id.txtUTicketDate);
            txtUTicketUser = itemView.findViewById(R.id.txtUTicketUser);
            imgdelete = itemView.findViewById(R.id.imgdelete);
            TAG = itemView.findViewById(R.id.TAG);
            ad = itemView.findViewById(R.id.ad);
            dev = itemView.findViewById(R.id.dev);
            tpnote = itemView.findViewById(R.id.tpnote);
            btnDeleteTicket = itemView.findViewById(R.id.btnDeleteTicket);
            solved = itemView.findViewById(R.id.tag1);
            unsolved = itemView.findViewById(R.id.tag2);
            txtPatchNote = itemView.findViewById(R.id.txtPatchNote);
            discarded = itemView.findViewById(R.id.tag3);
            pending = itemView.findViewById(R.id.tag4);
            imgcompress=itemView.findViewById(R.id.imgcompress);
            imgexpand = itemView.findViewById(R.id.imgexpand);
            imgdevinfo = itemView.findViewById(R.id.imgdevinfo);
            expandedDetails = itemView.findViewById(R.id.expandedDetails);
            cardView = itemView.findViewById(R.id.cardView3);
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