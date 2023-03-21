/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.androidapp.instasocial.R;

/**
 * Common loader class implementation
 */

public class CustomLoader {

    /**
     * Member variables declarations/initializations
     */
    public static Dialog loading_popup;
    Activity activity;


    public CustomLoader(Activity activity) {
        this.activity = activity;
    }


    /**
     * start the loader, by setting it in a dialog
     */

    public void getCommanLoading() {
        loading_popup = new Dialog(activity, R.style.NewDialog);
        loading_popup.setContentView(R.layout.common_loader);


        Window window = loading_popup
                .getWindow();
        window.setLayout(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = loading_popup
                .getWindow()
                .getAttributes();
        loading_popup.setCancelable(true);
        loading_popup.show();
    }

    /**
     * stop the loader
     */
    public void stopLoading() {
        loading_popup.dismiss();
    }
}
