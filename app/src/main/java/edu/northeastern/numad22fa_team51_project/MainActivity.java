package edu.northeastern.numad22fa_team51_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private long lastClickTime = 0;
    private EditText email_edit_text;
    private EditText password_edit_text;
    private Button sign_in_button;
    private Button sign_up_button;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //private void setupActionBar(){} //if more features needed
        getSupportActionBar().setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        email_edit_text = findViewById(R.id.email_address_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);
        sign_in_button = findViewById(R.id.sign_in_button);
        sign_up_button = findViewById(R.id.sign_up_button);
    }

    public void signInFlow(View view){

        // prevent redundant network calls being executed multiple times if button is pressed more than once within 1 sec
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        String user = email_edit_text.getText().toString().trim();
        String passwd = password_edit_text.getText().toString().trim();

        if (user.isEmpty()){
            email_edit_text.setError("Email cannot be empty");
        }
        else if (passwd.isEmpty()){
            password_edit_text.setError("Password cannot be empty");
        }
        else{
            mAuth.signInWithEmailAndPassword(user,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        setAlarm();
                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, "Login Failed. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void signUpFlow(View view){
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    //Referred https://developer.android.com/develop/ui/views/notifications/build-notification#java
    public void setAlarm(){
        createNotificationChannel();
        Intent intent=new Intent(MainActivity.this,NotificationAlarm.class);
        Intent dueDateIntent=new Intent(MainActivity.this,DueDateNotificationAlarm.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(MainActivity.this,1,intent,PendingIntent.FLAG_IMMUTABLE);
        PendingIntent dueDatePIntent=PendingIntent.getBroadcast(MainActivity.this,2,dueDateIntent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm=(AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,8);
        cal.set(Calendar.MINUTE,00);
        cal.set(Calendar.SECOND,1);

        alarm.setWindow(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pIntent);
        cal.set(Calendar.HOUR_OF_DAY,10);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,10);

        alarm.setWindow(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,dueDatePIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Check out your tasks";
            String description = "Check the to-do tasks inside the app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("task_notification", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}