package com.example.trackingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemWrapperICS;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.example.trackingapp.R.layout.activity_listview;

public class DevMyProjects extends AppCompatActivity {

    private DrawerLayout drawerLayoutDev;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton btnAddProject;
    private RecyclerView rcViewProject;
    private ArrayList<String> pids;
    private ImageView panda;

    private FirestoreRecyclerAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_my_projects);

        initValues();
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
                            startActivity(new Intent(DevMyProjects.this,DeveloperDashboard.class));
                            finish();
                            break;
                        case R.id.dev_nav_logOut:
                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevMyProjects.this);
                            builder.setMessage("Log out from the application?");
                            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(DevMyProjects.this, MainActivity.class));
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
//                            startActivity(new Intent(DevMyProjects.this,DevRecentTicket.class));
//                            finish();
//                            break;
                        case R.id.dev_nav_assignedTickets:
                            startActivity(new Intent(DevMyProjects.this,DevAssignedTickets.class));
                            finish();
                            break;

                        case R.id.dev_nav_project:
                            startActivity(new Intent(DevMyProjects.this,DevMyProjects.class));
                            finish();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });


        }

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DevMyProjects.this,DevAddProject.class));
            }
        });



        // Query,RecyclerOptions,View Holder;

        Query query = db.collection("Projects").whereEqualTo("developer",
                FirebaseAuth.getInstance().getCurrentUser().getUid().toString());



        FirestoreRecyclerOptions<Project> options = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query,Project.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Project, ProjectViewHolder>(options) {

            @NonNull
            @Override
            public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list,parent,false);
                return new ProjectViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProjectViewHolder holder, int position, @NonNull Project model) {


                holder.txtProjectName.setText(model.getName());
                holder.txtProjectDate.setText(model.getDate());
                holder.txtProjectDesc.setText(model.getDescription());
                holder.txtProjectKey.setText(getSnapshots().getSnapshot(position).getId().toString());
//                ArrayList<String> devTeam = model.getTeam();
                ArrayList<String> devTeamName = new ArrayList<>();
                Map<String,String> Team = model.getTeam();
//                ArrayAdapter<String > adapterTeam | model.getteambynames;

                for (Map.Entry<String,String> p:
                     Team.entrySet()) {
                    devTeamName.add(p.getValue());
                }

                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(DevMyProjects.this, activity_listview, R.id.txtitem,devTeamName);
                holder.ProjectTeamMembers.setAdapter(listAdapter);
                justifyListViewHeightBasedOnChildren(holder.ProjectTeamMembers);




                holder.imgexpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        TransitionManager.beginDelayedTransition(holder.cardView);
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
//                        TransitionManager.beginDelayedTransition(holder.cardView);
                        TransitionManager.beginDelayedTransition(holder.cardView);
                        holder.expandedDetails.setVisibility(View.GONE);
                        holder.imgexpand.setVisibility(View.VISIBLE);
                        holder.imgcompress.setVisibility(View.GONE);
                        holder.imgs.setVisibility(View.VISIBLE);

                    }
                });

                holder.btnDeleteProject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevMyProjects.this);
                        builder.setMessage("Are you sure you want to delete this?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Developer").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .update("MyProjects",FieldValue.arrayRemove(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId()));
                                db.collection("Tickets").whereEqualTo("ticketProjectId",getSnapshots().getSnapshot(holder.getAdapterPosition()).getId().toString())
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(QueryDocumentSnapshot document : task.getResult()){
//                                                Log.d("divyesh",document.getId());
                                                if(document.exists()) {
                                                    db.collection("Tickets").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                })
                                ;

                                getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().delete();


                                adapter.notifyDataSetChanged();

                                Toast.makeText(DevMyProjects.this, "Removed successfully", Toast.LENGTH_SHORT).show();
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

//                holder.ProjectTeamMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(DevMyProjects.this);
//                        builder.setMessage(devTeam.get(position) + " -");
//                        builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO: 29-06-2021 So Detail or Delete on tap;
//                            }
//                        });
//                        builder.create().show();
//                    }
//                });

            }

            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)panda.setVisibility(View.VISIBLE);
                else panda.setVisibility(View.GONE);

            }




        };

        rcViewProject.setLayoutManager(new LinearLayoutManager(this));
        rcViewProject.setAdapter(adapter);
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                adapter.deleteItem(viewHolder.getAdapterPosition());
//            }
//        }).attachToRecyclerView(rcViewProject);






    }

    private void initValues() {
        btnAddProject = findViewById(R.id.btnAddProject);
        rcViewProject = findViewById(R.id.rcViewDevProject);
        panda = findViewById(R.id.panda);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView txtProjectName,txtProjectDate,txtProjectDesc,txtProjectKey/*, ProjectTeamMembers*/;
        private ImageView imgexpand,imgcompress;
        private RelativeLayout expandedDetails,imgs;
        private CardView cardView;
        private ImageView btnDeleteProject;
        private ListView ProjectTeamMembers;
//        private LinearLayout ProjectTeamMembers;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProjectDate = itemView.findViewById(R.id.txtProjectDate);
            txtProjectDesc = itemView.findViewById(R.id.txtProjectDesc);
            txtProjectName = itemView.findViewById(R.id.txtProjectName);
            txtProjectKey = itemView.findViewById(R.id.txtProjectKey);
            imgcompress=itemView.findViewById(R.id.imgcompress);
            imgexpand = itemView.findViewById(R.id.imgexpand);
            expandedDetails = itemView.findViewById(R.id.expandedDetails);
            cardView = itemView.findViewById(R.id.cardView);
            imgs = itemView.findViewById(R.id.imgs);
//            ProjectTeamMembers  =itemView.findViewById(R.id.ProjectTeamMembers);
            ProjectTeamMembers = itemView.findViewById(R.id.ProjectTeamMembers);
            btnDeleteProject = itemView.findViewById(R.id.btnDeleteProject);
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

}
