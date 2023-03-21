/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.modules.notification.FollowNotificationAdapter;
import com.androidapp.instasocial.modules.notification.followingbean.FollowFeed;
import com.androidapp.instasocial.modules.notification.followingbean.FollowingNotificationList;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.webservice.ApiService;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.ApiUtils;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Notification - Following tab UI class
 */
public class FollowingNotificationFragment extends Fragment implements ResponseCallBack {



    /**
     * Member variables declarations/initializations
     */
    View rootView;
    int pageNo = 1;
    CompatTextView noData;
    RelativeLayout progress_lay;
    PullToRefreshLayout swipe_notification;
    RequestApiCall requestApiCall;
    HomeActivity homeActivity;
    private List<FollowFeed> followFeedList;
    private RecyclerView recyclerView;
    Boolean loadmore = true, dragcall;
    private FollowNotificationAdapter eAdapter;
    LinearLayoutManager linearLayoutManager;
    ApiService apiService;

    /**
     * Fragment instance creation
     * @return
     */
    public static FollowingNotificationFragment newInstance() {
        FollowingNotificationFragment frag = new FollowingNotificationFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.following_notification_layout, null);
        initControls();
        requestApiCall = new RequestApiCall(getActivity());
        apiService = ApiUtils.getAPIService();

        apiCallNotificationList(pageNo, true);
        return rootView;
    }

    /**
     * UI controls initialization
     */
    private void initControls() {

        followFeedList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.follow_notificationList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        noData = rootView.findViewById(R.id.noData);
        noData.setVisibility(View.GONE);
        progress_lay = rootView.findViewById(R.id.progress_lay);
        swipe_notification = rootView.findViewById(R.id.swipe_follow_notification);
        swipe_notification.setPullable(true);
        swipe_notification.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                followFeedList.clear();
                apiCallNotificationList(pageNo, true);
            }
        });

        /**
         * loadmore for list
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int positionView = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItemPos = positionView;
                if ((visibleItemCount + lastVisibleItemPos) >= totalItemCount && loadmore == true) {
                    loadmore = false;
                    onLoadMoreItems();

                }
            }

        });

    }

    private void onLoadMoreItems() {
        pageNo = pageNo + 1;
        apiCallNotificationList(pageNo, false);
    }



    /**
     * notification api call
     */
    private void apiCallNotificationList(final int pageNo, final boolean dragcall) {
        progress_lay.setVisibility(View.VISIBLE);
        this.pageNo = pageNo;
        this.dragcall = dragcall;

        Call<FollowingNotificationList> FollowingNotificationAPICall = apiService.GetFollowingNotificationResponse(App.preference().getUserId(), App.preference().getAccessToken(), 2, pageNo);

        /**
         * Enqueue Callback will be called on response
         */
        FollowingNotificationAPICall.enqueue(new Callback<FollowingNotificationList>() {
            @Override
            public void onResponse(Call<FollowingNotificationList> call, Response<FollowingNotificationList> response) {

                if (response.isSuccessful()) {
                    progress_lay.setVisibility(View.GONE);
                    swipe_notification.setRefreshing(false);

                    List<FollowFeed> followFeedListItems = response.body().getFollowFeeds();
                    for (int i = 0; i < followFeedListItems.size(); i++) {
                        followFeedList.add(followFeedListItems.get(i));
                    }

                    if (followFeedList != null && pageNo == 1) {
                        eAdapter = new FollowNotificationAdapter(followFeedList, getActivity(), homeActivity);
                        recyclerView.setAdapter(eAdapter);
                    } else {
                        //noData.setVisibility(View.VISIBLE);
                    }
                    if (followFeedListItems.size() > 0) {
                        loadmore = true;
                    } else {
                        loadmore = false;
                    }
                    eAdapter.notifyDataSetChanged();
                }
                noData.setVisibility(followFeedList.isEmpty() ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onFailure(Call<FollowingNotificationList> call, Throwable t) {
                noData.setVisibility(followFeedList.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        Log.e("notification_status", response);
        if (TAG.equals(ApiTags.NOTIFICATION_SEEN_UNSEEN)) {
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {
        noData.setVisibility(followFeedList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
