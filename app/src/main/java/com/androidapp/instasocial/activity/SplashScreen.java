/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;



/**
 * This class is used to display a welcome page, that staysput for 3 secs
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //Logic to stay put the splash page for 3 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent navigate;
                /**
                 * check whether user is logged in
                 */

                //User NOT logged in
                if (App.preference().getUserId().isEmpty()) {
                    navigate = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(navigate);
                    finish();

                } else
                //user logged in
                {
                    navigate = new Intent(SplashScreen.this, HomeActivity.class);
                    navigate.putExtra("index", 0);
                    navigate.putExtra("member_id", App.preference().getUserIdInt());
                    navigate.putExtra("username", App.preference().getUserName());
                    navigate.putExtra("password", App.preference().getAccessToken());
                    navigate.putExtra("remember_password", true);
                    navigate.putExtra("protocol", 5);
                    startActivity(navigate);
                    finish();
                }

            }
        }, 3000);

    }


}


