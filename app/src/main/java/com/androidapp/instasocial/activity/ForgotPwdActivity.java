
/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.utils.Config;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is used to reset password
 */


public class ForgotPwdActivity extends AppCompatActivity {
    public static final String TAG = "ForgotPwd";
    EditText emailId;
    TextView save_btn;
    CustomLoader loader;
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        //initialize data loader
        loader = new CustomLoader(this);
        findViewById();
        findViewById(R.id.txtBackToLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // back button press event
            }
        });
    }

    /**
     * This function is used to map ids to the views of xml layout
     */
    private void findViewById() {

        emailId = (EditText) findViewById(R.id.forgot_username_edittxt);
        emailId.setFilters(new InputFilter[]{RegisterActivity.SPACE_FILTER, RegisterActivity.EMOJI_FILTER});
        save_btn = findViewById(R.id.forgot_save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check for validations
                String email = emailId.getText().toString();

                if (email.isEmpty()) {
                    emailId.setError(getResources().getString(R.string.str_txt_forgot_email_empty));
                } else if (!checkEmail(email)) {
                    emailId.setError(getResources().getString(R.string.str_txt_forgot_valid_email_empty));
                } else {
                    apiCallForgotPwd(email); // Method call to invoke web service
                }
            }
        });


    }

    /**
     * API call to send email id of the user to web server
     *
     * @param email contains the email id, for which reset link will be sent
     */

    private void apiCallForgotPwd(final String email) {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        loader.getCommanLoading();
        StringRequest forgotpass = new StringRequest(Request.Method.POST, Config.ApiUrls.FORGOT_PWD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loader.stopLoading();
                        Log.e(TAG, "apiCallForgotPwd response ===" + response);
                        try {
                            JSONObject responseobj = new JSONObject(response);
                            String stat = responseobj.getString("status");
                            String msg = responseobj.getString("status_message");
                            if (stat.equalsIgnoreCase("true")) {
                                emailId.setText("");
                            } else {

                            }

                            App.showToast(msg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.stopLoading();
                        Log.e("VOLLEY", "forgotpass ERROR");
                        Toast.makeText(ForgotPwdActivity.this, getResources().getString(R.string.str_txt_try_again_msg), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                Log.e(TAG, "apiCallForgotPwd params " + params.toString());
                return params;
            }
        };

        forgotpass.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "forgotpass TimeOut");
                        loader.stopLoading();
                    }
                }).start();
            }
        });
        App.instance().addToRequestQueue(forgotpass);
    }

    /**
     * @param email
     * @return true if email is valid and false if invalid
     */
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }


    /**
     * Actions that take place on back click
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPwdActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
