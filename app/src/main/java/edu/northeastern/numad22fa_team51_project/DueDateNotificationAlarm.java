package edu.northeastern.numad22fa_team51_project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;




public class DueDateNotificationAlarm  extends BroadcastReceiver{

    private Intent intent;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        checkDueTasks();
        this.context=context;
        this.intent=intent;


    }

    public void checkDueTasks(){
        Log.e("in checkDueTasks","checkduetasks");
        FirebaseAuth auth= FirebaseAuth.getInstance();
        String userId=auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TASKS);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> dueTaskList=new ArrayList<>();

                Log.e("in checkDueTasks", "ondatachange" + snapshot.toString());
                Log.e("Incorrect Already present before entry",dueTaskList.toString());

                for (DataSnapshot snapshotOfTask : snapshot.getChildren()) {
                    for (DataSnapshot datasnapShot : snapshotOfTask.getChildren()) {
                        Log.d("DueDateNotification DB:", datasnapShot.toString());

                        String documentId = datasnapShot.getKey();
                        Log.d("docID", documentId);

                        DataSnapshot memberListSnapshot = datasnapShot.child("memberList");
                        String memberListStr = memberListSnapshot.getValue().toString();
                        String[] memberList = memberListStr.split(",");
                        ArrayList<String> memberArrayList = new ArrayList<String>(
                                Arrays.asList(memberList));

                        DataSnapshot createdBySnapshot = datasnapShot.child("createdBy");
                        String createdBy = createdBySnapshot.getValue().toString();

                        if(!memberArrayList.contains(userId)){
                            continue;
                        }

                        DataSnapshot dueDateSnapShot = datasnapShot.child("DueDate");
                        String dueDateStr = dueDateSnapShot.getValue().toString();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
                        Date dueDate=null;
                        try {
                            if(dueDateStr!=null && dueDateStr.length()!=0)
                                dueDate=simpleDateFormat.parse(dueDateStr);
                        } catch (ParseException e) {
                            Log.w("DueDateNotification", "Error while converting date format", e);
                        }

                        boolean isToday=false;
                        if(dueDateStr!=null && dueDateStr.length()!=0) {
                            ZoneId zoneId = ZoneId.systemDefault();
                            LocalDate localDate = LocalDate.now();
                            Date date = Date.from(localDate.atStartOfDay(zoneId).toInstant());
                            if (dueDate.compareTo(date) == 0) {
                                isToday = true;
                            }
                            Log.e("Dates:", date.toString() + " " + dueDate.toString() + " " + isToday);
                        }
                        else{
                            continue;
                        }

                        DataSnapshot isCompleteSnapShot = datasnapShot.child("isComplete");
                        Boolean isComplete = isCompleteSnapShot.getValue().toString().equals("true");
                        if(isToday && !isComplete){
                            Log.e("Incorrect Entry",String.valueOf(isComplete)+" "+String.valueOf(isToday)+" "+documentId);
                            dueTaskList.add(documentId);
                        }


                            Log.e("Res: "+userId,memberListStr+" "+memberArrayList.toString()+" "+createdBy+" "+dueDateStr+" "+dueDate+" "+isComplete+" "+isToday);

                    }
                }
                Log.e("Incorrect Already present",dueTaskList.toString());
                tempFunction(dueTaskList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DueDateNotification", "Error while checking due tasks", databaseError.toException());
            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
    }

    private void tempFunction(List<String> dueTaskList) {
        if(dueTaskList.size()>0){
            Log.e("isDueToday:",dueTaskList.toString()+"true");
            sendNotification();
        }
        else{
            Log.e("isDueToday:","false");

        }
    }

    private void sendNotification(){
        NotificationManager notifManage=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifIntent=new Intent(context,DashboardActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent=PendingIntent.getActivity(context,2,notifIntent,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notifCompat=new NotificationCompat.Builder(context,"task_notification").setSmallIcon(R.drawable.ic_done_yellow).setContentIntent(pIntent).setContentTitle("Task Due Today!").setContentText("Complete your task before it's too late!").setAutoCancel(true);
        notifManage.notify(2,notifCompat.build());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2, notifCompat.build());
    }


}
