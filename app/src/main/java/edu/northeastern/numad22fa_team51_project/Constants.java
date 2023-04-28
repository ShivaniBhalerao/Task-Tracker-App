package edu.northeastern.numad22fa_team51_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

public class Constants {

    // Including Firebase constants.
    static final String BOARDS = "Boards";
    static final String USERS = "Users";
    static final String NAME = "name";
    static final String TASKS = "Tasks";
    static final String DOCUMENT_ID = "documentId";
    static final String ASSIGNED_TO = "assignedTo";
    static final int STORAGE_PERMISSIONS = 1;
    static final int PICK_IMAGE_REQUEST_CODE = 2;
    static final String BOARD_DETAILS = "board_details";
    static final String BOARD_MEMBERS_LIST = "board_members_list";
    public static final String TASK_DETAILS = "task_details";
    public static final String SELECT = "Select";
    public static final String UNSELECT = "Unselect";
    public static final String BOARD_OBJ = "board_obj";
    public static final String USERS_OBJ_ARR = "users_obj_arr";
    public static final String FALSE = "false";
    public static final String TRUE = "true";
    public static final int MAX_POINTS_TASK = 1000;
    public static final int MIN_POINTS_TASK = 0;
    public static final int CAMERA_REQUEST_CODE = 20010;


    // Opens an activity to choose an image
    public static void showImageChooser(@NonNull Activity activity){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    // Fetch the image extension in our case
    public static String getFileExtension(@NonNull Activity activity, Uri uri){
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(
                activity.getContentResolver().getType(uri));
    }

}
