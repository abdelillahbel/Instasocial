/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.fragments.bottombar;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.profile.RefreshListener;
import com.androidapp.instasocial.modules.feed.FeedAdapter;
import com.androidapp.instasocial.modules.feed.FeedBean;
import com.androidapp.instasocial.modules.feed.FeedHolder;
import com.androidapp.instasocial.modules.feed.MediaDetail;
import com.androidapp.instasocial.modules.feed.api.ApiFeed;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.SeamLessViewPager.delegate.RecyclerViewDelegate;
import com.androidapp.instasocial.ui.SeamLessViewPager.fragment.BaseViewPagerFragment;
import com.androidapp.instasocial.ui.SeamLessViewPager.widget.PullToRefreshLayout;
import com.androidapp.instasocial.utils.BottomTabs;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.androidapp.instasocial.utils.Preferences;
import com.baoyz.widget.PullRefreshLayout;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


/**
 * This class is used to display feeds in Home
 */

public class HomeFeedFragment extends BaseViewPagerFragment {
    private String TAG = "HomeFeedFragment";

    /**
     * Member variables declarations/initializations
     */
    protected RefreshListener refreshListener = null;
    View convertView;
    RecyclerView newfeedsrecycle;
    FeedAdapter feedAdapter;
    RelativeLayout progress_lay;
    LinearLayoutManager linearLayoutManager;
    ProgressWheel progress_wheel_bottom;
    PullToRefreshLayout pullRefreshLayout;

    boolean loadmore = true;
    boolean is_Swipe = false;
    protected boolean isPullable = true;
    int pageNo = 1;
    protected int pageType = ApiFeed.FeedType.home.getValue();

    public ArrayList<FeedBean> myNewsFeedsArrayList;
    private int previousTotal = 0;
    private boolean loading = true;
    Preferences pref;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    Handler mHandler;
    TextView noData;
    protected String memberName = "";
    protected String memberId = "";
    String share_url;
    protected String feedId = "";
    protected boolean hasActionBar = false;
    protected boolean isFeedDetail = false;

    public CompatTextView icMore;
    public View layoutActionbar;
    public ImageView icNavigation;
    public View icSearch;


    /**
     * Constructors and overloaded functions
     */
    public HomeFeedFragment() {
        memberName = "";
        memberId = "";
        setFeedType(ApiFeed.FeedType.home);
        hasActionBar=false;
        isFeedDetail=false;
    }

    /**
     * Fragment instance creation
     * @param hasActionBar
     * @return
     */
    public static HomeFeedFragment newInstance(boolean hasActionBar) {
        HomeFeedFragment homeFeedFragment=new HomeFeedFragment();
        homeFeedFragment.hasActionBar=hasActionBar;
        homeFeedFragment.isFeedDetail=false;
        return homeFeedFragment;
    }

    public static HomeFeedFragment newInstance(String memberId, String memberName, boolean isPullable) {
        HomeFeedFragment newsFeedFragment = new HomeFeedFragment();
        newsFeedFragment.memberName = memberName;
        newsFeedFragment.memberId = memberId;
        newsFeedFragment.isPullable = isPullable;
        return newsFeedFragment;
    }

    public void setFeedType(ApiFeed.FeedType feedType) {
        this.pageType = feedType.getValue();
    }

    /**
     * Delegates for recycler view to stick to the top
     */
    private RecyclerViewDelegate recyclerViewDelegate = new RecyclerViewDelegate();

