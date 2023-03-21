/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.search;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Listing of all users of the app
 */

/**
 * UserFollowStatus
 * 0 - Not Followed
 * 1 - Followed
 * 2 - Request sent
 */
public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.SearchProfileHolder> implements ResponseCallBack {
    Context context;
    ArrayList<SearchProfileBean> searchProfileBeans;
    RequestApiCall requestApiCall;
    UserFollowStatusUpdate statusUpdate;

    public SearchProfileAdapter(Context context, ArrayList<SearchProfileBean> searchProfileBeanArrayList, UserFollowStatusUpdate statusUpdate) {
        this.context = context;
        this.searchProfileBeans = searchProfileBeanArrayList;
        requestApiCall = new RequestApiCall(context);
        this.statusUpdate = statusUpdate;
    }

    @Override
    public SearchProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_profile_repeat_item, null);
        return new SearchProfileHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchProfileHolder holder, final int position) {
        Picasso.with(context).load(Config.ApiUrls.PROFILE_URL + searchProfileBeans.get(position).user_id)
                .placeholder(R.color.colorPrimaryLightDark)
                .error(R.color.colorPrimaryLightDark)
                .fit().centerCrop().into(holder.userImage);
        holder.userName.setText(searchProfileBeans.get(position).username);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.startActivity(ProfileActivity.getArgIntent((Activity) context, searchProfileBeans.get(position).user_id, searchProfileBeans.get(position).name));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        switch (searchProfileBeans.get(position).user_follow_status) {
            case "0":
                holder.imgFollowStatus.setImageResource(R.drawable.ic_follow);
                break;
            case "1":
                holder.imgFollowStatus.setImageResource(R.drawable.ic_unfollow);
                break;
            case "2":
                holder.imgFollowStatus.setImageResource(R.drawable.ic_request);
                break;
        }
        holder.imgFollowStatus.setColorFilter(App.getColorRes(
                searchProfileBeans.get(position).user_follow_status.equalsIgnoreCase("1")?
                        R.color.colorPrimary:R.color.colorPrimaryLightDark
        ));
        holder.imgFollowStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (searchProfileBeans.get(position).user_follow_status) {
                    case "0":
                        apiCallFollowUnFollow(position, searchProfileBeans.get(position).user_id, "1");
                        break;
                    case "1":
                        apiCallFollowUnFollow(position, searchProfileBeans.get(position).user_id, "3");
                        break;
                    case "2":
                        App.showToast(App.getStringRes(R.string.str_requested));
                        break;
                }
            }
        });
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.addFragment(HomeProfileFragment.instance(searchProfileBeans.get(position).user_id,searchProfileBeans.get(position).username,false,false),((AppCompatActivity)context).getSupportFragmentManager());
            }
        });

    }


    @Override
    public int getItemCount() {
        return searchProfileBeans.size();
    }


    public class SearchProfileHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        CompatTextView userName;
        CompatImageView imgFollowStatus;

        public SearchProfileHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            imgFollowStatus = itemView.findViewById(R.id.imgFollowStatus);
        }
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {

        if (TAG.equals(ApiTags.TAG_FOLLOW_UNFOLLOW)) {
            parseResponseFollowAndUnfollow(response, pageNo);
        }

    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

    /**
     * Api call to follow or unfollow an user
     * @param position of the user in the list
     * @param memberID of the user
     * @param type 1-follow,2-block,3-unfollow,4-unblock
     */

    public void apiCallFollowUnFollow(final int position, final String memberID, final String type) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiParams.USER_ID, App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN, App.preference().getAccessToken());
        params.put(ApiParams.TYPE, type);
        params.put(ApiParams.MEMBER_ID, memberID);
        requestApiCall.postRequestMethodApiCall(this, Config.ApiUrls.FOLLOW_UNFOLLOW_BLOCK, params, ApiTags.TAG_FOLLOW_UNFOLLOW, position);

    }

    public interface UserFollowStatusUpdate {
        void onUserFollowStatusUpdate(String userFollowStatusUpdate, String id);
    }

    /**
     *
     * @param response parse the response once followed/unfollowed
     * @param position
     */
    private void parseResponseFollowAndUnfollow(String response, int position) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
            if (status) {
                makeStatusUpdateMessage(position, jsonObject.getString("user_follow_status"));
            }
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    /**
     * Display messages on successful FOLLOW, UNFOLLOW, FOLLOW REQUEST
     * @param position
     * @param followStatus
     */
    private void makeStatusUpdateMessage(int position,String followStatus){
        searchProfileBeans.get(position).user_follow_status = followStatus;
        switch (followStatus) {
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
        statusUpdate.onUserFollowStatusUpdate(followStatus, searchProfileBeans.get(position).user_id);

    }
}
