/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.androidapp.instasocial.activity.LoginActivity;
import com.androidapp.instasocial.pushnotification.InstaplusNotificationOpenedHandler;
import com.androidapp.instasocial.pushnotification.InstaplusNotificationReceivedHandler;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.androidapp.instasocial.utils.Preferences;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;

import java.io.File;
import java.io.UnsupportedEncodingException;


/**
 * Base android Application class
 */
public class App extends MultiDexApplication {
    public static final String TAG = App.class.getSimpleName();
    RequestQueue requestQueue = null;
    public static App mInstance;
    public static final String Foldername = "InstaPlus";
    public static final String Filename = "insta_image";
    private static Preferences mPreferences;
    private static RetryPolicy retryPolicy = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        MultiDex.install(this);
        super.onCreate();
        mInstance = this;
        initOneSignal();
        /**
         * Replace your AdMob app id here
         */
        MobileAds.initialize(instance(), getStringRes(R.string.admob_app_id));
    }

    public static synchronized App instance() {
        return mInstance;
    }

    /**
     * Webservice calls adding to queue management
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    /**
     * Singleton method to get an instance of the preference
     *
     * @return
     */

    public static synchronized Preferences preference() {
        if (mPreferences == null) mPreferences = Preferences.getInstance(mInstance);
        return mPreferences;
    }


    /**
     * Common method to display toast
     *
     * @param msg
     * @param duration
     */
    public static void showToast(String msg, int duration) {
        Toast.makeText(instance(), msg, duration).show();
    }

    /**
     * Display toast with msg
     *
     * @param msg
     */
    public static void showToast(String msg) {
        if (msg != null)
            showToast(msg, Toast.LENGTH_SHORT);
    }

    /**
     * Display toast using string id
     *
     * @param stringId
     */
    public static void showToast(int stringId) {
        showToast(getStringRes(stringId), Toast.LENGTH_SHORT);
    }

    /**
     * to get string from string.xml file
     *
     * @param resId
     * @return
     */
    public static String getStringRes(int resId) {
        try {
            return instance().getResources().getString(resId);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * to get color from the resource id
     *
     * @param resId
     * @return
     */
    public static int getColorRes(int resId) {
        try {
            return (ContextCompat.getColor(instance(), resId));
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    /**
     * helper method to hide the keyboard
     *
     * @param view
     */
    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) App.instance().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * helper method for unicode conversion
     *
     * @param unicode
     * @returns a string
     */

    public static String UniCodeCoversion(String unicode) {

        String unicodeString = new String(unicode);

        byte[] utf8Bytes = null;
        String convertedString = null;
        try {
            System.out.println(unicodeString);
            utf8Bytes = unicodeString.getBytes("UTF8");
            convertedString = new String(utf8Bytes, "UTF8");
            System.out.println(convertedString); //same as the original string
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return convertedString;
    }


    /**
     * check whether network is available
     *
     * @return
     */
    public static boolean isOnline() {
        return NetworkReceiver.isOnline(instance());
    }

    /**
     * Logout logics
     *
     * @param activity
     */
    public static void logout(Activity activity) {
        try {
            LoginManager.getInstance().logOut();

            App.preference().purge();
            Intent in = new Intent(instance(), LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.finish();
            instance().startActivity(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logic to handle emojis in editable view
     */

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        }
    };

    /**
     * Helper methods
     */
    public static int getVisiblePercent(View view) {
        if (view == null) return 0;

        int percents = 100;
        try {
            final Rect mCurrentViewRect = new Rect();
            view.getLocalVisibleRect(mCurrentViewRect);
            int height = view.getHeight();
            int width = view.getWidth();
            boolean isViewPartiallyHiddenBottom = mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
            boolean isViewPartiallyHiddenTop = mCurrentViewRect.top > 0;
            if (isViewPartiallyHiddenTop) {
                // view is partially hidden behind the top edge
                percents = (height - mCurrentViewRect.top) * 100 / height;
            } else if (isViewPartiallyHiddenBottom) {
                percents = mCurrentViewRect.bottom * 100 / height;
            }
        } catch (Exception e) {
            return 0;
        }
        return percents;
    }

    /**
     * Helper method to get the width of the device
     *
     * @return
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * This function returns true if the device is a tablet
     *
     * @return
     */
    public static boolean isTablet() {
        double diagonalInches = getDiagonalInches();
        Log.e("App", "diagonalInches: " + diagonalInches);
        return diagonalInches >= 6.5;
    }

    /**
     * This function is used to get the dimension diagnally
     * @return
     */
    public static double getDiagonalInches() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        return Math.sqrt(xInches * xInches + yInches * yInches);
    }

    /**
     * Takes file as input and returns the Uri of the file from content provider
     *
     * @param file
     * @return
     */
    public static Uri getUriForFile(File file) {
        String provider = "com.androidapp.instasocial.fileprovider";
        Context context = instance().getApplicationContext();
        return FileProvider.getUriForFile(context, provider, file);
    }


    /**
     * one signal initialization for push notifications
     */
    private void initOneSignal() {
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new InstaplusNotificationOpenedHandler(getApplicationContext()))
                .setNotificationReceivedHandler(new InstaplusNotificationReceivedHandler(getApplicationContext()))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                // Log.d("debug", "User:" + userId);
                if (registrationId != null) {
                    System.out.println("Registration  id " + userId);
                    Preferences.getInstance(getApplicationContext()).setPlayerId(userId);
                }
                //  Log.d("debug", "registrationId:" + registrationId);

            }
        });
    }

}
