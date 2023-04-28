package edu.northeastern.numad22fa_team51_project;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.numad22fa_team51_project.adapters.MemberListItemAdapter;
import edu.northeastern.numad22fa_team51_project.models.UserModel;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MembersActivity extends AppCompatActivity {

    Intent intent;
    String groupId;
    RecyclerView memberRecycler;
    private DatabaseReference databaseReference;
    public ArrayList<UserModel> users;
    private Dialog progressDialog;
    MemberListItemAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        setupActionBar();
        intent = getIntent();
        memberRecycler = findViewById(R.id.rv_members_list);
        memberRecycler.setLayoutManager(new LinearLayoutManager(this));
        memberRecycler.setHasFixedSize(true);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(memberRecycler);


        if (intent.hasExtra(Constants.BOARD_DETAILS)) {
            groupId = intent.getStringExtra(Constants.BOARD_DETAILS);
            getBoardMembersAssignedDetails(groupId);
        }
    }


    private void setupActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_members_activity);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        if (getSupportActionBar() != null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Members");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getUsersListAssigned(ArrayList<String> assignToArrayList){
        users = new ArrayList<UserModel>();
        for (String userUid: assignToArrayList) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapShot) {
                    if (datasnapShot.hasChildren()) {
                        String user_email = datasnapShot.child("user_email").getValue().toString();
                        String user_id = datasnapShot.child("user_id").getValue().toString();
                        String user_img = datasnapShot.child("user_img").getValue().toString();
                        String user_mobile = datasnapShot.child("user_mobile").getValue().toString();
                        String user_name = datasnapShot.child("user_name").getValue().toString();
                        String user_passwd = datasnapShot.child("user_passwd").getValue().toString();
                        String user_points = datasnapShot.child("user_points").getValue().toString();
                        String user_tasks_completed = datasnapShot.child("user_tasks_completed").getValue().toString();
                        UserModel user = new UserModel(user_email, user_id, user_name, user_passwd, user_img, user_mobile, user_points, user_tasks_completed);
                        users.add(user);
                        adapter = new MemberListItemAdapter(MembersActivity.this, users);
                        memberRecycler.setAdapter(adapter);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MembersActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getBoardMembersAssignedDetails(String groupId){
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.BOARDS).child(groupId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapShot) {
                if (datasnapShot.hasChildren()) {
                    DataSnapshot assignedToSnapShot = datasnapShot.child("group_assignedTo");
                    String assignTo = assignedToSnapShot.getValue().toString();
                    String[] assignToList = assignTo.split(",");
                    ArrayList<String> assignToArrayList = new ArrayList<String>(
                            Arrays.asList(assignToList));
                    getUsersListAssigned(assignToArrayList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MembersActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_member){
            dialogSearchMember();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogSearchMember(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_search_member);
        TextView add_button = (TextView) dialog.findViewById(R.id.tv_add);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) dialog.findViewById(R.id.et_email_search_member);
                if (!editText.getText().toString().isEmpty()){
                    showProgressDialog("Please Wait");
                    //TODO: Handle addtion of the members here.
                    addNewMember(editText.getText().toString());
                    progressDialog.dismiss();
                    dialog.dismiss();
                } else{
                    Toast.makeText(MembersActivity.this, "Please enter member email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView cancel_button = (TextView) dialog.findViewById(R.id.tv_cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void addNewMember(String newMemberEmail){
        // Check for if user is already a member.
        for (UserModel user: users){
            if (user.getUser_email().equals(newMemberEmail)){
                Toast.makeText(MembersActivity.this, "Email provided is already a member", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapShot) {
                Boolean found = false;
                for (DataSnapshot snapshot : datasnapShot.getChildren()) {
                    String email = snapshot.child("user_email").getValue().toString();
                    if (email.equals(newMemberEmail)){
                        found = true;
                        addNewMemberToBoard(snapshot.child("user_id").getValue().toString());
                    }
                }
                if (!found) {
                    Toast.makeText(MembersActivity.this, "User Email not registered. Please check your email again", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MembersActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addNewMemberToBoard(String user_id) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.BOARDS).child(groupId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapShot) {
                if (datasnapShot.hasChildren()){
                    DataSnapshot assignedToSnapShot = datasnapShot.child("group_assignedTo");
                    String assignTo = assignedToSnapShot.getValue().toString();
                    String[] assignToList = assignTo.split(",");
                    ArrayList<String> assignToArrayList = new ArrayList<String>(
                            Arrays.asList(assignToList));
                    assignToArrayList.add(user_id);
                    String StringList = "";
                    for (int i = 0; i < assignToArrayList.size(); i++) {
                        StringList += assignToArrayList.get(i) + ",";
                    }
                    Map<String, Object> update = new HashMap<>();
                    updateDB(datasnapShot.getRef(), StringList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MembersActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDB(DatabaseReference ref, String stringList) {
        ref.child("group_assignedTo").setValue(stringList);
    }

    private void showProgressDialog(String text){
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView progressTV = (TextView) progressDialog.findViewById(R.id.tv_progress_text);
        progressTV.setText(text);
        progressDialog.show();
    }

    ItemTouchHelper.SimpleCallback itemTouchHelper=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setTitle("Remove Group Member")
                    .setMessage("Are you sure you want to remove this group member?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserModel user_model = users.get(viewHolder.getAdapterPosition());
                            users.remove(viewHolder.getAdapterPosition());
                            removeMemberFromDataBase(user_model.getUser_id());
                            adapter.notifyDataSetChanged();
                            if (Objects.equals(user_model.getUser_id(), firebaseUser.getUid())){
                                finish();
                            }
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MembersActivity.this, R.color.deleteColor))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(MembersActivity.this, R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void removeMemberFromDataBase(String user_id) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.BOARDS).child(groupId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapShot) {
                DataSnapshot assignedToSnapShot = datasnapShot.child("group_assignedTo");
                String assignTo = assignedToSnapShot.getValue().toString();
                String[] assignToList = assignTo.split(",");
                ArrayList<String> assignToArrayList = new ArrayList<String>(
                        Arrays.asList(assignToList));
                assignToArrayList.remove(user_id);
                String StringList = "";
                for (int i = 0; i < assignToArrayList.size(); i++) {
                    StringList += assignToArrayList.get(i) + ",";
                }
                updateDB(datasnapShot.getRef(), StringList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MembersActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
            }

        });
    }

}