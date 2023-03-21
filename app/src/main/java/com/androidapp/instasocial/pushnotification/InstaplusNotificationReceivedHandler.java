/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.pushnotification;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.androidapp.instasocial.App;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class InstaplusNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    Context context;

    public InstaplusNotificationReceivedHandler(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void notificationReceived(OSNotification osNotification) {

        JSONObject data = osNotification.payload.additionalData;
        try{
            App.preference().setNotificationCountStr(data.getString("batch_count"));
        }catch (Exception e){
            e.printStackTrace();
        }
        }
}