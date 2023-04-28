package edu.northeastern.numad22fa_team51_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CreateTaskCardActivity extends AppCompatActivity {

    private Dialog progressDialog;

    public Intent intent;
    public String documentId = "";

    private Toolbar toolbar;
    public TextView et_card_name;
    public TextView et_card_notes;
    public Button btn_pick_date;
    public Button btn_clear_date;
    public TextView tv_due_date;
    public EditText et_card_points;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private long lastClickTime = 0;

    public int pointCount = Constants.MIN_POINTS_TASK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task_card);

        setupActionBar();

        intent = getIntent();
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            documentId = intent.getStringExtra(Constants.DOCUMENT_ID);
        }

        et_card_name = findViewById(R.id.et_card_name);
        et_card_notes = findViewById(R.id.et_card_notes);
        btn_pick_date = findViewById(R.id.btn_pick_date);
        tv_due_date = findViewById(R.id.tv_due_date);
        btn_clear_date = findViewById(R.id.btn_clear_date);
        et_card_points = findViewById(R.id.et_card_points);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        updatePointsCount();

        btn_clear_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_due_date.setText("");
                tv_due_date.setHint(R.string.due_date);
            }
        });

        btn_pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog dpl = new DatePickerDialog(CreateTaskCardActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String month_str, date_str, year_str;

                        if ((i1 + 1) < 10){
                            month_str = '0' + String.valueOf(i1 + 1);
                        }else{
                            month_str = String.valueOf(i1 + 1);
                        }

                        if ((i2) < 10){
                            date_str = '0' + String.valueOf(i2);
                        }else{
                            date_str = String.valueOf(i2);
                        }

                        year_str = String.valueOf(i);

                        String selected_date = month_str  + "-" + date_str  + "-" + year_str;

                        Date input = new Date(i, i1, i2);
                        Date today = new Date(year, month, day);

                        if (input.equals(today) || input.after(today)){
                            tv_due_date.setText(selected_date);
                        }else{
                            Toast.makeText(CreateTaskCardActivity.this, "Due date cannot be in the past", Toast.LENGTH_SHORT).show();
                        }
                    }
                },year, month, day);
                dpl.show();
            }
        });
    }


    public void addTaskCardToBoard(View view){

        // prevent redundant network calls being executed multiple times if button is pressed more than once within 1 sec
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        // Add more field checks here if needed
        if(et_card_name.getText() == null || et_card_name.getText().toString().equals("")){
            Toast.makeText(CreateTaskCardActivity.this, "Please enter a name for the task", Toast.LENGTH_SHORT).show();
        }else{
            showProgressDialog("Adding Card");
            createCard();
        }
    }

    public void createCard(){

        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("board_id", documentId);
        hMap.put("card_name", et_card_name.getText().toString());

        if (!et_card_notes.getText().toString().equals("") || et_card_notes.getText() != null){
            hMap.put("card_notes", et_card_notes.getText().toString());
        }else{
            hMap.put("card_notes", "");
        }

        hMap.put("createdBy", firebaseUser.getUid());
        hMap.put("memberList", "");

        if (!tv_due_date.getText().toString().equals("")){
            hMap.put("DueDate", tv_due_date.getText().toString());
        }else{
            hMap.put("DueDate", "");
        }

        if (!et_card_points.getText().toString().equals("") && et_card_points.getText() != null){
            if (Integer.parseInt(et_card_points.getText().toString()) <= Constants.MAX_POINTS_TASK
                    && Integer.parseInt(et_card_points.getText().toString()) >= Constants.MIN_POINTS_TASK){
                hMap.put("points", et_card_points.getText().toString());

            }else{
                Toast.makeText(CreateTaskCardActivity.this, "Points need to be between " + String.valueOf(Constants.MIN_POINTS_TASK) + " and " + String.valueOf(Constants.MAX_POINTS_TASK), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
        }else{
            hMap.put("points", String.valueOf(Constants.MIN_POINTS_TASK));
        }

        hMap.put("isComplete", "false");

        databaseReference.child(Constants.TASKS).child(documentId).push().setValue(hMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateTaskCardActivity.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                        cardCreatedSuccessfully();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateTaskCardActivity.this, "Unable to add task at this moment, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cardCreatedSuccessfully(){
        progressDialog.dismiss();
        finish();
    }

    private void setupActionBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_create_card_activity);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        if (getSupportActionBar() != null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showProgressDialog(String text){
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView progressTV = (TextView) progressDialog.findViewById(R.id.tv_progress_text);
        progressTV.setText(text);
        progressDialog.show();
    }

    public void pointsIncrease(View view){
        if (et_card_points.getText().toString().isEmpty()){
            pointCount = 1000;
        }else{
            pointCount = Integer.parseInt(et_card_points.getText().toString());
        }

        if (pointCount > Constants.MAX_POINTS_TASK){
            pointCount = Constants.MAX_POINTS_TASK;
        }else{
            pointCount = Math.min(Constants.MAX_POINTS_TASK, pointCount + 1);
        }

        updatePointsCount();
    }

    public void pointsDecrease(View view){
        if (et_card_points.getText().toString().isEmpty()){
            pointCount = 0;
        }else{
            pointCount = Integer.parseInt(et_card_points.getText().toString());
        }

        if (pointCount > Constants.MAX_POINTS_TASK){
            pointCount = Constants.MIN_POINTS_TASK;
        }else{
            pointCount = Math.max(Constants.MIN_POINTS_TASK, pointCount - 1);
        }

        updatePointsCount();
    }

    public void updatePointsCount(){
        et_card_points.setText(String.valueOf(pointCount));
    }
}