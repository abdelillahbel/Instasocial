/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Notification and account related settings
 */
public class ManualSettingsFragment extends Fragment implements View.OnClickListener{

    /**
     * Member variables declarations/initializations
     */
    View rootView;
    CustomLoader customLoader;
    SwitchCompat private_toggle,notify_toggle;
    CompatImageView prof_back;
    View layoutPrivateAccount;

    /**
     * back click functionality
     */
    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.prof_back:
            getActivity().getSupportFragmentManager().popBackStack();
            break;
    }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      rootView=LayoutInflater.from(getActivity()).inflate(R.layout._manual_setting_,null);
       initControls();
       initListeners();
      return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * UI controls initialization
     */
    private void initControls(){
        customLoader=new CustomLoader(getActivity());
        private_toggle=rootView.findViewById(R.id.private_toggle);
        notify_toggle=rootView.findViewById(R.id.notify_toggle);
        prof_back=rootView.findViewById(R.id.prof_back);
        layoutPrivateAccount=rootView.findViewById(R.id.layoutPrivateAccount);
        if(App.preference().getIsPrivate().equals("1")){
            private_toggle.setChecked(true);
        }
        if(App.preference().getIsNotify().equals("1")){
            notify_toggle.setChecked(true);
        }
        private_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                apiCallManualSettings();
            }
        });

        notify_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCallManualSettings();
            }
        });
        layoutPrivateAccount.setVisibility(Config.AUTO_PRIVATE_USER?View.GONE:View.VISIBLE);

    }

    /**
     * Register listeners
     */
    private void initListeners(){
        prof_back.setOnClickListener(this);
    }

    /**
     * API call to send user's settings
     */
    private void apiCallManualSettings(){
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        customLoader.getCommanLoading();
        StringRequest manualSettings = new StringRequest(Request.Method.PUT, Config.ApiUrls.USER_SETTINGS_PAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        customLoader.stopLoading();
                        Log.e("Response " ,response.toString());
                        try {
                            if(new JSONObject(response).getString("status").equals("true")) {
                                storeSettingsDetail(new JSONObject(response));
                                Config.showToast(getActivity(), new JSONObject(response).getString("status_message"));
                            }else{
                                Config.showToast(getActivity(), new JSONObject(response).getString("status_message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        customLoader.stopLoading();
                        Toast.makeText(getActivity(), getResources().getString(R.string.str_txt_try_again_msg), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params=getParamsForManualSetting();
                return params;
            }
        };

        manualSettings.setRetryPolicy(new RetryPolicy() {
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
                        customLoader.stopLoading();
                    }
                }).start();
            }
        });
        App.instance().addToRequestQueue(manualSettings);
    }

    /**
     * build hashmap params
     * @return
     */
    private HashMap<String,String> getParamsForManualSetting(){
      HashMap<String,String> params = new HashMap<>();
      params.put("userid",App.preference().getUserId());
      params.put("access_token",App.preference().getAccessToken());
      if(notify_toggle.isChecked())
      params.put("is_notify","1");
      else
      params.put("is_notify","0");
        if (private_toggle.isChecked())
            params.put("is_private", "1");
        else
            params.put("is_private", "0");
      Log.e("Settings api ",params.toString());
        return  params;
    }

    /**
     * Store details from parsed web response
     * @param responseObj
     */
    private void storeSettingsDetail(JSONObject responseObj){
        try {
            Preferences pref = App.preference();
            JSONObject result = responseObj.getJSONObject("details");
            pref.setUserId(result.getString("id"));
            pref.setAccessToken(result.getString("access_token"));
            pref.setUserName(result.getString("username"));
            pref.setFirstName(result.getString("first_name"));
            pref.setEmailAddress(result.getString("email"));
            pref.setGender(result.getString("gender"));
            pref.setDescription(result.getString("description"));
            pref.setBirthDate(result.getString("dob"));
            pref.setLastName(result.getString("last_name"));
            pref.setCountry(result.getString("country"));
            pref.setState(result.getString("state"));
            pref.setIsPrivate(result.getString("is_private"));
            pref.setIsNotify(result.getString("is_notify"));
            pref.setFollowerCount(result.getString("follower_count"));
            pref.setFollowingCount(result.getString("following_count"));
            pref.setPostCount(result.getString("post_count"));
            pref.setRole(result.getString("role"));
            pref.setUnreadCount(result.getString("unread_count"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
