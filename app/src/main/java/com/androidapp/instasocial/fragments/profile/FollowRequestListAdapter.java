/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.profile.api.FollowRequestBean;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter class to load data in Profile ==> Follow request
 */
public class FollowRequestListAdapter extends RecyclerView.Adapter<FollowRequestListAdapter.FollowRequestHolder> implements ResponseCallBack {

    /**
     * Member variables declarations/initializations
     */
    Context context;
    ArrayList<FollowRequestBean> followRequestBeans;
    RequestApiCall requestApiCall;
    RelativeLayout progress_lay;
    public FollowRequestListAdapter(Context context, ArrayList<FollowRequestBean> followRequestBeans, RelativeLayout progress_lay){
       this.context=context;
       this.followRequestBeans=followRequestBeans;
       this.progress_lay=progress_lay;
       requestApiCall = new RequestApiCall(context);
    }
    @NonNull
    @Override
    public FollowRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_request_repeat_item,null);
        return new FollowRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowRequestHolder holder, final int position) {
     holder.userName.setText(followRequestBeans.get(position).name);
     Picasso.with(context).load(Config.ApiUrls.PROFILE_URL+followRequestBeans.get(position).id).into(holder.userImage);

     //Accepting folow request
     holder.btnAccept.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             getParamsForFollowStatus("1",followRequestBeans.get(position).connect_id,followRequestBeans.get(position).id,position);
         }
     });

     //Deny follow request
     holder.btnDecline.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             getParamsForFollowStatus("0",followRequestBeans.get(position).connect_id,followRequestBeans.get(position).id,position);

         }
     });
    }

    @Override
    public int getItemCount() {
        return followRequestBeans.size();
    }



    public class FollowRequestHolder extends RecyclerView.ViewHolder{
          CircleImageView userImage;
          CompatTextView userName,btnAccept,btnDecline;
        public FollowRequestHolder(View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.userImage);
            userName=itemView.findViewById(R.id.userName);
            btnAccept=itemView.findViewById(R.id.btnAccept);
            btnDecline=itemView.findViewById(R.id.btnDecline);
        }
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        progress_lay.setVisibility(View.GONE);
        Log.e("Follow accept decineapi", response);
        if(TAG.equals(ApiTags.TAG_FOLLOW_REQUEST_LIST_ACTION)){
          parseAcceptDeclineRequestApi(response,pageNo);

        }
    }

    /**
     * Parse api response and show appropriate message
     * @param response
     * @param position
     */
    private void parseAcceptDeclineRequestApi(String response,int position){
        try {
            switch (new JSONObject(response).getJSONObject("result").getString("message")){
                case "accept":
                    App.showToast(App.getStringRes(R.string.str_txt_accept_request));
                    break;
                case "decline":
                    App.showToast(App.getStringRes(R.string.str_txt_decline_request));
                    break;
            }
            followRequestBeans.remove(position);
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

   // Status : 1-Accept, 0-Decline
    private void getParamsForFollowStatus(String status,String connection_id,String id,int position){
        HashMap<String,String> params = new HashMap<>();
        params.put(ApiParams.USERID, App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN,App.preference().getAccessToken());
        params.put(ApiParams.STATUS,status);
        params.put(ApiParams.FOLLOWER_ID,id);
        params.put(ApiParams.CONNECT_ID,connection_id);
        apiCallForBtnAcceptDecline(params,position);

    }

    /**
     * Accept/Deny follow request --> api call
     * @param params
     * @param position
     */
    private void apiCallForBtnAcceptDecline(HashMap<String,String> params,int position){
        progress_lay.setVisibility(View.VISIBLE);
        requestApiCall.postRequestMethodApiCall(this,Config.ApiUrls.FOLLOW_REQUEST_ACCEPT,params, ApiTags.TAG_FOLLOW_REQUEST_LIST_ACTION,position);
    }
}
