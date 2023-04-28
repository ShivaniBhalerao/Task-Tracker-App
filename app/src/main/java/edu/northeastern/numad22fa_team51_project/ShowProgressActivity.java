package edu.northeastern.numad22fa_team51_project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


//Referred from: https://www.geeksforgeeks.org/how-to-add-a-pie-chart-into-an-android-application/
public class ShowProgressActivity extends AppCompatActivity {

    TextView tvCompleted, tvNotCompleted;
    PieChart pieChart;
    private Toolbar toolbar;
    private DrawerLayout menuDrawer;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    TextView taskCompletedTV;
    TextView pointsEarnedTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_progress);

        pieChart = findViewById(R.id.piechart);
        taskCompletedTV=findViewById(R.id.task_complete_tv);
        pointsEarnedTV=findViewById(R.id.points_earned_tv);
        int taskCompleted=0;
        int taskIncomplete=0;
        getFirebaseUserData();

        getSupportActionBar().setTitle("Progress Tracker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getFireBaseActiveTask(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TASKS);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int TotalActiveTask = 0;
                int TotalActiveTaskCompleted = 0;
                for (DataSnapshot s: snapshot.getChildren()){
                    for (DataSnapshot value: s.getChildren()){
                        String members = value.child("memberList").getValue().toString();
                        String isCompleted = value.child("isComplete").getValue().toString();
                        ArrayList<String> membersList = convertStringToArrayList(members);
                        if (membersList.contains(userId)) {
                            TotalActiveTask += 1;
                            if (isCompleted.equals("true")) {
                                TotalActiveTaskCompleted += 1;
                            }
                        }
                    }
                }

                setData(TotalActiveTaskCompleted,TotalActiveTask-TotalActiveTaskCompleted);
                Log.d("getFireBaseActiveTask-TotalActiveTask", String.valueOf(TotalActiveTask));
                Log.d("getFireBaseActiveTask-TotalActiveTaskCompleted", String.valueOf(TotalActiveTaskCompleted));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<String> convertStringToArrayList(String list){
        String[] assignToList = list.split(",");
        ArrayList<String> assignToArrayList = new ArrayList<String>(Arrays.asList(assignToList));
        return assignToArrayList;
    }



    public void setData(int complete, int incomplete){
        pieChart.addPieSlice(
                new PieModel(
                        "Tasks Completed",
                        complete,
                        Color.parseColor("#29B696")));
        pieChart.addPieSlice(
                new PieModel(
                        "Active Tasks",
                        incomplete,
                        Color.parseColor("#995350")));

        pieChart.startAnimation();
    }


    public void getFirebaseUserData(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.child("user_id").getValue().toString();

                float points = Float.parseFloat(snapshot.child("user_points").getValue().toString());
                String pointsRound=String.format("%.2f",points);
                pointsEarnedTV.setText("Points Earned: "+pointsRound);
                String taskCompleted = snapshot.child("user_tasks_completed").getValue().toString();
                taskCompletedTV.setText("Tasks Completed: "+taskCompleted);
                getFireBaseActiveTask(userId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowProgressActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}