/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.LoginActivity;
import com.androidapp.instasocial.ui.CompatEditText;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.CustomLoader;
import com.androidapp.instasocial.utils.Config;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    View rootView;
    CompatImageView user_sett_back;
    CompatTextView btnSubmit;
    CompatEditText edtConfirmPass,edtNewPass,edtOldPass;
    CustomLoader customLoader;


    @Override
    public void onClick(View v) {
     switch (v.getId()){
        case R.id.btnSubmit:
            callChangePassword();
         break;
         case R.id.user_sett_back:
             getActivity().getSupportFragmentManager().popBackStack();
             break;
     }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=LayoutInflater.from(getActivity()).inflate(R.layout._layout_change_password_,null);
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
        user_sett_back=rootView.findViewById(R.id.user_sett_back);
        btnSubmit=rootView.findViewById(R.id.btnSubmit);
        edtConfirmPass=rootView.findViewById(R.id.edtConfirmPass);
        edtNewPass=rootView.findViewById(R.id.edtNewPass);
        edtOldPass=rootView.findViewById(R.id.edtOldPass);
        customLoader=new CustomLoader(getActivity());
    }
    private void initListeners(){
        user_sett_back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    /**
     * API call to change password
     */
    private void apiCallForChangePassword(){
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        customLoader.getCommanLoading();
        StringRequest login = new StringRequest(Request.Method.POST, Config.ApiUrls.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        customLoader.stopLoading();
                        Log.e("Response " ,response.toString());
                        try {
                            JSONObject responseObj= new JSONObject(response);

                           if(responseObj.getString("status").equals("true")){
                               Config.showToast(getActivity(),responseObj.getString("status_message"));
                               getActivity().getSupportFragmentManager().popBackStack();
                               Intent loginIntent= new Intent(getActivity(),LoginActivity.class);
                               startActivity(loginIntent);
                               getActivity().finish();
                           }else{
                               Config.showToast(getActivity(),responseObj.getString("status_message"));
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
                params=getParamsForChangePass();
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
                        customLoader.stopLoading();
                    }
                }).start();
            }
        });
        App.instance().addToRequestQueue(login);
    }
    private HashMap<String ,String> getParamsForChangePass(){
        HashMap<String,String> params = new HashMap<>();
        params.put("userid",App.preference().getUserId());
        params.put("access_token",App.preference().getAccessToken());
        params.put("old_password",edtOldPass.getText().toString());
        params.put("new_password",edtNewPass.getText().toString());
        return params;
    }

    /**
     * On successful validation, make api call
     */
    private void callChangePassword(){
     if(validateChangePasswordField()){
         apiCallForChangePassword();
     }
    }

    /**
     * Password field validations
     * @return
     */
    private boolean validateChangePasswordField(){
        boolean validate= true;
        if(edtOldPass.getText().toString().equals("")){
            validate=false;
            Config.showToast(getActivity(),Config.getStringRes(getActivity(),R.string.str_msg_old_pass_empty));
        }else if(edtOldPass.getText().toString().length()<6){
            validate=false;
            Config.showToast(getActivity(),Config.getStringRes(getActivity(),R.string.str_msg_pass_old_length));

        }
        else if(edtNewPass.getText().toString().equals("")){
            validate=false;
            Config.showToast(getActivity(),Config.getStringRes(getActivity(),R.string.str_msg_new_pass_empty));

        }else if(edtNewPass.getText().toString().length()<6){
            validate=false;
            Config.showToast(getActivity(),Config.getStringRes(getActivity(),R.string.str_msg_pass_new_length));

        }
        else if (edtConfirmPass.getText().toString().equals("")){
            validate=false;
            Config.showToast(getActivity(),Config.getStringRes(getActivity(),R.string.str_msg_new_conf_pass_empty));

        }else if(!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString()))
        {
            Config.showToast(getActivity(),Config.getStringRes(getActivity(),R.string.str_msg_pass_do_not_match));

            validate=false;
        }
        return validate;
    }
}
