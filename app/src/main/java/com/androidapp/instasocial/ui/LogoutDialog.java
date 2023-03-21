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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;


public class LogoutDialog extends Dialog implements
        View.OnClickListener {

    /**
     * Member variables declarations/initializations
     */
    public Activity activity;
    public Dialog dialog;
    public Button yes, no;


    public LogoutDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_logout);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                App.logout(activity);// Yes Onclick - so logout of the app
                break;
            case R.id.btn_no:
                dismiss();// No Onclick - close the popup
                break;
            default:
                break;
        }
    }
}
