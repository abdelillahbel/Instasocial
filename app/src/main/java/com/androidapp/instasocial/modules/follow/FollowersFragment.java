/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.follow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Followers list UI class
 */
public class FollowersFragment extends Fragment implements ResponseCallBack,View.OnClickListener {

    /**
     * Member variables declarations/initializations
     */
    View rootView;
    RecyclerView followersList;
    ArrayList<FollowBean> followBeanArrayList;
    FollowersAdapter followersAdapter;
    PullToRefreshLayout swipe_follow;
    int pageNo = 1;
    CompatTextView noData;
    RelativeLayout progress_lay;
    String member_id;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    CompatImageView icNavigation;
    RequestApiCall requestApiCall;
    TextView txtTitle;

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.icNavigation:
             getActivity().getSupportFragmentManager().popBackStack();
             break;
     }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.follower_list, null);
        initControls();
        initListeners();
        initWebControls();
        apiCallForFollowersList();
        return rootView;
    }

    /**
     * UI controls initialization
     */
    private void initControls() {
        followBeanArrayList = new ArrayList<>();
        txtTitle=rootView.findViewById(R.id.txtTitle);
        txtTitle.setText(R.string.str_followers);
        swipe_follow = rootView.findViewById(R.id.swipe_follow);
        icNavigation=rootView.findViewById(R.id.icNavigation);
        swipe_follow.setPullable(true);
        followersList = rootView.findViewById(R.id.followersList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        followersList.setLayoutManager(linearLayoutManager);
        followersAdapter = new FollowersAdapter(getActivity(), followBeanArrayList);
        followersList.setAdapter(followersAdapter);
        noData = rootView.findViewById(R.id.noData);
        progress_lay = rootView.findViewById(R.id.progress_lay);
        swipe_follow.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                followBeanArrayList.clear();
                followersAdapter.notifyDataSetChanged();
                apiCallForFollowersList();
            }
        });

        /**
         * Loadmore logic for followers list
         */
        followersList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = followersList.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    pageNo = pageNo + 1;
                    apiCallForFollowersList();
                    loading = true;
                }
            }

        });


    }

    /**
     * register listeners
     */
    private void initListeners() {
     icNavigation.setOnClickListener(this);
    }

    /**
     * Initializa api controls
     */
    private void initWebControls(){
        requestApiCall= new RequestApiCall(getActivity());
    }

    /**
     * API call to get FollowersList
     */
    public void apiCallForFollowersList() {
        progressVisibility(View.VISIBLE);
        requestApiCall.getRequestMethodApiCall(this,getFollowersList(), ApiTags.TAG_FOLLOWERS_LIST,pageNo);
    }

    /**
     * Fragment instance creation
     */
    public static FollowersFragment newInstance(String member_id){
        FollowersFragment followersFragment=new FollowersFragment();
        followersFragment.member_id=member_id;
        return followersFragment;
    }

    /**
     * Construct followers list api
     * @return
     */
    public String getFollowersList(){
        return Config.ApiUrls.FOLLOWERLIST + "?"+
                ApiParams.getUserCredential()+
                "&"+ApiParams.MEMBER_ID+"="+member_id+"&"+
                ApiParams.PAGE_NO+"=" + pageNo + "&"+
                ApiParams.USERNAME+"=&"+
                ApiParams.RECORD+"=";
    }


    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        parseFollowerResponse(response,pageNo);
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

    /**
     * retrieve the response from api
     * @param response
     * @param pageNo
     */
    private void parseFollowerResponse(String response,int pageNo){
        try {
            uiChangeAfterApiCall();
            JSONObject jResponse = new JSONObject(response);
            JSONArray result = jResponse.getJSONArray("result");
            if (jResponse.getString("status").equals("true")) {
                if (result.length() == 0 && pageNo == 1) {
                   noDataVisibility(View.VISIBLE);
                } else {
                    noDataVisibility(View.GONE);
                    for (int i = 0; i < result.length(); i++) {
                        addToFollowingsList(result.getJSONObject(i));
                    }
                    followersAdapter.notifyDataSetChanged();
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update UI views
     */
    private void uiChangeAfterApiCall(){
        progressVisibility(View.GONE);
        swipe_follow.setRefreshing(false);
    }
    private void noDataVisibility(int visibility){
        noData.setVisibility(visibility);
    }
    private void progressVisibility(int visibility){
        progress_lay.setVisibility(visibility);
    }

    /**
     * Add the parsed data to list
     * @param resObj
     */
    private void addToFollowingsList(JSONObject resObj){
        FollowBean followBean = new FollowBean();
        try {
            followBean.user_id = resObj.getString("user_id");
            followBean.name = resObj.getString("name");
            followBean.profile_pic = resObj.getString("profile_pic");
            followBean.follower_status = resObj.getString("follower_status");
            followBean.is_private = resObj.getString("is_private");
            followBean.user_follow_status = resObj.getString("user_follow_status");
            followBeanArrayList.add(followBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
