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
import com.androidapp.instasocial.modules.profile.api.BlockRequestBean;
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
 * View list of blocked users
 */

public class UserBlockedListFragment extends Fragment implements View.OnClickListener, ResponseCallBack {
    View rootView;
    RequestApiCall requestApiCall;
    BlockRequestListAdapter blokcedRequestListAdapter;
    int pageNo=1;
    CompatImageView icNavigation;
    RecyclerView blockedRequestList;
    CompatTextView txtNoData;
    RelativeLayout progress_lay;
    LinearLayoutManager linearLayoutManager;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ArrayList<BlockRequestBean> blockedBeanArrayList= new ArrayList<>();
    PullToRefreshLayout blockedPullToRefresh;
    CompatImageView user_sett_back;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.user_blocked_list, null);
        initControls();
        initListeners();
        initWebControls();
        apiCallGetBlockedRequestList();
        return rootView;
    }

    /**
     * UI controls initialization
     */
    private void initControls() {
        txtNoData=rootView.findViewById(R.id.txtNoData);
       // icNavigation = rootView.findViewById(R.id.icNavigation);
        progress_lay=rootView.findViewById(R.id.progress_lay);
        blockedRequestList = rootView.findViewById(R.id.blockedRequestList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        blockedRequestList.setLayoutManager(linearLayoutManager);
        blokcedRequestListAdapter =new BlockRequestListAdapter(getActivity(),blockedBeanArrayList,progress_lay);
        blockedRequestList.setAdapter(blokcedRequestListAdapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider));
        blockedRequestList.addItemDecoration(dividerItemDecoration);
        blockedPullToRefresh=rootView.findViewById(R.id.blockedPullToRefresh);
        user_sett_back=rootView.findViewById(R.id.user_sett_back);
        init();
    }
    private void init(){
        blockedPullToRefresh.setPullable(true);
        blockedPullToRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                blockedBeanArrayList.clear();
                blokcedRequestListAdapter.notifyDataSetChanged();
                apiCallGetBlockedRequestList();
            }
        });
        blockedRequestList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = blockedRequestList.getChildCount();
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
                    apiCallGetBlockedRequestList();
                    loading = true;
                }
            }

        });

    }

    private void initListeners() {
        user_sett_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void initWebControls() {
        requestApiCall=new RequestApiCall(getActivity());
    }
    private void txtNoDataVisibility(int visibility){
        txtNoData.setVisibility(visibility);
    }

    private void apiCallGetBlockedRequestList(){
        progress_lay.setVisibility(View.VISIBLE);
        txtNoDataVisibility(View.GONE);
        requestApiCall.getRequestMethodApiCall(this,getBlokcedRequestListUrl() , ApiTags.TAG_BLOCK_USER_LIST,1);
    }

    /**
     * APi call to list the blocked users
     * @return
     */
    private String getBlokcedRequestListUrl(){
        return Config.ApiUrls.BLOCK_USER_LIST+"?"+
                ApiParams.getUserCredential()+"&"+
                ApiParams.PAGE_NO+"="+pageNo;
    }

    /**
     * Blocked users list response
     * @param response
     * @param pageNo
     * @param TAG
     */
    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        progress_lay.setVisibility(View.GONE);
        blockedPullToRefresh.setRefreshing(false);
        if(TAG.equals(ApiTags.TAG_BLOCK_USER_LIST)){
            parseBlockedRequestListApiResponse(response,pageNo);
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
     * Parse the response of blocked users list
     * @param response
     * @param pageNo
     */
    private void parseBlockedRequestListApiResponse(String response,int pageNo){
        try {
            JSONObject respObj = new JSONObject(response);
            if(respObj.getString("status").equals("true")){
                JSONArray resultArray = respObj.getJSONArray("result");
                txtNoDataVisibility(View.GONE);
                for (int i = 0; i < resultArray.length(); i++) {
                    addToBlockedUserList(resultArray.getJSONObject(i));
                }
                txtNoData.setVisibility(blockedBeanArrayList.isEmpty()?View.VISIBLE:View.GONE);
                blokcedRequestListAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Block an user logic
     * @param resultObj
     */
    private void addToBlockedUserList(JSONObject resultObj){
        BlockRequestBean blockRequestBean =new BlockRequestBean();
        try {
            blockRequestBean.id = resultObj.getString("id");
            blockRequestBean.name=resultObj.getString("name");
            blockRequestBean.first_name=resultObj.getString("first_name");
            blockRequestBean.last_name=resultObj.getString("last_name");
            blockedBeanArrayList.add(blockRequestBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
