package edu.northeastern.numad22fa_team51_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private long lastClickTime = 0;
    private EditText username_edit_text;
    private EditText email_edit_text;
    private EditText password_edit_text;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onResume() {
        super.onResume();

        username_edit_text = findViewById(R.id.user_name_register_edit_text);
        email_edit_text = findViewById(R.id.email_id_register_edit_text);
        password_edit_text = findViewById(R.id.password_register_edit_text);
    }

    public void registerFlow(View view){

        // prevent redundant network calls being executed multiple times if button is pressed more than once within 1 sec
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        String userName = username_edit_text.getText().toString().trim();
        String userEmail = email_edit_text.getText().toString().trim();
        String userPasswd = password_edit_text.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPasswd)){
            Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }else if (userName.contains(" ")){
            Toast.makeText(SignUpActivity.this, "Username cannot have spaces", Toast.LENGTH_SHORT).show();
        }
        else if (userEmail.contains(" ")){
            Toast.makeText(SignUpActivity.this, "Email cannot have spaces", Toast.LENGTH_SHORT).show();
        }
        else{
            registerUserOnFirebase(userName, userEmail, userPasswd);
        }
    }

    private void registerUserOnFirebase(String userName, String userEmail, String userPasswd){
        if (userName.isEmpty()){
            username_edit_text.setError("Username cannot be empty");
        }
        else if (userEmail.isEmpty()){
            email_edit_text.setError("Email cannot be empty");
        }
        else if (userPasswd.isEmpty()) {
            password_edit_text.setError("Password cannot be empty");
        }
        else{
            mAuth.createUserWithEmailAndPassword(userEmail, userPasswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();

                        HashMap<String, String> hMap = new HashMap<>();
                        hMap.put("user_id", uid);
                        hMap.put("user_name", userName);
                        hMap.put("user_email", userEmail);
                        hMap.put("user_passwd", userPasswd);
                        hMap.put("user_img", " ");
                        hMap.put("user_mobile", "0");
                        hMap.put("user_points", "0.0");
                        hMap.put("user_tasks_completed", "0");

                        reference.child("Users").child(uid).setValue(hMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(SignUpActivity.this, "Registration Failed. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}