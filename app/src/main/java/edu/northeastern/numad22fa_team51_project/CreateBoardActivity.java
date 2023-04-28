package edu.northeastern.numad22fa_team51_project;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateBoardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Uri selectedImageUri = null;
    private String boardImageURL = "";
    private CircleImageView iv_board_image;
    private String user_id;
    private Intent intent;
    private Button user_create_bttn;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private EditText groupName;
    private Dialog progressDialog;
    private long lastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        groupName = findViewById(R.id.et_board_name);
        user_create_bttn = findViewById(R.id.btn_create);
        setupActionBar();
        iv_board_image = findViewById(R.id.iv_board_image);
        intent = getIntent();
        if (intent.hasExtra(Constants.NAME)){
            user_id = intent.getStringExtra(Constants.NAME);
            Log.d("Username-CreateDashBoardActivity", user_id);
        }

        iv_board_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractedCheckSeldPermission();
            }
        });


        user_create_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // prevent redundant network calls being executed multiple times if button is pressed more than once within 1 sec
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();

                if(groupName.getText() == null || groupName.getText().toString().equals("")){
                    Log.d("groupName", String.valueOf(groupName));
                    Toast.makeText(CreateBoardActivity.this, "Please fill in the group name", Toast.LENGTH_SHORT).show();
                }else if (selectedImageUri != null){
                    uploadBoardImage();
                }else{
                    showProgressDialog("Please Wait");
                    createBoard();
                }
            }
        });
    }

    private void extractedCheckSeldPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(CreateBoardActivity.this);
        }else{
            ActivityCompat.requestPermissions(CreateBoardActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.STORAGE_PERMISSIONS);
        }
    }

    private void setupActionBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_create_board_activity);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null){
        selectedImageUri = data.getData();

        try{
            Glide.with(this)
                    .load(Uri.parse((selectedImageUri.toString())))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_board_image);
        }catch (Exception e){
            e.printStackTrace();
        }

        }
    }

    private void boardCreatedSuccessfully(){
        progressDialog.dismiss();
        finish();
    }

    private void createBoard(){
        ArrayList<String> assignedUserArrayList = new ArrayList<>();
        assignedUserArrayList.add(auth.getCurrentUser().getUid());

        String assignedUserList = covertArrayListToString(assignedUserArrayList);
        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("board_name", groupName.getText().toString());
        hMap.put("group_image", boardImageURL);
        hMap.put("group_createdBy", user_id);
        hMap.put("group_assignedTo", assignedUserList);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        reference.child(Constants.BOARDS).push().setValue(hMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Board-Creation", "Board Created Successfully");
                        Toast.makeText(CreateBoardActivity.this, "Board Created Successfully", Toast.LENGTH_SHORT).show();
                        boardCreatedSuccessfully();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Board-Creation-Failed", "Board Failed to Create", e);
                    }
                });
    }

    @NonNull
    private String covertArrayListToString(ArrayList<String> list) {
        String StringList = "";
        for (int i = 0; i < list.size(); i++) {
            StringList += list.get(i) + ",";
        }
        return StringList;
    }

    private void uploadBoardImage() {
        showProgressDialog("Please Wait");
        StorageReference sref = FirebaseStorage.getInstance().getReference().child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "."
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
                                        boardImageURL = uri.toString();
                                        // Call a function to create the board.
                                        createBoard();
                                    }
                                }
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateBoardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    }
                );

    }

    private void showProgressDialog(String text){
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress);
        TextView progressTV = (TextView) progressDialog.findViewById(R.id.tv_progress_text);
        progressTV.setText(text);
        progressDialog.show();
    }

}