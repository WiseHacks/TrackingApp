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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.view.View.GONE;

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
                //----------------------------------------------------
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> uids = new ArrayList<>();
                ArrayAdapter<String> listadapter = new ArrayAdapter<String>(DevAssignedTickets.this,R.layout.activity_listview,R.id.txtitem,names);
                holder.teamlist.setAdapter(listadapter);
                //----------------------------------------------------
                if(!model.getAssignedToDevId().equals(model.getHeadDev())){ // Team member can't assign tickets
                    holder.teamAssign.setVisibility(GONE); // assign button is gone lol
                }
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

                holder.imguserinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevAssignedTickets.this);

                        builder.setTitle("User Info -");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Users").child(model.getRaisedByUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                });
                holder.imgexpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransitionManager.beginDelayedTransition(holder.cardView);
                        holder.expandedDetails.setVisibility(View.VISIBLE);
                        holder.imgexpand.setVisibility(GONE);
                        holder.imgcompress.setVisibility(View.VISIBLE);
                    }
                });


                holder.imgcompress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.expandedDetails.setVisibility(GONE);
                        holder.imgexpand.setVisibility(View.VISIBLE);
                        holder.imgcompress.setVisibility(GONE);

                    }
                });
                db.collection("Projects").whereEqualTo("developer",model.getAssignedToDevId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if(document.exists()){
                                Map<String,String > MemberList = (Map<String, String>) document.get("team");
                                for(Map.Entry<String,String> p: MemberList.entrySet()){
                                    names.add(p.getValue());
                                    uids.add(p.getKey());
                                    listadapter.notifyDataSetChanged();
                                }
                                justifyListViewHeightBasedOnChildren(holder.teamlist);
                            }
                        }
                    }
                });
                holder.teamAssign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.teamDetails.getVisibility()==GONE){
                            TransitionManager.beginDelayedTransition(holder.cardView);
                            holder.teamDetails.setVisibility(View.VISIBLE);
                        }
                        else{
                            holder.teamDetails.setVisibility(GONE);
                        }
                    }
                });
                /*if(model.getAssignedToDevId().equals(model.getHeadDev())){
                    db.collection("Projects").document(model.getAssignedToDevId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()) {
                                    Map<String, String> totalMembers = (Map<String, String>) document.get("team");

                                }
                            }
                            else{
                                Toast.makeText(DevAssignedTickets.this, "something went wrong   ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    holder.teamAssign.setVisibility(GONE);
                }*/
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
                                    db.collection("Tickets").document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId().toString())
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

                holder.teamlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevAssignedTickets.this);
                        builder.setMessage("Assign?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Tickets").document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId())
                                        .update("assignedToDevId", uids.get(position));
                                Toast.makeText(DevAssignedTickets.this, "Assigned", Toast.LENGTH_SHORT).show();

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();

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
                else noticket.setVisibility(GONE);

            }
        };

        rcViewAssign.setLayoutManager(new LinearLayoutManager(this));
        rcViewAssign.setAdapter(adapter);

    }


    static class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView txtATicketProject, txtATicketSub, txtATicketUser, txtATicketPr, txtATicketdate, txtATicketDesc, teamAssign;
        private ImageView imgexpand,imgcompress,imguserinfo;
        private RelativeLayout expandedDetails,imgs,teamDetails;
        private EditText edtPatch;
        private Button btnSolve;
        private CardView cardView;
        private ImageView btnDiscardTicket,btnAssignTicket;
        private ListView teamlist;
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
            imguserinfo = itemView.findViewById(R.id.imguserinfo);
            imgcompress=itemView.findViewById(R.id.imgcompress);
            imgexpand = itemView.findViewById(R.id.imgexpand);
            teamAssign = itemView.findViewById(R.id.teamAssign);
            expandedDetails = itemView.findViewById(R.id.expandedDetails);
            teamDetails = itemView.findViewById(R.id.teamDetails);
            teamlist = itemView.findViewById(R.id.teamlist);
            cardView = itemView.findViewById(R.id.cardView2);
        }
    }

    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
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