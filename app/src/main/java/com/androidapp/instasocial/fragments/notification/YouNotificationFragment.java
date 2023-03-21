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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.profile.FeedDetailFragment;
import com.androidapp.instasocial.fragments.profile.FollowRequestListFragment;
import com.androidapp.instasocial.modules.notification.NotificationAdapter;
import com.androidapp.instasocial.modules.notification.NotificationBean;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.states.NotificationType;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.ItemClickListener;
import com.androidapp.instasocial.webservice.ApiParams;
import com.androidapp.instasocial.webservice.ApiTags;
import com.androidapp.instasocial.webservice.RequestApiCall;
import com.androidapp.instasocial.webservice.ResponseCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Notification - You tab UI class
 */
public class YouNotificationFragment extends Fragment implements ItemClickListener<NotificationBean>, ResponseCallBack {

    /**
     * Member variables declarations/initializations
     */
    View rootView;
    NotificationAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<NotificationBean> notifications = new ArrayList<>();
    int pageNo = 1;
    CompatTextView noData;
    RelativeLayout progress_lay;
    PullToRefreshLayout swipe_notification;
    RequestApiCall requestApiCall;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    HomeActivity homeActivity;

    /**
     * Fragment instance creation
     * @return
     */
    public static YouNotificationFragment newInstance(){
        YouNotificationFragment frag=new YouNotificationFragment();
        return frag;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity= (HomeActivity) getActivity();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout._notification_list_, null);
        initControls();
        apiCallNotificationList();
        requestApiCall = new RequestApiCall(getActivity());
        return rootView;
    }

    /**
     * UI controls initialization
     */
    private void initControls() {
        adapter = new NotificationAdapter(getActivity(), notifications);
        adapter.setItemClickListener(this);
        recyclerView = rootView.findViewById(R.id.notificationList);
        final  LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        noData=rootView.findViewById(R.id.noData);
        noData.setVisibility(View.GONE);
        progress_lay=rootView.findViewById(R.id.progress_lay);
        swipe_notification=rootView.findViewById(R.id.swipe_notification);
        swipe_notification.setPullable(true);
        swipe_notification.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                notifications.clear();
                adapter.notifyDataSetChanged();
                apiCallNotificationList();
            }
        });

        /**
         * loadmore for list
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = YouNotificationFragment.this.recyclerView.getChildCount();
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
                    apiCallNotificationList();
                    loading = true;
                }
            }

        });

    }

    /**
     * notification api call
     */
    private void apiCallNotificationList() {
        progress_lay.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        String notificationUrl = Config.ApiUrls.NOTIFICATIONURL + "?user_id=" + App.preference().getUserId() + "&access_token=" + App.preference().getAccessToken() + "&page_no=" + pageNo+"&type=1";
        Log.e("Test","url: "+notificationUrl);
        StringRequest request = new StringRequest(Request.Method.GET, notificationUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("apiCallProfileInfo", "response: " + response);
                    swipe_notification.setRefreshing(false);
                    parseNotificationResponse(new JSONObject(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                noData.setVisibility(notifications.isEmpty()?View.VISIBLE:View.GONE);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        noData.setVisibility(notifications.isEmpty()?View.VISIBLE:View.GONE);
                        Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return new HashMap<String, String>();
            }
        };

        request.setRetryPolicy(new RetryPolicy() {
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
                        Log.e("VOLLEY", "request TimeOut");
                    }
                }).start();
            }
        });
        App.instance().addToRequestQueue(request);
    }

    /**
     * Parse response from api
     * @param respObj
     */
    public void parseNotificationResponse(JSONObject respObj) {
        try {
            progress_lay.setVisibility(View.GONE);
            String status = respObj.getString("status");
            JSONArray result = respObj.getJSONArray("you_feeds");
            App.preference().setNotificationCountStr(respObj.has("unread_count")?respObj.getString("unread_count"):"0");

            if (status.equals("true")) {


                    for (int i = 0; i < result.length(); i++) {
                        JSONObject resultObj = result.getJSONObject(i);
                        NotificationBean notificationBean = new NotificationBean();
                        notificationBean.notification_id = resultObj.getString("notification_id");
                        notificationBean.title = resultObj.getString("title");
                        notificationBean.type = NotificationType.parse(resultObj.getString("type"));
                        notificationBean.object_id = resultObj.getString("object_id");
                        notificationBean.message = resultObj.getString("message");
                        notificationBean.individualmessage = resultObj.getString("individualmessage");
                        notificationBean.sender_id = resultObj.getString("sender_id");
                        notificationBean.author_name = resultObj.getString("author_name");
                        notificationBean.profile_pic = resultObj.getString("profile_pic");
                        notificationBean.status = resultObj.getString("status");
                        notificationBean.created_at = resultObj.getString("created_at");
                        notificationBean.setCreatedAgo(resultObj.has("created_at_ago")?resultObj.getString("created_at_ago"):"");
                        notificationBean.setOwnerId(resultObj.has("ID")?resultObj.getString("ID"):"");
                        notifications.add(notificationBean);
                    }
                    adapter.notifyDataSetChanged();

            }else{
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(NotificationBean notification, int position) {
        notificationSeenUnseenStatus(notification,position);
        switch (notification.type) {
            case request_sent:
                //if follow request sent to private user : navigate to request list page
                if (getActivity() instanceof  HomeActivity) {
                    HomeActivity.addFragment(new FollowRequestListFragment(), getActivity().getSupportFragmentManager());
                }
                break;
            case request_accept:
                try {
                    startActivity(ProfileActivity.getArgIntent(getActivity(), notification.sender_id, ""));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //if private user accept follow request
                break;
            case follow:
                //if public user followed by
                try {
                    startActivity(ProfileActivity.getArgIntent(getActivity(), notification.sender_id, ""));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case like:
                //if any likes on owner's feed
                homeActivity.views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), true);
                break;
            case comment:
                //if any comment on owner's feed
                homeActivity.views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), true);
                break;
            case post:
                //if follower post a new post
                homeActivity.views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), true);
                break;
            case video_post:
                homeActivity.views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), false);

            default:
        }
    }

    /**
     * update whether notification is seen or not
     * @param notification item
     * @param position position of the selected notification
     */
    private void notificationSeenUnseenStatus(final NotificationBean notification, final int position) {
        if (notification.status!=null && notification.status.equalsIgnoreCase("1"))return;
        HashMap<String,String> params = new HashMap<>();
        params.put(ApiParams.USER_ID, App.preference().getUserId());
        params.put(ApiParams.ACCESS_TOKEN,App.preference().getAccessToken());
        params.put(ApiParams.NOTIFICATION_ID,notification.getNotification_Id());
        ResponseCallBack responseCallBack=new ResponseCallBack() {
            @Override
            public void onResponse(String response, int pageNo, String TAG) {
                Log.e("notification_status", response);
                if(TAG.equals(ApiTags.NOTIFICATION_SEEN_UNSEEN)){
                    try {
                        JSONObject obj=new JSONObject(response);
                        boolean status=obj.has("status")?obj.getBoolean("status"):false;
                        if (status){
                            App.preference().setNotificationCount(obj.getInt("unread_count"));
                            notification.status="1";
                            adapter.notifyItemChanged(position);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(String response, int pageNo, String TAG) {

            }
        };
        requestApiCall.postRequestMethodApiCall(responseCallBack,Config.ApiUrls.NOTIFICATION_SEEN_STATUS_UPDATE,params, ApiTags.NOTIFICATION_SEEN_UNSEEN,position);
    }

    @Override
    public void onResponse(String response, int pageNo, String TAG) {
        Log.e("notification_status", response);
        if(TAG.equals(ApiTags.NOTIFICATION_SEEN_UNSEEN)){
        }
    }

    @Override
    public void onErrorResponse(String response, int pageNo, String TAG) {

    }
}
