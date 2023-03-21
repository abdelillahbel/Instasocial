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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.profile.api.FollowRequestBean;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.DividerItemDecorator;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Profile ==> Follow request UI class
 */

public class FollowRequestListFragment extends Fragment implements View.OnClickListener, ResponseCallBack {

    /**
     * Member variables declarations/initializations
     */
    View rootView;
    RequestApiCall requestApiCall;
    FollowRequestListAdapter followRequestListAdapter;
    int pageNo=1;
    CompatImageView icNavigation;
    RecyclerView followRequestList;
    CompatTextView txtNoData;
    RelativeLayout progress_lay;
    LinearLayoutManager linearLayoutManager;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ArrayList<FollowRequestBean> followRequestBeanArrayList= new ArrayList<>();
    PullToRefreshLayout followPullToRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.follow_request_list, null);
        initControls();
        initListeners();
        initWebControls();
        apiCallGetFollowRequestList();
        return rootView;
    }

    /**
     * UI controls initialization
     */
    private void initControls() {
        txtNoData=rootView.findViewById(R.id.txtNoData);
        icNavigation = rootView.findViewById(R.id.icNavigation);
        progress_lay=rootView.findViewById(R.id.progress_lay);
        followRequestList = rootView.findViewById(R.id.followRequestList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        followRequestList.setLayoutManager(linearLayoutManager);
        followRequestListAdapter =new FollowRequestListAdapter(getActivity(),followRequestBeanArrayList,progress_lay);
        followRequestList.setAdapter(followRequestListAdapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        followRequestList.addItemDecoration(dividerItemDecoration);
        followPullToRefresh=rootView.findViewById(R.id.followPullToRefresh);
         init();
    }
    private void init(){
        followPullToRefresh.setPullable(true);

        //pull to refresh
        followPullToRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                followRequestBeanArrayList.clear();
                followRequestListAdapter.notifyDataSetChanged();
                apiCallGetFollowRequestList();
            }
        });

        //Load more data
        followRequestList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = followRequestList.getChildCount();
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
                    apiCallGetFollowRequestList();
                    loading = true;
                }
            }

        });

    }

    /**
     * Register listeners
     */
    private void initListeners() {
      icNavigation.setOnClickListener(this);
    }

    /**
     * init api controls
     */
    private void initWebControls() {
        requestApiCall=new RequestApiCall(getActivity());
    }
    private void txtNoDataVisibility(int visibility){
        txtNoData.setVisibility(visibility);
    }


    /**
     * Api call to fetch follow requests
     */
    private void apiCallGetFollowRequestList(){
        progress_lay.setVisibility(View.VISIBLE);
        txtNoDataVisibility(View.GONE);
        requestApiCall.getRequestMethodApiCall(this,getFollowRequestListUrl() , ApiTags.TAG_FOLLOW_REQUEST_LIST,1);
    }

    /**
     * generate follow request api call
     * @return
     */
    private String getFollowRequestListUrl(){
        return Config.ApiUrls.FOLLOW_REQUEST_LIST+"?"+
                ApiParams.getUserCredential()+"&"+
                ApiParams.PAGE_NO+"="+pageNo;
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        progress_lay.setVisibility(View.GONE);
        followPullToRefresh.setRefreshing(false);
        if(TAG.equals(ApiTags.TAG_FOLLOW_REQUEST_LIST)){
            parseFollowRequestListApiResponse(response,pageNo);
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }

    @Override
    public void onClick(View v) {
      switch (v.getId())
      {
          case R.id.icNavigation:
              getActivity().getSupportFragmentManager().popBackStack();
              break;
      }
    }

    /**
     * Parse response from api
     * @param response
     * @param pageNo
     */
    private void parseFollowRequestListApiResponse(String response,int pageNo){
        try {
            JSONObject respObj = new JSONObject(response);
            if(respObj.getString("status").equals("true")){
                JSONArray resultArray = respObj.getJSONArray("result");
                    txtNoDataVisibility(View.GONE);
                    for (int i = 0; i < resultArray.length(); i++) {
                        addToFollowerList(resultArray.getJSONObject(i));
                    }
                    txtNoData.setVisibility(followRequestBeanArrayList.isEmpty()?View.VISIBLE:View.GONE);
                    followRequestListAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * add response to list and update data
     * @param resultObj
     */
    private void addToFollowerList(JSONObject resultObj){
        FollowRequestBean followRequestBean =new FollowRequestBean();
        try {
            followRequestBean.connect_id=resultObj.getString("connect_id");
            followRequestBean.id = resultObj.getString("id");
            followRequestBean.name=resultObj.getString("name");
            followRequestBeanArrayList.add(followRequestBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
