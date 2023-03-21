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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.modules.feed.FeedBean;
import com.androidapp.instasocial.modules.feed.MediaDetail;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.SeamLessViewPager.delegate.AbsListViewDelegate;
import com.androidapp.instasocial.ui.SeamLessViewPager.fragment.BaseViewPagerFragment;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.poliveira.apps.parallaxlistview.ParallaxGridView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Media posts grid listing in profile page
 */
public class ProfilePostGridFragment extends BaseViewPagerFragment {
    private String TAG = "ProfilePostGridFragment";

    @Override
    public void onAttach(Context context) {
        try {
            refreshListener = (RefreshListener) getParentFragment();
        }catch (Exception e){
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        refreshListener=null;
        super.onDetach();
    }


    /**
     * Fragment instance creation
     * @param memberId
     * @param memberName
     * @param isPullable
     * @param index
     * @return
     */
    public static ProfilePostGridFragment newInstance(String memberId, String memberName, boolean isPullable, int index) {
        ProfilePostGridFragment newsFeedFragment = new ProfilePostGridFragment();
        newsFeedFragment.memberName = memberName;
        newsFeedFragment.memberId = memberId;
        newsFeedFragment.isPullable = isPullable;
        newsFeedFragment.mFragmentIndex = index;
        return newsFeedFragment;
    }


    /**
     * Member variables declarations/initializations
     */
    protected RefreshListener refreshListener = null;
    private AbsListViewDelegate mAbsListViewDelegate = new AbsListViewDelegate();
    protected String memberName;
    protected String memberId;
    protected HomeProfileFragment profileFragment;
    int pageNo = 1;
    Handler handler;
    Views views;

    boolean loadmore = true;
    boolean is_Swipe = false;
    protected boolean isPullable = true;
    public ArrayList<FeedBean> myNewsFeedsArrayList=new ArrayList<>();

    @Override
    public boolean isViewBeingDragged(MotionEvent event) {
        return mAbsListViewDelegate.isViewBeingDragged(event, views.gridView);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        views = new Views(inflater.inflate(R.layout.layout_profile_media_fragment, null));
        profileFragment = (HomeProfileFragment) getParentFragment();
        handler = new Handler();
        reloadFeed();
        return views.root;
    }

    /**
     * Refresh list
     */
    public void reloadFeed() {
        is_Swipe = true;
        loadmore = true;
        myNewsFeedsArrayList.clear();
        pageNo = 1;
        GetMyNewsFeeds(pageNo,false);

    }

    /**
     * Views declaration
     */
    public class Views {
        final View root;
        final TextView txtNoData;
        final ParallaxGridView gridView;
        final View layoutProgress;

        final PostedFeedsAdapter adapter;

        public Views(View root) {
            this.root = root;
            txtNoData = root.findViewById(R.id.txtNoData);
            txtNoData.setVisibility(View.GONE);
            gridView = root.findViewById(R.id.parallaxGridView);
            layoutProgress = root.findViewById(R.id.layoutProgress);
            adapter = new PostedFeedsAdapter(myNewsFeedsArrayList);
            gridView.setAdapter(adapter);
            gridView.setOnScrollListener(scrollListener);

            setLoadMore(false);
        }

        public void setLoadMore(boolean isVisible) {
            if (layoutProgress == null) return;
            layoutProgress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }


    /**
     * Posts listing api call
     * @param pageNo
     * @param is_Swipe
     */
    public void GetMyNewsFeeds(final int pageNo, final boolean is_Swipe) {
        views.txtNoData.setVisibility(View.GONE);
        if (NetworkReceiver.isOnline(getContext())) {
            if (pageNo == 1) {
            }
            String url = Config.ApiUrls.FEEDS_LIST+"?user_id="+App.preference().getUserId()+"&access_token="+
                    App.preference().getAccessToken()+"&page_no"+"="+pageNo+"&feed_type=3"+"&feed_id="+"&member_id="+memberId;

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (refreshListener != null) refreshListener.onRefreshed();
                            if (views != null)
                                views.setLoadMore(false);

                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ArrayList<FeedBean> newsFeedsArrayLists = GotoParseResponse(jsonObject);
                                    if (pageNo == 1) {
                                        myNewsFeedsArrayList.clear();
                                    }
                                    if (newsFeedsArrayLists.size() == 0) {
                                        loadmore = false;
                                    }


                                    for (int i = 0; i < newsFeedsArrayLists.size(); i++) {
                                        myNewsFeedsArrayList.add(newsFeedsArrayLists.get(i));
                                    }

                                    views.adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (refreshListener != null) refreshListener.onRefreshed();
                            views.txtNoData.setVisibility(myNewsFeedsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "apiCallHomePage Connection timeout");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            views.txtNoData.setVisibility(myNewsFeedsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                            if (refreshListener != null) refreshListener.onRefreshed();
                        }
                    });
                }
            });

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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            views.txtNoData.setVisibility(myNewsFeedsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
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
     * Parse response logic
     * @param newsFeedObj
     * @return
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempNewsFeed;
    }


    /**
     * Adapter to load the feed data in list
     */
    private class PostedFeedsAdapter extends BaseAdapter {
        ArrayList<FeedBean> data;
        PostedFeedsAdapter.ViewHolder holder;

        public PostedFeedsAdapter(ArrayList<FeedBean> postList) {
            this.data = postList;
        }

        @Override
        public int getCount() {
            if (views != null && views.txtNoData != null)
                views.txtNoData.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            ImageView icPlay;
            ImageView image;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final FeedBean feed = data.get(position);
            if (convertView == null) {
                holder = new PostedFeedsAdapter.ViewHolder();
                int layoutId = R.layout.grid_view_repeat_item;
                LayoutInflater li = getActivity().getLayoutInflater();
                convertView = li.inflate(layoutId, null);
                holder.icPlay = (ImageView) convertView.findViewById(R.id.photo_view_video_play_icon);
                holder.image = (ImageView) convertView.findViewById(R.id.grid_view_image);

                convertView.setTag(holder);
            } else {
                holder = (PostedFeedsAdapter.ViewHolder) convertView.getTag();
            }

            String imageUrl=feed.getMedia()!=null ? feed.isPhotoFeed()? feed.getMedia().media_name:feed.getMedia().media_image :"url-not-found";

            holder.icPlay.setVisibility(feed.isVideoFeed() ? View.VISIBLE : View.GONE);
            Picasso.with(getContext()).load(imageUrl)
                    .placeholder(R.color.colorPrimaryLightDark)
                    .error(R.color.colorPrimaryLightDark)
                    .fit()
                    .centerCrop()
                    .into(holder.image);

            View.OnLayoutChangeListener layoutChangeListener=new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    int width=i2-i;
                    holder.image.getLayoutParams().height=width;
                    holder.image.removeOnLayoutChangeListener(this);
                }
            };
            holder.image.addOnLayoutChangeListener(layoutChangeListener);

            /**
             * Feed detail redirection
             */
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(feed.post_id,memberId), true);
                    } else if (getActivity() instanceof ProfileActivity) {
                        ((ProfileActivity) getActivity()).replaceFragment(FeedDetailFragment.newInstance(feed.post_id,memberId), true);
                    }
                }
            });
            return convertView;
        }
    }


    // -------------------------------------load more for newsFeed list------------------------------------------//
    protected int default_PreviousTotalCount = 0;
    public AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (totalItemCount == 0 || views.adapter == null) {
                return;
            }
            if (default_PreviousTotalCount == totalItemCount) {
                return;
            }

            final int lastItem = firstVisibleItem + visibleItemCount;
            if ((lastItem == totalItemCount)) {
                default_PreviousTotalCount = totalItemCount;
                pageNo = pageNo + 1;

                views.setLoadMore(true);
                GetMyNewsFeeds(pageNo,false);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };

    // -------------------------------------load more for newsFeed list------------------------------------------//


}
