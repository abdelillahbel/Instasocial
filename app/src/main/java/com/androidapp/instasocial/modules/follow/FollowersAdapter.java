/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.follow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.CompatImageView;
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
 * Adapter class to load data in Followers list
 */

/**
 * UserFollowStatus
 * 0 - Not Followed
 * 1 - Followed
 * 2 - Request sent
 */
public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersHolder> implements ResponseCallBack{
    Context context;
    ArrayList<FollowBean> followBeanArrayList;
    RequestApiCall requestApiCall;
    public FollowersAdapter(Context context, ArrayList<FollowBean> followBeanArrayList){
        this.context=context;
        this.followBeanArrayList=followBeanArrayList;
        requestApiCall=new RequestApiCall(context);
    }
    @Override
    public FollowersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_list_repeat_item,null);
      return new  FollowersHolder(view);
     }

    @Override
    public void onBindViewHolder(FollowersHolder holder, final int position) {
        Picasso.with(context).load(followBeanArrayList.get(position).profile_pic)
                .placeholder(R.color.colorPrimaryLightDark)
                .error(R.color.colorPrimaryLightDark)
                .fit().into(holder.userImage);
        holder.userName.setText(followBeanArrayList.get(position).name);

        switch (followBeanArrayList.get(position).user_follow_status){
            case "0":
                holder.imgFollowStatus.setImageResource(R.drawable.ic_follow); //follow
                break;
            case "1":
                holder.imgFollowStatus.setImageResource(R.drawable.ic_unfollow); //unfollow
                break;
            case "2":
                holder.imgFollowStatus.setImageResource(R.drawable.ic_request); //follow request
                break;
        }

        holder.imgFollowStatus.setColorFilter(App.getColorRes(
                followBeanArrayList.get(position).user_follow_status.equalsIgnoreCase("1")?
                R.color.colorPrimary:R.color.colorPrimaryLightDark
        ));
        /**
         * Redirect to user's profile
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.startActivity(ProfileActivity.getArgIntent((Activity) context, followBeanArrayList.get(position).user_id, followBeanArrayList.get(position).name));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        /**
         * Follow or unfollow actions
         */
        holder.imgFollowStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (followBeanArrayList.get(position).user_follow_status){
                    case "0":
                        apiCallFollowUnFollow(position,followBeanArrayList.get(position).user_id,"1");
                        break;
                    case "1":
                        apiCallFollowUnFollow(position,followBeanArrayList.get(position).user_id,"3");
                        break;
                }
            }
        });

        /**
         * Logics if logged in user and user from list is owner
         */
        try {
            boolean isOwnUser = App.preference().getUserId().equalsIgnoreCase(followBeanArrayList.get(position).user_id);
            holder.imgFollowStatus.setVisibility(isOwnUser ? View.GONE : View.VISIBLE);
            holder.txtYou.setVisibility(isOwnUser ? View.VISIBLE : View.GONE);
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return followBeanArrayList.size();
    }

    @Override
    public void onResponse(String response, int position, String TAG) {
        try {
            if(TAG.equals(ApiTags.TAG_FOLLOW_UNFOLLOW)){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                    if (status) {
                        followBeanArrayList.get(position).user_follow_status=jsonObject.getString("user_follow_status");
                        notifyItemChanged(position);
                        switch (followBeanArrayList.get(position).user_follow_status) {
                            case "1":
                                App.showToast(App.getStringRes(R.string.str_txt_follow_success));
                                break;
                            case "0":
                                App.showToast(App.getStringRes(R.string.str_txt_un_follow_success));
                                break;
                            case "2":
                                App.showToast(App.getStringRes(R.string.str_txt_request_success));
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }
    class FollowersHolder extends RecyclerView.ViewHolder{
        CircleImageView userImage;
        CompatTextView userName;
        CompatImageView imgFollowStatus;
        TextView txtYou;
        public FollowersHolder(View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.userName);
            userImage=itemView.findViewById(R.id.userImage);
            imgFollowStatus=itemView.findViewById(R.id.imgFollowStatus);
            txtYou=itemView.findViewById(R.id.txtYou);
        }
    }
    //1-follow,2-block,3-unfollow,4-unblock
    public void apiCallFollowUnFollow(final  int position,final String memberID, final String type) {

        HashMap<String,String> params = new HashMap<>();
        params.put(ApiParams.USER_ID,App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN,App.preference().getAccessToken());
        params.put(ApiParams.TYPE,type);
        params.put(ApiParams.MEMBER_ID,memberID);
      requestApiCall.postRequestMethodApiCall(this,Config.ApiUrls.FOLLOW_UNFOLLOW_BLOCK,params ,ApiTags.TAG_FOLLOW_UNFOLLOW,position);
    }

}
