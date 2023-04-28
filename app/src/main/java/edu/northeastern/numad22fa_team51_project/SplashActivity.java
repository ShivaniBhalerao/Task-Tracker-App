package edu.northeastern.numad22fa_team51_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);

        if (checkIfFirebaseUser()){
            intent = new Intent(SplashActivity.this, DashboardActivity.class);
        }
        else{
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        final Runnable r = new Runnable() {
            public void run() {
                startActivity(intent);
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(r, 1500);
    }

    private boolean checkIfFirebaseUser(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null){
            return false;
        }

        return true;
    }
}