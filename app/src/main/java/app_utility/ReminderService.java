package app_utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

public class ReminderService extends Service implements AsyncInterface {

    String channelId = "app_utility.ReminderService";
    String channelName = "tracking";

    /*startService(new Intent(MyService.ServiceIntent));
    stopService(new Intent((MyService.ServiceIntent));*/

    public static ReminderService refOfService;
    SharedPreferenceClass sharedPreferenceClass;

    NotificationManager notifyMgr;
    NotificationCompat.Builder nBuilder;
    NotificationCompat.InboxStyle inboxStyle;

    AsyncInterface asyncInterface;


    Timer timer = new Timer();
    Handler handler = new Handler();

    //Location previousLocation;

    //Double radius = 60.0;

    //boolean hasNoGpsBug = true;
    //String VOLLEY_STATUS = "NOT_RUNNING";

    //long startTime = 0;
    //long endTime = 0;
    //long totalTime = 0;
    DatabaseHandler db;
    public RemainderDataStorage remainderDataStorage;


    public ReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
        remainderDataStorage = new RemainderDataStorage();
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
        assert notifyMgr != null;
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
        ArrayList<Integer> alSwitchCase = new ArrayList<>();
        alSwitchCase.add(0);
        alSwitchCase.add(1);
        alSwitchCase.add(2);
        ArrayList<DataBaseHelper> alDBData;
        ArrayList<Integer> alVehicleID = new ArrayList<>();
        ArrayList<String> alVehicleBrand = new ArrayList<>();
        ArrayList<String> alModelName = new ArrayList<>();
        ArrayList<ArrayList<String>> alExpiryDate = new ArrayList<>();
        ArrayList<ArrayList<String>> alRemainderDate = new ArrayList<>();
        ArrayList<String> alDisplayPicture = new ArrayList<>();
        ArrayList<String> alLicensePlate = new ArrayList<>();
        ArrayList<String> alModelYear = new ArrayList<>();

        alDBData = new ArrayList<>(db.getAllVehicleID());

        for (int i = 0; i < alDBData.size(); i++) {
            //String[] saAllData = new String[3];
            ArrayList<String> alAllData = new ArrayList<>();

            if (alDBData.get(i).get_insurance_info() != null && !alDBData.get(i).get_insurance_info().equals("")) {
                alAllData.add(alDBData.get(i).get_insurance_info().split(",")[3]);
            } else {
                alAllData.add("");
                //saAllData[0] = "";
            }

            if (alDBData.get(i).get_emission_info() != null && !alDBData.get(i).get_emission_info().equals("")) {
                alAllData.add(alDBData.get(i).get_emission_info().split(",")[3]);
            } else {
                alAllData.add("");
                //saAllData[1] = "";
            }

            if (alDBData.get(i).get_service_info() != null && !alDBData.get(i).get_service_info().equals("")) {
                alAllData.add(alDBData.get(i).get_service_info().split(",")[4]);
            } else {
                alAllData.add("");
                //saAllData[2] = "";
            }
            if (checkForNull(alAllData) < alAllData.size()) {
               /* alVehicleID.add(alDBData.get(i).get_vehicle_id());
                alVehicleBrand.add(alDBData.get(i).get_brand_name());
                alModelName.add(alDBData.get(i).get_model_name());*/
                alExpiryDate.add(alAllData);
            }

            alAllData = new ArrayList<>();
            //resetting flag for next case

            if (alDBData.get(i).get_insurance_info() != null && !alDBData.get(i).get_insurance_info().equals("")) {
                alAllData.add(alDBData.get(i).get_insurance_info().split(",")[4]);
            } else {
                alAllData.add("");
                //saAllData[0] = "";
            }

            if (alDBData.get(i).get_emission_info() != null && !alDBData.get(i).get_emission_info().equals("")) {
                alAllData.add(alDBData.get(i).get_emission_info().split(",")[4]);
            } else {
                alAllData.add("");
                //saAllData[1] = "";
            }

            if (alDBData.get(i).get_service_info() != null && !alDBData.get(i).get_service_info().equals("")) {
                alAllData.add(alDBData.get(i).get_service_info().split(",")[5]);
            } else {
                alAllData.add("");
                //saAllData[2] = "";
            }
            if (checkForNull(alAllData) < alAllData.size()) {
                alVehicleID.add(alDBData.get(i).get_vehicle_id());
                alVehicleBrand.add(alDBData.get(i).get_brand_name());
                alModelName.add(alDBData.get(i).get_model_name());
                alDisplayPicture.add(alDBData.get(i).get_image_base64());
                alLicensePlate.add(alDBData.get(i).get_license_plate());
                alModelYear.add(alDBData.get(i).get_model_year());
                alRemainderDate.add(alAllData);
            }
        }

        // Get Current Date Time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String getCurrentDateTime = sdf.format(c.getTime());