    @Override
    public boolean isViewBeingDragged(MotionEvent event) {
        return recyclerViewDelegate.isViewBeingDragged(event, newfeedsrecycle);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    /**
     * Method to reload the feeds
     */
    public void reloadFeed() {
        is_Swipe = true;
        loadmore = true;
        previousTotal = 0;
        loading = true;
        visibleThreshold = 1;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        pageNo = 1;
        GetMyNewsFeeds(pageNo, is_Swipe);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        convertView = inflater.inflate(R.layout.layout_feed, null);
       InitUIControls();
        memberId = memberId != null && !memberId.isEmpty() ? memberId : App.preference().getUserId();
        memberName = memberName != null && !memberName.isEmpty() ? memberName : App.preference().getUserName();
        Log.e("memberID", "memberID" + memberId);

        pref = App.preference();
        mHandler = new Handler();
        pullRefreshLayout.setPullable(isPullable);
        reloadFeed();

        icMore = convertView.findViewById(R.id.ic_more);
        icSearch = convertView.findViewById(R.id.icSearch);
        layoutActionbar=convertView.findViewById(R.id.layoutActionbar);
        layoutActionbar.setVisibility(hasActionBar||isFeedDetail?View.VISIBLE:View.GONE);
        icNavigation = convertView.findViewById(R.id.icNavigation);
        icNavigation.setImageResource(R.drawable.ic_back);
        icNavigation.setVisibility(isFeedDetail?View.VISIBLE:View.GONE);
        if (isFeedDetail)icSearch.setVisibility(View.GONE);
        icNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        icSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HomeActivity.addFragment(SearchProfileFragment.newInstance(true,true),getActivity().getSupportFragmentManager());
                try{
                    ((HomeActivity)getActivity()).views.bottomTabs.select(BottomTabs.Tab.friends,false);
                }catch (Exception e){

                }
            }
        });

        /**
         * Load more feeds logical part
         */
        newfeedsrecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            FeedHolder currentFocusedLayout = null;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    final int positionView = ((LinearLayoutManager) newfeedsrecycle.getLayoutManager()).findFirstVisibleItemPosition();
                    if (positionView < 0 || positionView >= myNewsFeedsArrayList.size())
                        return;
                    final FeedBean feed = myNewsFeedsArrayList.get(positionView);
                    if (feed.post_type.equals("video")) {
                        currentFocusedLayout = new FeedHolder((newfeedsrecycle.getLayoutManager()).findViewByPosition(positionView));
                        feedAdapter.setCurrentVideoViewObj(currentFocusedLayout.videoView);
                        int visiblePercent = App.getVisiblePercent(currentFocusedLayout.videoView);
                        if (visiblePercent > 30) {
                            currentFocusedLayout.playVideo();
                        }
                    }
