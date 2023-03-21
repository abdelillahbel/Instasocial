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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Handle email login and facebook login
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Member variables declarations/initializations
     */
    private static final String TAG = "LoginActivity";
    protected LoginButton m_buttonLoginFB;
    protected View m_viewLoginButtonWrapperFB;
    protected CompatTextView m_buttonSubmit;
    protected EditText m_editUsername;
    protected EditText m_editPassword;
    private CallbackManager callbackManager;
    protected CallbackManager m_fbCallbackManager;
    CustomLoader loader;
    String socialLoginValues = "";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initFacebookControls();
        setContentView(R.layout.activity_login);
        initControls();
    }
    
    /**FaceBookControls init is used for facebook login**/

    private void initFacebookControls() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        m_fbCallbackManager = CallbackManager.Factory.create();
        callbackManager = CallbackManager.Factory.create();
        LoginManager fbLoginManager = LoginManager.getInstance();
        fbLoginManager.logOut();
    }
   
    /**UI controls initialization**/
    private void initControls() {
        loader = new CustomLoader(this);

        //Go to Forgot password
        findViewById(R.id.txtForgotPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPwdActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //Go to signup
        findViewById(R.id.layoutSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);
                finish();
            }
        });
        m_editUsername = (EditText) findViewById(R.id.username);
        m_editPassword = (EditText) findViewById(R.id.password);
        m_buttonSubmit = findViewById(R.id.submit);
        m_buttonSubmit.setOnClickListener(this);

        // Facebook login button setup
        m_buttonLoginFB = new LoginButton(LoginActivity.this);
        m_viewLoginButtonWrapperFB = (View) findViewById(R.id.fb_login_button_wrapper);
        m_viewLoginButtonWrapperFB.setOnClickListener(this);
        m_buttonLoginFB.setReadPermissions("email");
        m_buttonLoginFB.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        m_buttonLoginFB.registerCallback(callbackManager, callback);
        m_buttonLoginFB.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0);
        m_buttonLoginFB.setText(Config.getStringRes(this,R.string.str_sign_in_facebook));
        m_buttonLoginFB.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        graphRequestApi();
    }



    /**
     * Facebook login code logics  -----  STARTS HERE  -----
     */

    /**
     * Facebook graph reauest get user details
     */
     public void  graphRequestApi(){
         m_buttonLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
             @Override
             public void onSuccess(LoginResult loginResult) {
                 GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                         new GraphRequest.GraphJSONObjectCallback() {
                             @Override
                             public void onCompleted(JSONObject object, GraphResponse response) {
                                 Log.e("LoginAct", response.toString());

                                 try {
                                     String soc_id = object.getString("id");
                                     String soc_emailid = object.has("email") ? object.getString("email") : "null";
                                     String soc_username = object.getString("name");
                                     String soc_firstname = object.getString("first_name");
                                     String soc_lastname = object.getString("last_name");
                                     String locale = object.has("locale") ? object.getString("locale") : "null";
                                     String user_image_profile = "http://graph.facebook.com/" + soc_id + "/picture?type=large";

                                     socialLoginValues = soc_id + "," + soc_emailid + "," + soc_username + "," + soc_firstname + "," + soc_lastname + "," + user_image_profile;
                                     apiCallSocialLoginCheck(soc_id); //Method call that makes a check if user is a new user
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                             }
                         });

                 Bundle parameters = new Bundle();
                 parameters.putString("fields", "id,email,name,first_name,last_name,birthday,locale");
                 request.setParameters(parameters);
                 request.executeAsync();
             }

             @Override
             public void onCancel() {
                 Log.e("LoginAct", "cancel");
             }

             @Override
             public void onError(FacebookException exception) {
                 Log.e("LoginAct ERROR ---->>> ", exception.toString());
             }
         });
     }

    /**
     * Facebook Callback for Login result
     */
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {
            System.out.println("error" + e);
        }
    };

    /** facebook callback after logging in**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**Check whether existing facebook user is there or not **/
    public void apiCallSocialLoginCheck(final String id) {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        loader.getCommanLoading();
        StringRequest login = new StringRequest(Request.Method.POST, Config.ApiUrls.FACEBOOK_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loader.stopLoading();
                        Log.e("Response ", response.toString());
                        try {
                            JSONObject obj=new JSONObject(response);

                            if (obj.getString("status").equals("false")) {
                                String msg=obj.has("status_message")?obj.getString("status_message"):"";
                                if (!msg.contains("suspend")) {
                                    Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    newIntent.putExtra("socialLoginValue", socialLoginValues);
                                    startActivity(newIntent);
                                }else {
                                    LoginManager.getInstance().logOut();
                                }
                                App.showToast(msg);
                            } else {

                                try {
                                    storeLoginDetails(new JSONObject(response));
                                    String message = new JSONObject(response).getString("status_message");
                                    Config.showToast(LoginActivity.this, message);
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 20, 50);
                                    toast.show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                App.preference().setIssociallogin("1");
                                App.preference().setSocialLogin(true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Log.e("error",new String(error.networkResponse.data, "utf-8"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        loader.stopLoading();
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.str_txt_try_again_msg), Toast.LENGTH_SHORT).show();
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
                params = getParamsForCheckSocialLogin(id);
                return params;
            }
        };

        login.setRetryPolicy(new RetryPolicy() {
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
        App.instance().addToRequestQueue(login);
    }

    /**Get params for check social login api **/

    public HashMap<String, String> getParamsForCheckSocialLogin(String auth_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("auth_id", auth_id);
        params.put("auth_type", "facebook");
        params.put("player_id",App.preference().getPlayerId());
        params.put("device_token","");
        params.put("device_type","android");
        Log.e("Social Login param ", params.toString());
        return params;
    }

    /**
     * Facebook login code logics  -----  ENDS HERE  -----
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                callApiForLogin();
                break;
            case R.id.fb_login_button_wrapper:
                m_buttonLoginFB.callOnClick();
                break;

        }
    }

    /**
     * Email login code logics  -----  STARTS HERE  -----
     */

    /** On successful validation, call login api **/

    private void callApiForLogin() {
        if (validateLoginFields()) {
            apiCallLogin();
        }
    }
    
    /**API call to login using mail id **/

    private void apiCallLogin() {
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        loader.getCommanLoading();
        StringRequest login = new StringRequest(Request.Method.POST, Config.ApiUrls.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loader.stopLoading();
                        Log.e("Response ", response.toString());
                        try {
                            JSONObject obj=new JSONObject(response);
                            String message = obj.getString("status_message");
                            boolean status=obj.getString("status").equalsIgnoreCase("true");
                            if (status) {
                                storeLoginDetails(new JSONObject(response));
                            }
                            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 20, 50);
                            toast.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.stopLoading();
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.str_txt_try_again_msg), Toast.LENGTH_SHORT).show();
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
                params = getParamsForLogin();
                return params;
            }
        };

        login.setRetryPolicy(new RetryPolicy() {
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
        App.instance().addToRequestQueue(login);
    }

    /**Need to get the params needed for login**/

    private HashMap<String, String> getParamsForLogin() {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", m_editUsername.getText().toString());
        params.put("password", m_editPassword.getText().toString());
        params.put("player_id", App.preference().getPlayerId());
        params.put("device_token", "");
        params.put("device_type", "android");
        Log.e(TAG, "Api Call Login params " + params.toString());
        return params;
    }

    /**Validate the login fields before Api call hit**/
    private boolean validateLoginFields() {
        boolean validate = true;
        if (m_editUsername.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getResources().getString(R.string.str_msg_login_empty_email), Toast.LENGTH_SHORT).show();
        } else if (m_editPassword.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(this, getResources().getString(R.string.str_msg_login_empty_password), Toast.LENGTH_SHORT).show();

        } else if (m_editPassword.getText().toString().length() < 6) {
            validate = false;
            Toast.makeText(this, getResources().getString(R.string.str_msg_login_valid_password), Toast.LENGTH_SHORT).show();

        }
        return validate;
    }

    /**
     * Email login code logics  -----  ENDS HERE  -----
     */


    /**Navigation after successful login **/

    private void navAfterLogin() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**Before navigation store all details inside the application **/
    private void storeLoginDetails(JSONObject responseObj) {
        try {
            Preferences pref = App.preference();
            JSONObject result = responseObj.getJSONObject("details");
            pref.setUserId(result.getString("id"));
            pref.setAccessToken(result.getString("access_token"));
            pref.setUserName(result.getString("username"));
            pref.setProfileImage(result.getString("profile_pic"));
            pref.setFirstName(result.getString("first_name"));
            pref.setEmailAddress(result.has("email") ? result.getString("email") : m_editUsername.getText().toString().toLowerCase().trim());
            pref.setGender(result.getString("gender"));
            pref.setDescription(result.getString("description"));
            pref.setBirthDate(result.getString("dob"));

            pref.setLastName(result.getString("last_name"));
            pref.setCountry(result.getString("country"));
            pref.setState(result.getString("state"));
            pref.setIsPrivate(result.getString("is_private"));

            pref.setFollowerCount(result.getString("follower_count"));
            pref.setFollowingCount(result.getString("following_count"));
            pref.setPostCount(result.getString("post_count"));
            pref.setRole(result.getString("role"));
            pref.setUnreadCount(result.getString("unread_count"));
            pref.setIsNotify(result.getString("is_notify"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navAfterLogin();
    }

}

