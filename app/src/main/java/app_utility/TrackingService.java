package app_utility;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.autochip.myvehicle.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

public class TrackingService extends Service implements AsyncInterface {

    String channelId = "app_utility.TrackingService";
    String channelName = "tracking";

    /*startService(new Intent(MyService.ServiceIntent));
    stopService(new Intent((MyService.ServiceIntent));*/

    public static TrackingService refOfService;
    SharedPreferenceClass sharedPreferenceClass;

    NotificationManager notifyMgr;
    NotificationCompat.Builder nBuilder;
    NotificationCompat.InboxStyle inboxStyle;

    AsyncInterface asyncInterface;

    Timer timer = new Timer();
    Handler handler = new Handler();

    Location previousLocation;

    //Double radius = 60.0;

    //boolean hasNoGpsBug = true;
    String VOLLEY_STATUS = "NOT_RUNNING";

    long startTime = 0;
    long endTime = 0;
    long totalTime = 0;


    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        this will make sure service will run on background in oreo and above
        service wont run without a notification from oreo version.
        After the system has created the service, the app has five seconds to call the service's startForeground() method
        to show the new service's user-visible notification. If the app does not call startForeground() within the time limit,
        the system stops the service and declares the app to be ANR.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground();
        }

        refOfService = this;
        asyncInterface = this;
        sharedPreferenceClass = new SharedPreferenceClass(getApplicationContext());
        fireBaseNotifyListener();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "I am still running", Toast.LENGTH_LONG).show();
                        Log.e("Service status: ", "RUNNING");
                        //getLocation();
                    }
                });
            }
        };
        //Starts after 20 sec and will repeat on every 20 sec of time interval.
        timer.schedule(doAsynchronousTask, 0, 10000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), createNotificationChannel() );
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.createNotificationChannel(chan);
        return channelId;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent in) {
        Log.e("Service is killed", "");
        super.onTaskRemoved(in);
        if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.TrackingService.ServiceStopped");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TrackingService.this.stopSelf();
        timer.cancel();
        timer.purge();

        //refOfService.stopForeground(true);
        refOfService.stopSelf();
        if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.TrackingService.ServiceStopped");
            sendBroadcast(intent);
        }

        Log.i(TAG, "Service destroyed ...");
    }

    private void fireBaseNotifyListener() {
    }

    /*private void notifyUser() {
        inboxStyle = new NotificationCompat.InboxStyle();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent acceptIntent = new Intent(TrackingService.this, TrackingBroadCastReceiver.class);
        acceptIntent.setAction("android.intent.action.ac.user.accept");
        PendingIntent acceptPI = PendingIntent.getBroadcast(TrackingService.this, 0, acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent declineIntent = new Intent(TrackingService.this, TrackingBroadCastReceiver.class);
        declineIntent.setAction("android.intent.action.ac.user.decline");
        PendingIntent declinePI = PendingIntent.getBroadcast(TrackingService.this, 0, declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        nBuilder = new NotificationCompat.Builder(TrackingService.
                this, channelId)
                //.setSmallIcon(R.drawable.ic_launcher_background)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Admin has asked permission to track you. Would you like to accept?")
                .setSubText("Admin has asked permission to track you. Would you like to accept?")
                //.addAction(R.drawable.download, "Accept", acceptPI)
                //.addAction(R.drawable.download, "Decline", declinePI)
                .setContentIntent(acceptPI)
                .setContentIntent(declinePI)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);
        // Allows notification to be cancelled when user clicks it
        nBuilder.setAutoCancel(true);
        nBuilder.setStyle(inboxStyle);
        int notificationId = 515;
        notifyMgr.notify(notificationId, nBuilder.build());
    }*/


    public void acceptListener() {
        final String phone = sharedPreferenceClass.getUserID();
    }

    public void declineListener() {
    }


    @Override
    public void onAsyncTaskComplete(String sMessage, int nCase, ArrayList<Integer> alID, ArrayList<String> alModelID, ArrayList<Integer> alModelIDNo, ArrayList<String> alModelYear, ArrayList<String> alName, ArrayList<String> alLicensePlate, ArrayList<Bitmap> alDisplayPicture, ArrayList<String> alEncodedDisplayPicture, HashSet<Integer> hsModelIDSingleValues) {

    }

    @Override
    public void onAsyncTaskCompleteGeneral(String sMessage, int nCase, int position, String sData, ArrayList<String[]> alModelArray) {

    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {

    }
}
