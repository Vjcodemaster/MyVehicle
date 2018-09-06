package app_utility;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.autochip.myvehicle.MainActivity;

import java.util.List;

public class MyVehicleBroadCastReceiver extends BroadcastReceiver {
    boolean isActivityFound = false;
    Context context;
    Intent service;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Log.v(intent.getAction(),intent.getAction());
        String in = intent.getAction();
        assert in != null;
        switch(in)
        {
            case "android.intent.action.BOOT_COMPLETED":
                service = new Intent(context, ReminderService.class);
                context.startService(service);
                break;
            /*case "app_utility.TrackingService.ServiceStopped":
                service = new Intent(context, TrackingService.class);
                context.startService(service);
                break;*/
            case "android.intent.action.ac.user.accept":
                SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                sharedPreferenceClass.setIsFromNotification(true);
                /*Bundle extras = intent.getExtras();
                String[] saData = extras.getStringArray("SA");*/
                Intent inMain = new Intent(context, MainActivity.class);
                if(isActivityRunning()){
                    inMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    inMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                //inMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(inMain);
                //TrackingService.refOfService.acceptListener();
                break;
            case "android.intent.action.ac.user.decline":
                //TrackingService.refOfService.declineListener();
                break;
        }

    }

    public boolean isActivityRunning() {

        ActivityManager activityManager = (ActivityManager)context.getSystemService (Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> activitys = activityManager.getRunningTasks(Integer.MAX_VALUE);
        isActivityFound = false;
        for (int i = 0; i < activitys.size(); i++) {
            if (activitys.get(i).topActivity.toString().equalsIgnoreCase("ComponentInfo{com.autochip.myvehicle/com.autochip.myvehicle.MainActivity}")) {
                isActivityFound = true;
            }
        }
        return isActivityFound;
    }
}
