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
import com.androidapp.instasocial.modules.profile.api.BlockRequestBean;
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
 * Adapter class to load data on blocked list
 */
public class BlockRequestListAdapter extends RecyclerView.Adapter<BlockRequestListAdapter.BlockRequestHolder> implements ResponseCallBack {
    Context context;
    ArrayList<BlockRequestBean> blockRequestBeans;
    RequestApiCall requestApiCall;
    RelativeLayout progress_lay;
    public BlockRequestListAdapter(Context context, ArrayList<BlockRequestBean> blockRequestBeans, RelativeLayout progress_lay){
        this.context=context;
        this.blockRequestBeans=blockRequestBeans;
        this.progress_lay=progress_lay;
        requestApiCall = new RequestApiCall(context);
    }
    @NonNull
    @Override
    public BlockRequestListAdapter.BlockRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_request_items,null);
        return new BlockRequestListAdapter.BlockRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockRequestListAdapter.BlockRequestHolder holder, final int position) {
        holder.userName.setText(blockRequestBeans.get(position).name);
        Picasso.with(context).load(Config.ApiUrls.PROFILE_URL+blockRequestBeans.get(position).id).into(holder.userImage);
        //Unblock onclick
        holder.btnUnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParamsForBlockStatus("4",blockRequestBeans.get(position).id,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockRequestBeans.size();
    }



    public class BlockRequestHolder extends RecyclerView.ViewHolder{
        CircleImageView userImage;
        CompatTextView userName,btnUnBlock;
        public BlockRequestHolder(View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.userImage);
            userName=itemView.findViewById(R.id.userName);
            btnUnBlock=itemView.findViewById(R.id.unblock);
        }
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        progress_lay.setVisibility(View.GONE);
        Log.e("Unblock", response);
        if(TAG.equals(ApiTags.TAG_UNBLOCK_USER)){
            parseUnBlockRequestApi(response,pageNo);

        }
    }

    /**
     * Parse the response received from api
     * @param response
     * @param position
     */
    private void parseUnBlockRequestApi(String response,int position){

        try {
            JSONObject respObj = new JSONObject(response);
            if(respObj.getString("status").equals("true")){
                respObj.getString("message").equalsIgnoreCase("core.unblocked_success");
                App.showToast(App.getStringRes(R.string.str_txt_unblock_user));
            }
            blockRequestBeans.remove(position);
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

    // Status : 1-follow,2-block,3-unfollow,4-unblock
    private void getParamsForBlockStatus(String type,String id,int position){
        HashMap<String,String> params = new HashMap<>();
        params.put(ApiParams.USER_ID, App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN,App.preference().getAccessToken());
        params.put(ApiParams.TYPE,type);
        params.put(ApiParams.MEMBER_ID,id);
        callForBtnUnblock(params,position);

    }

    /**
     * API call to unblock user
     * @param params
     * @param position
     */
    private void callForBtnUnblock(HashMap<String,String> params,int position){
        progress_lay.setVisibility(View.VISIBLE);
        requestApiCall.postRequestMethodApiCall(this,Config.ApiUrls.FOLLOW_UNFOLLOW_BLOCK,params, ApiTags.TAG_UNBLOCK_USER,position);
    }
}
