//Referred https://developer.android.com/develop/ui/views/notifications/build-notification#java
//Pair programming: Divit, Parshva, Shivani
package edu.northeastern.numad22fa_team51_project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notifManage=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifIntent=new Intent(context,DashboardActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent=PendingIntent.getActivity(context,1,notifIntent,PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notifCompat=new NotificationCompat.Builder(context,"task_notification").setSmallIcon(R.drawable.ic_done_yellow).setContentIntent(pIntent).setContentTitle("Plan your day now!").setContentText("Hey, check out your to-do list.").setAutoCancel(true);
        notifManage.notify(1,notifCompat.build());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notifCompat.build());
    }
}