        for (int j = 0; j < alRemainderDate.size(); j++) {
            ArrayList<String> alAllData = alRemainderDate.get(j);

            for (int k = 0; k < alAllData.size(); k++) {
                if (!alAllData.get(k).equals("") && !alAllData.get(k).equals("false")) {
                    String getMyTime = alAllData.get(k);
                    //Log.d("getCurrentDateTime", getCurrentDateTime);
                    //CompareTo method must return negative number if current object is less than other object, positive number if
                    //current object is greater than other object and zero if both objects are equal to each other.
                    //getCurrentDateTime: 05/23/2016 18:49 PM
                    if (getCurrentDateTime.compareTo(getMyTime) == 2) {
                        //if(remainderDataStorage.lhmVehicleID.containsKey(alVehicleID.get(j))){
                        //}
                        ArrayList<String> alTmp;
                        StringBuilder sb = new StringBuilder();
                        alTmp = remainderDataStorage.lhmVehicleID.get(alVehicleID.get(j));
                        if (alTmp != null && alTmp.size() >= 4) {
                            sb.append(String.valueOf(alSwitchCase.get(k))).append(",").append(alExpiryDate.get(j).get(k));
                            alTmp.add(sb.toString());
                        } else {
                            alTmp = new ArrayList<>();
                            alTmp.add(alVehicleID.get(j).toString());
                            alTmp.add(alVehicleBrand.get(j));
                            alTmp.add(alModelName.get(j));
                            if (alDisplayPicture.get(j) != null)
                                alTmp.add(alDisplayPicture.get(j));
                            else
                                alTmp.add("");
                            alTmp.add(alLicensePlate.get(j));
                            alTmp.add(alModelYear.get(j));
                            sb.append(String.valueOf(alSwitchCase.get(k))).append(",").append(alExpiryDate.get(j).get(k));
                            alTmp.add(sb.toString());
                            //alTmp.add(alExpiryDate.get(j).get(k));
                            //alTmp.add(String.valueOf(alSwitchCase.get(k)));
                        }
                        remainderDataStorage.lhmVehicleID.put(alVehicleID.get(j), alTmp);

                        /*String[] saNotify = new String[5];
                        saNotify[0] = String.valueOf(alSwitchCase.get(k));
                        saNotify[1] = alVehicleID.get(j).toString();
                        saNotify[2] = alVehicleBrand.get(j);
                        saNotify[3] = alModelName.get(j);
                        saNotify[4] = alExpiryDate.get(j).get(k);
                        remainderDataStorage.alToNotify.add(saNotify);*/
                    }
                }
            }
        }
        //remainderDataStorage.alToNotify.size() >= 1
        if (remainderDataStorage.lhmVehicleID.size() >= 1) {
            notifyUser();
        }
        //Log.d("Return","getMyTime older than getCurrentDateTime ");
        //stopSelf();
    }

    private int checkForNull(ArrayList<String> alTmp) {
        int count = 1;
        for (int i = 0; i < alTmp.size(); i++) {
            if (alTmp.get(i).equals(""))
                count = count + 1;
        }
        return count;
    }

    @Override
    public void onTaskRemoved(Intent in) {
        Log.e("Service is killed", "");
        super.onTaskRemoved(in);
        if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.ReminderService.ServiceStopped");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ReminderService.this.stopSelf();
        timer.cancel();
        timer.purge();

        //refOfService.stopForeground(true);
        refOfService.stopSelf();
        if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.ReminderService.ServiceStopped");
            sendBroadcast(intent);
        }

        Log.i(TAG, "Service destroyed ...");
    }

    private void fireBaseNotifyListener() {
    }

    private void notifyUser() {
        inboxStyle = new NotificationCompat.InboxStyle();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent acceptIntent = new Intent(ReminderService.this, MyVehicleBroadCastReceiver.class);
        acceptIntent.setAction("android.intent.action.ac.user.accept");
        //acceptIntent.putExtra("SA",alToNotify);
        PendingIntent acceptPI = PendingIntent.getBroadcast(ReminderService.this, 0, acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        /*Intent declineIntent = new Intent(ReminderService.this, MyVehicleBroadCastReceiver.class);
        declineIntent.setAction("android.intent.action.ac.user.decline");
        PendingIntent declinePI = PendingIntent.getBroadcast(ReminderService.this, 0, declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/

        nBuilder = new NotificationCompat.Builder(ReminderService.
                this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("Your vehicle document will expire soon. Tap to view")
                .setContentText("My Vehicle")
                //.setSubText("Your document will expire soon. Tap to view")
                //.addAction(R.drawable.download, "Accept", acceptPI)
                //.addAction(R.drawable.download, "Decline", declinePI)
                .setContentIntent(acceptPI)
                //.setContentIntent(declinePI)
                //.setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);
        // Allows notification to be cancelled when user clicks it
        nBuilder.setAutoCancel(true);
        nBuilder.setStyle(inboxStyle);
        int notificationId = 515;
        notifyMgr.notify(notificationId, nBuilder.build());
    }



    /*public void acceptListener() {
        final String phone = sharedPreferenceClass.getUserID();
    }

    public void declineListener() {
    }*/


    @Override
    public void onAsyncTaskComplete(String sMessage, int nCase, ArrayList<Integer> alID, ArrayList<String> alModelID, ArrayList<Integer> alModelIDNo, ArrayList<String> alModelYear, ArrayList<String> alName, ArrayList<String> alLicensePlate, ArrayList<Bitmap> alDisplayPicture, ArrayList<String> alEncodedDisplayPicture, HashSet<Integer> hsModelIDSingleValues) {

    }

    @Override
    public void onAsyncTaskCompleteGeneral(String sMessage, int nCase, int position, String sData, ArrayList<String[]> alModelArray) {

    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {

    }

    public class RemainderDataStorage {
        //public ArrayList<String[]> alToNotify = new ArrayList<>();

        public LinkedHashMap<Integer, ArrayList<String>> lhmVehicleID = new LinkedHashMap<>();

    }
}
