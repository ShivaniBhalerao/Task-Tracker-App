package edu.northeastern.numad22fa_team51_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import edu.northeastern.numad22fa_team51_project.models.UserModel;

public class MyProfileActivity extends AppCompatActivity {

    private Intent intent;
    private Uri selectedImageUri = null;
    public UserModel curr_user;
    private ImageView user_img;
    private EditText user_name;
    private EditText user_mobile;
    private EditText user_email;
    private EditText user_pass;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String UserImageURI = "";
    private ImageView imageView_Delete;
    private ImageView imageView_Camera;
    private Button update_my_profile_button;
    private Dialog progressDialog;
    private boolean deleted = false;
    private AlertDialog alert;
    public Intent cameraIntent;
    public Bitmap cameraImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFirebaseUserData();

        update_my_profile_button = (Button) findViewById(R.id.update_my_profile_button);
        user_img = (ImageView) findViewById(R.id.my_profile_img_view);
        user_name = (EditText) findViewById(R.id.user_name_my_profile_edit_text);
        user_mobile = (EditText) findViewById(R.id.mobile_my_profile_edit_text);
        user_email = (EditText) findViewById(R.id.email_id_my_profile_edit_text);
        user_pass = (EditText) findViewById(R.id.password_my_profile_edit_text);
        imageView_Delete = findViewById(R.id.imageView_Delete);
        imageView_Camera = findViewById(R.id.imageView_Camera);

        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractedCheckSelfPermission();
            }
        });

        imageView_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyProfileActivity.this, "Image Removed",Toast.LENGTH_SHORT).show();
                user_img.setImageResource(R.drawable.avatar_1);
                selectedImageUri = null;
                deleted = true;
            }
        });

        imageView_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if ((ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                    startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
                }else{
                    askCameraPermission();
                }
            }
        });

        update_my_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri != null){
                    uploadBoardImage();
                }else{
                    setFirebaseUserData();
                }
            }
        });
    }

    private void askCameraPermission() {
        // check for permission the very first time
        if (ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            // disallowed once, show permission rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(MyProfileActivity.this, Manifest.permission.CAMERA)){
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setMessage("Access to camera is required for this function, else select image from local storage by tapping the image above!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // ask again for permission
                                ActivityCompat.requestPermissions(MyProfileActivity.this, new String[] {Manifest.permission.CAMERA}, Constants.CAMERA_REQUEST_CODE);
                            }
                        });
                alert = builder.create();
                alert.show();
            }
            // ask for permission for the first time
            else{
                ActivityCompat.requestPermissions(MyProfileActivity.this, new String[] {Manifest.permission.CAMERA}, Constants.CAMERA_REQUEST_CODE);
            }
        }
    }



    private void extractedCheckSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(MyProfileActivity.this);
        }else{
            ActivityCompat.requestPermissions(MyProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSIONS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!= null && data.getData() != null){
            selectedImageUri = data.getData();
            try{
                user_img.setImageURI(selectedImageUri);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if((resultCode == Activity.RESULT_OK && requestCode == Constants.CAMERA_REQUEST_CODE && data!= null)){
            cameraImageUri = (Bitmap) (data.getExtras().get("data"));
            try{
                user_img.setImageBitmap(cameraImageUri);
            }catch (Exception e){
                e.printStackTrace();
            }
            selectedImageUri = getImageUri(this, cameraImageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.STORAGE_PERMISSIONS){
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this);
            }else{
                Toast.makeText(this, "You denied permission for storage, change from settings.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == Constants.CAMERA_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // permission granted - happy path
                startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST_CODE);
            }// user has selected do not ask again
            else{
                // have asked once already, permanently denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MyProfileActivity.this, Manifest.permission.CAMERA)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Camera permissions have been permanently denied, go to settings and allow camera permission?")
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // route to settings menu
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", MyProfileActivity.this.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("Don't Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MyProfileActivity.this, "Access to camera is required for this function, else select image from local storage by tapping the image above!", Toast.LENGTH_LONG).show();
                                }
                            }).setCancelable(false);
                    alert = builder.create();
                    alert.show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getFirebaseUserData(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curr_user = snapshot.getValue(UserModel.class);

                // user_img.setImageBitmap();
                user_name.setText(curr_user.getUser_name());

                //user_img.set

                if (!curr_user.getUser_mobile().equals("0")){
                    user_mobile.setText(curr_user.getUser_mobile());
                }

                //To store the current_user image.
                if (!curr_user.getUser_img().isEmpty() && !curr_user.getUser_img().equals(" ")) {
                    Picasso.get().load(curr_user.getUser_img()).into(user_img);
                }

                user_email.setText(curr_user.getUser_email());
                user_pass.setText(curr_user.getUser_passwd());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfileActivity.this, "Failed to fetch user data, try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setFirebaseUserData(){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());

        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("user_id", mAuth.getUid());
        hMap.put("user_email", curr_user.getUser_email());
        hMap.put("user_img", curr_user.getUser_img());

        if (deleted && selectedImageUri == null){
            hMap.put("user_img", " ");
        }

        if (selectedImageUri != null){
            hMap.put("user_img", UserImageURI);
        }

        if (!user_mobile.getText().toString().isEmpty() && !user_mobile.getText().toString().equals("") && !user_mobile.getText().toString().equals(" ")){
            hMap.put("user_mobile", user_mobile.getText().toString());
        }else{
            hMap.put("user_mobile", "0");
        }

        hMap.put("user_name", user_name.getText().toString());
        hMap.put("user_passwd", user_pass.getText().toString());

        hMap.put("user_points", curr_user.getUser_points());
        hMap.put("user_tasks_completed", curr_user.getUser_tasks_completed());


        // check if user made any changes
        if ((hMap.get("user_name").equals(curr_user.getUser_name())) && (hMap.get("user_img").equals(curr_user.getUser_img())) &&
                (hMap.get("user_mobile").equals(curr_user.getUser_mobile())) && (hMap.get("user_passwd").equals(curr_user.getUser_passwd()))){
            Toast.makeText(MyProfileActivity.this, "No Changes made", Toast.LENGTH_SHORT).show();
        }else{
            databaseReference.setValue(hMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(MyProfileActivity.this, "User Data Updated", Toast.LENGTH_SHORT).show();
                        setResult(1002);
                        finish();
                    }
                }
            });
        }
    }


    private void uploadBoardImage() {
        showProgressDialog("Updating Data Please Wait");
        StorageReference sref = FirebaseStorage.getInstance().getReference().child("USER_IMAGES").child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this, selectedImageUri)
        );
        sref.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image upload is successful
                        Log.i(
                                "Firebase Image URL",
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()
                        );
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(
                                new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("Downloadable Image URL", uri.toString());
                                        UserImageURI = uri.toString();
                                        // Call a function to create the board.
                                        setFirebaseUserData();
                                        progressDialog.dismiss();

                                    }
                                }
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Toast.makeText(MyProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                              progressDialog.dismiss();
                                          }
                                      }

                );

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void showProgressDialog(String text){
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView progressTV = (TextView) progressDialog.findViewById(R.id.tv_progress_text);
        progressTV.setText(text);
        progressDialog.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Profile", null);
        return Uri.parse(path);
    }
}