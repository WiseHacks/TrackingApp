package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AdminDashboard extends AppCompatActivity {


    private FirestoreRecyclerAdapter adapter;
    private RecyclerView rcViewTicket;
    private ImageView noticket;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        rcViewTicket = findViewById(R.id.rcViewTicketAdmin);
        noticket = findViewById(R.id.noticket);
        Query query = db.collection("Tickets").whereEqualTo("ticketStatusByAdmin","NA");
        FirestoreRecyclerOptions<Ticket> options = new FirestoreRecyclerOptions.Builder<Ticket>()
                .setQuery(query,Ticket.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Ticket, AdminDashboard.ProjectViewHolder>(options) {
            @NonNull
            @Override
            public AdminDashboard.ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_list,parent,false);
                return new ProjectViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AdminDashboard.ProjectViewHolder holder, int position, @NonNull Ticket model) {


                db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                holder.txtTicketProject.setText(document.get("name").toString());
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child(document.get("developer").toString()).child("fullname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DataSnapshot snapshot = task.getResult();
                                            if(snapshot.exists()){
                                                holder.txtTicketDev.setText(String.valueOf(snapshot.getValue()));
                                            }
                                        }
                                    }
                                });
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
                                holder.txtTicketUser.setText(String.valueOf(snapshot.getValue()));
                            }
                        }
                    }
                });
                holder.txtTicketSub.setText(model.getTicketSubject());
                holder.txtTicketDesc.setText(model.getTicketDesc());

                holder.txtTicketdate.setText(model.getTicketModified());
                holder.txtTicketPr.setText(model.getTicketPriority());

                holder.imgexpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionManager.beginDelayedTransition(holder.cardView);
                        holder.expandedDetails.setVisibility(View.VISIBLE);
                        holder.imgexpand.setVisibility(View.GONE);
                        holder.imgcompress.setVisibility(View.VISIBLE);
                        holder.imgs.setVisibility(View.GONE);
                    }
                });

                holder.imgcompress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionManager.beginDelayedTransition(holder.cardView);
                        holder.expandedDetails.setVisibility(View.GONE);
                        holder.imgexpand.setVisibility(View.VISIBLE);
                        holder.imgcompress.setVisibility(View.GONE);
                        holder.imgs.setVisibility(View.VISIBLE);

                    }
                });

                holder.btnDiscardTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
                        builder.setMessage("Are you sure you want to discard this?");
                        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Tickets").document(getSnapshots()
                                .getSnapshot(holder.getAdapterPosition()).getId()).update("ticketStatusByAdmin","Discarded");
                                Toast.makeText(AdminDashboard.this, "Discarded", Toast.LENGTH_SHORT).show();
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
                holder.btnAssignTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
                        builder.setMessage("Assign to developer?");
                        builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Projects").document(model.getTicketProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()){
                                                Date c = Calendar.getInstance().getTime();
                                                System.out.println("Current time => " + c);
                                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                                String formattedDate = df.format(c);
                                                Log.d("divyesh","notes - "+document.get("developer"));
                                                db.collection("Tickets").document(getSnapshots()
                                                        .getSnapshot(holder.getAdapterPosition()).getId()).update("ticketStatusByAdmin","Assigned",
                                                        "assignedToDevId",document.get("developer"),
                                                        "ticketStatusByDev","Unsolved",
                                                        "ticketModified",formattedDate);
//                                                db.collection("Developer").document(document.get("developer").toString())
//                                                        .update("RecentTicket",getSnapshots().getSnapshot(position).getId().toString());
                                                Toast.makeText(AdminDashboard.this, "Assigned", Toast.LENGTH_SHORT).show();

                                            }
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
                });
            }
            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)noticket.setVisibility(View.VISIBLE);
                else noticket.setVisibility(View.GONE);

            }
        };

        rcViewTicket.setLayoutManager(new LinearLayoutManager(this));
        rcViewTicket.setAdapter(adapter);




    }

    private class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTicketProject,txtTicketSub,txtTicketUser,txtTicketPr,txtTicketdate,txtTicketDesc,txtTicketDev;
        private ImageView imgexpand,imgcompress;
        private RelativeLayout expandedDetails,imgs;
        private CardView cardView;
        private ImageView btnDiscardTicket,btnAssignTicket;
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTicketProject = itemView.findViewById(R.id.txtTicketProject);
            txtTicketSub = itemView.findViewById(R.id.txtTicketSub);
            txtTicketPr = itemView.findViewById(R.id.txtTicketPr);
            txtTicketDesc = itemView.findViewById(R.id.txtTicketDesc);
            txtTicketdate = itemView.findViewById(R.id.txtTicketDate);
            txtTicketUser = itemView.findViewById(R.id.txtTicketUser);
            txtTicketDev = itemView.findViewById(R.id.txtTicketDev);

            btnDiscardTicket = itemView.findViewById(R.id.btnDiscardTicket);
            btnAssignTicket = itemView.findViewById(R.id.btnAssignTicket);
            imgcompress=itemView.findViewById(R.id.imgcompress);
            imgexpand = itemView.findViewById(R.id.imgexpand);
            expandedDetails = itemView.findViewById(R.id.expandedDetails);
            cardView = itemView.findViewById(R.id.cardView1);
            imgs = itemView.findViewById(R.id.imgs);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.btnAdminLogout:
                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);
                builder.setMessage("Log out from the application?");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(AdminDashboard.this, MainActivity.class));
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
            default:break;
        }
        return true;
    }
}