package app_utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.autochip.myvehicle.MainActivity;
import com.autochip.myvehicle.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

public class RemainderService extends Service implements AsyncInterface {

    String channelId = "app_utility.RemainderService";
    String channelName = "tracking";

    /*startService(new Intent(MyService.ServiceIntent));
    stopService(new Intent((MyService.ServiceIntent));*/

    public static RemainderService refOfService;
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
    DatabaseHandler db;


    public RemainderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
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

        checkForExpiryDates();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), createNotificationChannel());
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
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


    private void checkForExpiryDates() {
        int nInsurance = 0;
        int nEmission = 1;
        int nService = 2;
        ArrayList<DataBaseHelper> alDBData;
        ArrayList<Integer> alVehicleID = new ArrayList<>();
        ArrayList<String> alVehicleBrand = new ArrayList<>();
        ArrayList<String> alModelName = new ArrayList<>();
        ArrayList<String[]> alExpiryDate = new ArrayList<>();
        ArrayList<String[]> alRemainderDate = new ArrayList<>();
        ArrayList<String[]> alToNotify = new ArrayList<>();
        
        alDBData = new ArrayList<>(db.getAllVehicleID());

        for (int i = 0; i < alDBData.size(); i++) {
            alVehicleID.add(alDBData.get(i).get_vehicle_id());
            alVehicleBrand.add(alDBData.get(i).get_brand_name());
            alModelName.add(alDBData.get(i).get_model_name());

            String[] saAllData = new String[3];
            saAllData[0] = alDBData.get(i).get_insurance_info().split(",")[3];
            saAllData[1] = alDBData.get(i).get_emission_info().split(",")[3];
            saAllData[2] = alDBData.get(i).get_service_info().split(",")[4];
            alExpiryDate.add(saAllData);

            saAllData[0] = alDBData.get(i).get_insurance_info().split(",")[4];
            saAllData[1] = alDBData.get(i).get_emission_info().split(",")[4];
            saAllData[2] = alDBData.get(i).get_service_info().split(",")[5];
            alRemainderDate.add(saAllData);
            //.get
        }

        // Get Current Date Time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String getCurrentDateTime = sdf.format(c.getTime());

        for(int j=0; j<alRemainderDate.size(); j++){
            String[] saAllData = alRemainderDate.get(j);
            String getMyTime = saAllData[0];
            Log.d("getCurrentDateTime", getCurrentDateTime);

            if (getCurrentDateTime.compareTo(getMyTime) == 0) {
                String[] saNotify = new String[5];
                saNotify[0] = String.valueOf(nInsurance);
                saNotify[1] = alVehicleID.get(0).toString();
                saNotify[2] = alVehicleBrand.get(0);
                saNotify[3] = alModelName.get(0);
                saNotify[4] = alExpiryDate.get(0)[0];

                alToNotify.add(saNotify);
            }
        }



        //getCurrentDateTime: 05/23/2016 18:49 PM
        //CompareTo method must return negative number if current object is less than other object, positive number if 
        //current object is greater than other object and zero if both objects are equal to each other.


        /*else
        {
            Log.d("Return","getMyTime older than getCurrentDateTime ");
        }*/
    }

    @Override
    public void onTaskRemoved(Intent in) {
        Log.e("Service is killed", "");
        super.onTaskRemoved(in);
        if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.RemainderService.ServiceStopped");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //RemainderService.this.stopSelf();
        timer.cancel();
        timer.purge();

        //refOfService.stopForeground(true);
        refOfService.stopSelf();
        if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.RemainderService.ServiceStopped");
            sendBroadcast(intent);
        }

        Log.i(TAG, "Service destroyed ...");
    }

    private void fireBaseNotifyListener() {
    }

    /*private void notifyUser() {
        inboxStyle = new NotificationCompat.InboxStyle();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent acceptIntent = new Intent(RemainderService.this, TrackingBroadCastReceiver.class);
        acceptIntent.setAction("android.intent.action.ac.user.accept");
        PendingIntent acceptPI = PendingIntent.getBroadcast(RemainderService.this, 0, acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent declineIntent = new Intent(RemainderService.this, TrackingBroadCastReceiver.class);
        declineIntent.setAction("android.intent.action.ac.user.decline");
        PendingIntent declinePI = PendingIntent.getBroadcast(RemainderService.this, 0, declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        nBuilder = new NotificationCompat.Builder(RemainderService.
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