//                    Log.e(TAG, "Feed SCROLL_STATE_IDLE position " + positionView + "  " + msg);
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager layoutManager = ((LinearLayoutManager) newfeedsrecycle.getLayoutManager());
                try {
                    if (dy > 0) {
                        //scroll up get first visible item
                        int position = layoutManager.findFirstVisibleItemPosition();
                        if (position >= 0 && position < myNewsFeedsArrayList.size()) {
                            final FeedBean feed = myNewsFeedsArrayList.get(position);
                            if (feed.post_type.equals("video")){
                                final FeedHolder holder = new FeedHolder(layoutManager.findViewByPosition(position));
                                feedAdapter.setCurrentVideoViewObj(holder.videoView);
                                int curVisiblePercent = App.getVisiblePercent(holder.videoView);
                                if (curVisiblePercent > 30 && !holder.videoView.isPlaying()) {
                                    holder.videoView.start();
                                    if (!holder.videoView.isPlaying()) {
                                        holder.videoView.start();
//                                        Log.e(TAG, "video started");
                                    } else {
//                                        Log.e(TAG, "video already started");
                                    }
                                } else if (curVisiblePercent <= 30 && holder.videoView.isPlaying()) {
                                    holder.videoView.pause();
//                                    Log.e(TAG, "video paused");
                                }
                            }
                        }

                    } else {
                        //scroll down get last visible item
                        int position = layoutManager.findLastVisibleItemPosition();
                        final FeedBean feed = myNewsFeedsArrayList.get(position);
                        if (feed.post_type.equals("video")){
                            final FeedHolder holder = new FeedHolder(layoutManager.findViewByPosition(position));
                            int curVisiblePercent = App.getVisiblePercent(holder.videoView);
                            if (curVisiblePercent < 30 && holder.videoView.isPlaying()) {
                                holder.videoView.pause();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    /**
     * API call to get the feeds
     * @param pageNo
     * @param is_Swipe boolean that determines whether this method is called when refreshing feeds
     */
    public void GetMyNewsFeeds(final int pageNo, final boolean is_Swipe) {
        final ApiFeed apiFeed = new ApiFeed(pageNo, memberId, String.valueOf(pageType), feedId);
        noData.setVisibility(View.GONE);
        if (NetworkReceiver.isOnline(getContext())) {
            if (pageNo == 1) {
                if (!is_Swipe) {
                    progress_lay.setVisibility(View.VISIBLE);
                    progress_wheel_bottom.setVisibility(View.VISIBLE);
                }
            }
            feedId=feedId!=null?feedId:"";
            String feedType=feedId!=null&&!feedId.isEmpty()?"2":"1";
            if(pageType==3){
                feedType="3";
            }

            String url = Config.ApiUrls.FEEDS_LIST+"?user_id="+App.preference().getUserId()+"&access_token="+
                    App.preference().getAccessToken()+"&page_no"+"="+pageNo+"&feed_type="+feedType+"&feed_id="+feedId+"&member_id="+memberId;
            Log.e(TAG, "apiCallHomePage : " + url);

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(TAG + " apiCallMyInfoNewsFeed Response: " + response);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ArrayList<FeedBean> newsFeedsArrayLists = GotoParseResponse(jsonObject);
                                    if (pageNo == 1) {
                                        myNewsFeedsArrayList.clear();
                                        share_url = jsonObject.has("share_url") ? jsonObject.getString("share_url") : "";
                                    }
//                                    Log.e(TAG, "Share_urlShare_urlShare_url: " + share_url);
                                    if (newsFeedsArrayLists.size() == 0) {
                                        loadmore = false;
                                    }
                                    if (is_Swipe) {

                                        pullRefreshLayout.setRefreshing(false);
                                    }

                                    for (int i = 0; i < newsFeedsArrayLists.size(); i++) {
                                        myNewsFeedsArrayList.add(newsFeedsArrayLists.get(i));
                                    }

                                    if (pageNo == 1) {
                                        progress_lay.setVisibility(View.GONE);
                                        progress_wheel_bottom.setVisibility(View.GONE);
                                    }

                                    feedAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (refreshListener != null) refreshListener.onRefreshed();
                            noData.setVisibility(myNewsFeedsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "apiCallHomePage Connection timeout");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            noData.setVisibility(myNewsFeedsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                            if (refreshListener != null) refreshListener.onRefreshed();
                        }
                    });
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return apiFeed.asPostParam();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return apiFeed.getDefaultHeaders();
                }
            };

            request.setShouldCache(false);
            request.setRetryPolicy(new RetryPolicy() {

                @Override
                public int getCurrentTimeout() {
                    return 30000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 0;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    Log.e(TAG, "apiCallHomePageApiRetry: " + error.toString());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            noData.setVisibility(myNewsFeedsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                            if (refreshListener != null) refreshListener.onRefreshed();
                        }
                    });
                }
            });
            App.instance().addToRequestQueue(request, TAG);


        } else {
            Log.e(TAG, "internet connection not found");
        }
    }

    /**
     * Method to parse the json response
     * @param newsFeedObj
     * @return store the parsed response in arraylist
     */

    public ArrayList<FeedBean> GotoParseResponse(JSONObject newsFeedObj) {
        ArrayList<FeedBean> tempNewsFeed = new ArrayList<>();
        try {
            String status = newsFeedObj.has("status") ? newsFeedObj.getString("status") : "false";

            if (status.equals("true")) {
                JSONArray newsFeedResultArray = newsFeedObj.getJSONArray("result");

                for( int i=0 ;i<newsFeedResultArray.length();i++){
                    FeedBean feedBean= new FeedBean();
                    JSONObject resObj= newsFeedResultArray.getJSONObject(i);
                    try {
                        feedBean.post_id=resObj.getString("post_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    feedBean.user_id=resObj.getString("user_id");
                    feedBean.post_text=resObj.getString("post_text");
                    try{
                        feedBean.post_text= StringEscapeUtils.unescapeJava(feedBean.post_text);
                    }catch (Exception e){

                    }
                    feedBean.post_type=resObj.getString("post_type");
                    feedBean.post_like_count=resObj.getString("post_like_count");
                    feedBean.post_comment_count=resObj.getString("post_comment_count");
                    feedBean.user_name=resObj.getString("user_name");
                    feedBean.user_first_name=resObj.getString("user_first_name");
                    feedBean.user_last_name =resObj.getString("user_last_name");
                    feedBean.user_image=resObj.getString("user_image");
                    feedBean.created_at=resObj.getString("created_at");
                    feedBean.author_pic=resObj.getString("author_pic");
                    feedBean.is_like=resObj.getString("is_like");
                    feedBean.created_at_ago=resObj.getString("created_at_ago");
                    feedBean.setFeedUrl(resObj.has("share_url")?resObj.getString("share_url"):"no-url-found");
                    try {
                        JSONArray mediaArray =resObj.getJSONArray("media");

                        for(int j=0;j<mediaArray.length();j++){
                            MediaDetail mediaDetail= new MediaDetail();
                            JSONObject mediaObj =mediaArray.getJSONObject(j);
                            mediaDetail.media_id=mediaObj.getString("media_id");
                            mediaDetail.media_name=mediaObj.getString("media_name");
                            mediaDetail.media_image=mediaObj.getString("media_image");
                            mediaDetail.media_size=mediaObj.getString("media_size");
                            mediaDetail.media_mime_type=mediaObj.getString("media_mime_type");
                            mediaDetail.media_extension=mediaObj.getString("media_extension");
                            mediaDetail.media_type=mediaObj.getString("media_type");
                            mediaDetail.setMediaWidth(mediaObj.has("media_width")?mediaObj.getString("media_width"):"");
                            mediaDetail.setMediaHeight(mediaObj.has("media_height")?mediaObj.getString("media_height"):"");

                            feedBean.mediaDetails.add(mediaDetail);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tempNewsFeed.add(feedBean);


                }
                feedAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempNewsFeed;
    }


    /**
     * UI views initialization
     */
    public void InitUIControls() {

        if (myNewsFeedsArrayList == null) {
            myNewsFeedsArrayList = new ArrayList<>();
        }
        noData = (TextView) convertView.findViewById(R.id.noData);
        noData.setVisibility(View.GONE);
        progress_wheel_bottom = (ProgressWheel) convertView.findViewById(R.id.progress_wheel_bottom);
        progress_lay = (RelativeLayout) convertView.findViewById(R.id.progress_lay);


        newfeedsrecycle = (RecyclerView) convertView.findViewById(R.id.newfeedsrecycle);
        feedAdapter = new FeedAdapter(HomeFeedFragment.this,myNewsFeedsArrayList,  getActivity(), progress_lay, memberId);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        newfeedsrecycle.setLayoutManager(linearLayoutManager);
        newfeedsrecycle.setHasFixedSize(true);

        newfeedsrecycle.setAdapter(feedAdapter);

        pullRefreshLayout = (PullToRefreshLayout) convertView.findViewById(R.id.swipe_newsFeeds);
        pullRefreshLayout.setPullable(isPullable);

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadFeed();
            }
        });

        newfeedsrecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = newfeedsrecycle.getChildCount();
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
                    is_Swipe = false;
                    if (pageType != ApiFeed.FeedType.custom.getValue()) {
                        pageNo = pageNo + 1;
                        GetMyNewsFeeds(pageNo, is_Swipe);
                        loading = true;
                    }
                }
            }
        });


    }

    /**
     *method action that occurs when feed is removed from the adapter
     */
    public void onDeleteFeed(){
        if (getParentFragment()!=null && getParentFragment() instanceof HomeProfileFragment){
            ((HomeProfileFragment)getParentFragment()).onFeedDeleted();
        }
    }


}
