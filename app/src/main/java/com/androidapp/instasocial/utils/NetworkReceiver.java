/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.androidapp.instasocial.App;


public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG=NetworkReceiver.class.getSimpleName();
    private Context context;
    private NetworkListener networkListener;
    public NetworkReceiver(Context context){
        this.context=context;
    }

    public NetworkReceiver(){
    }

    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    public static interface NetworkListener{
        public void onNetworkChange(boolean isConnected, NetworkInfo activeNetwork);
    }

    public void startReceiver(){
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver((BroadcastReceiver) this, intentFilter);
    }

    public void stopReceiver(){
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
            if (networkListener != null) {
                networkListener.onNetworkChange(activeNetwork != null, activeNetwork);
            }
        }catch (SecurityException e){
            Log.e(TAG,"ACCESS_NETWORK_STATE permission is missing");
        }

//        if (activeNetwork != null) { // connected to the internet
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                // connected to wifi
//                AppLog.d(TAG,"Wifi connected");
//            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                // connected to the mobile provider's data plan
//                AppLog.d(TAG,"mobile data connected");
//            }
//        } else {
//            // not connected to the internet
//            AppLog.d(TAG,"no network connected");
//        }


    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) App.instance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork=connectivity.getActiveNetworkInfo();
            return activeNetwork!=null;
            }
        return false;
    }

}
