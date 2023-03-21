/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.pushnotification;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;


public class MyNotificationExtenderService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        NotificationExtenderService.OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Red on Android 5.0+ devices.
                Bitmap icon = BitmapFactory.decodeResource(App.mInstance.getResources(),
                        R.drawable.ic_stat_onesignal_default);

                builder.setLargeIcon(icon);
                builder.setPriority(2);
                builder.setSmallIcon(R.drawable.ic_stat_onesignal_default);


                return builder.setColor(new BigInteger("FF703A87", 16).intValue());
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

        boolean isLoggedIn=!App.preference().getUserId().isEmpty();
        try {
            if (isLoggedIn){
                App.preference().setNotificationCountStr(receivedResult.payload.additionalData.getString("batch_count"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isLoggedIn;
    }
}