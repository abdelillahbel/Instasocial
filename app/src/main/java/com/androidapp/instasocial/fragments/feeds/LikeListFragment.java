/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.feeds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.feed.FeedLike;
import com.androidapp.instasocial.modules.feed.LikeListAdapter;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is used to view the list of users who've liked the posts
 */

public class LikeListFragment extends Fragment implements View.OnClickListener,ResponseCallBack {

    /**
     * Member variables declarations/initializations
     */
    String postId;
    View rootView;
    LikeListAdapter likeListAdapter;
    ArrayList<FeedLike> feedLikeArrayList = new ArrayList<>();
    int pageNo=1;
    RecyclerView feedLikeList;
    RequestApiCall requestApiCall;
    LinearLayoutManager linearLayoutManager;
    CompatImageView prof_back;
    CompatTextView txtNoData;
    PullToRefreshLayout likePullToRefresh;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    /**
     * backbutton on click
     */
    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case  R.id.prof_back:
             getActivity().getSupportFragmentManager().popBackStack();
             break;
     }
    }

    /**
     * Fragment instance creation
     * @param postId
     * @return
     */
    public static LikeListFragment newInstance(String postId){
        LikeListFragment likeListFragment=new LikeListFragment();
        likeListFragment.postId=postId;
        return likeListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.like_feed_list,null);
        initControls();
        initUIListeners();
        initWebControls();
        apiCallForLikeListing();
        return rootView;
    }

    /**
     * UI controls initialization
     */
    private void initControls(){
        likePullToRefresh = rootView.findViewById(R.id.likePullToRefresh);
        prof_back=rootView.findViewById(R.id.prof_back);
        txtNoData=rootView.findViewById(R.id.txtNoData);
        likeListAdapter =new LikeListAdapter(feedLikeArrayList,0,getActivity());
        feedLikeList =rootView.findViewById(R.id.feedLikeList);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        feedLikeList.setLayoutManager(linearLayoutManager);
        feedLikeList.setAdapter(likeListAdapter);
        init();

    }
    private void init(){

        /**
         * Pull to refresh
         */
        likePullToRefresh.setPullable(true);
        likePullToRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                feedLikeArrayList.clear();
                likeListAdapter.notifyDataSetChanged();
                apiCallForLikeListing();
            }
        });

        /**
         * Loadmore logic
         */
        feedLikeList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = feedLikeList.getChildCount();
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
                    apiCallForLikeListing();
                    loading = true;
                }
            }

        });

    }

    /**
     * Regsiter listeners
     */
    private void initUIListeners(){
        prof_back.setOnClickListener(this);
    }

    /**
     * API call to list liked users
     */
    private void apiCallForLikeListing(){
     requestApiCall.getRequestMethodApiCall(this,getUrlForLikeList(), ApiTags.TAG_LIKE_LIST,pageNo);
    }

    /**
     * API controls initialization
     */
    private void initWebControls(){
        requestApiCall = new RequestApiCall(getActivity());
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        if(TAG.equals(ApiTags.TAG_LIKE_LIST)){
            parseLikeListApi(response);
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

    /**
     * Generate api url
     * @return
     */
    private String getUrlForLikeList(){
        return Config.ApiUrls.LIKE_LIST+"?"+
                ApiParams.getUserCredential()+"&"+
                ApiParams.POST_ID+"="+postId+"&"+
                ApiParams.PAGE_NO+"="+pageNo;
    }

    /**
     * Parse the response obtained from web server
     * @param response
     */
    private void parseLikeListApi(String response){
        try {
            JSONObject respObj = new JSONObject(response);
            JSONArray resultArray = respObj.getJSONArray("result");

            if(respObj.getString("status").equals("true")){
                if(resultArray.length()==0 && pageNo==1){
                    txtNoData.setVisibility(View.VISIBLE);
                }else {
                    txtNoData.setVisibility(View.GONE);
                    for (int i = 0; i < resultArray.length(); i++) {
                        addToLikeList(resultArray.getJSONObject(i));
                    }
                    likeListAdapter.notifyDataSetChanged();
                }
            }else{
                if(resultArray.length()==0 && pageNo==1){
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the parsed response to bean class
     * @param resultObj
     */
    private void addToLikeList(JSONObject resultObj){
        FeedLike bean = new FeedLike();
        try {
            bean.like_id = resultObj.has("like_id")?resultObj.getString("like_id"):"";
            bean.user_id=resultObj.has("user_id")?resultObj.getString("user_id"):"";
            bean.user_name=resultObj.has("user_name")?resultObj.getString("user_name"):"";
            bean.post_id=resultObj.has("post_id")?resultObj.getString("post_id"):"";
            bean.created_at=resultObj.has("created_at")?resultObj.getString("created_at"):"";
            bean.author_pic=resultObj.has("author_pic")?resultObj.getString("author_pic"):"";
            bean.is_private = resultObj.has("is_private")?resultObj.getString("is_private"):"";
            bean.is_follow = resultObj.has("is_follow")?resultObj.getString("is_follow"):"";
            bean.follow_status = resultObj.has("follow_status")?resultObj.getString("follow_status"):"";
            bean.is_block = resultObj.has("is_block")?resultObj.getString("is_block"):"";
            bean.is_requested = resultObj.has("is_requested")?resultObj.getString("is_requested"):"";
            bean.is_follow_button = resultObj.has("is_follow_button")?resultObj.getString("is_follow_button"):"";
            bean.blockedbyme = resultObj.has("blockedbyme")?resultObj.getString("blockedbyme"):"";
            bean.member_request = resultObj.has("member_request")?resultObj.getString("member_request"):"";
            bean.connection_id = resultObj.has("connection_id")?resultObj.getString("connection_id"):"";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        feedLikeArrayList.add(bean);
    }
}
