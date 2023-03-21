/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.pushnotification;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.activity.LoginActivity;
import com.androidapp.instasocial.utils.Config;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.List;


public class InstaplusNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.
    Context context;
    public InstaplusNotificationOpenedHandler(Context context){
        this.context=context;
    }
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        boolean isAppVisible=isAppIsInBackground(context);
        try {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);


            int sizeStack =  am.getRunningTasks(2).size();
            System.out.println("Size of the Stack " + sizeStack);
            if(sizeStack>1){
                isAppVisible=false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Is App Visible 0" + isAppVisible);
        System.out.println("JSON OBject  Data " + result.notification.payload.toJSONObject());

        JSONObject data = result.notification.payload.additionalData;



        if (!App.preference().getInstance(context).getUserId().equals("")) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.putExtra(Config.ARG_PUSH, data.toString());
            intent.putExtra("isAppVisible",isAppVisible);
            context.startActivity(intent);
        } else {
            Intent LoginIntent = new Intent(context, LoginActivity.class);
            LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoginIntent.setAction(Long.toString(System.currentTimeMillis()));
            LoginIntent.putExtra(Config.ARG_PUSH, data.toString());
            context.startActivity(LoginIntent);
        }
    }

    /**
     * Method that takes in a context and checks whether app is in background
     * @param context
     * @return
     */
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
