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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.SeamLessViewPager.delegate.AbsListViewDelegate;
import com.androidapp.instasocial.ui.SeamLessViewPager.fragment.BaseViewPagerFragment;
import com.poliveira.apps.parallaxlistview.ParallaxGridView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class ProfileMediaFragment extends BaseViewPagerFragment {

    /**
     * Member variables declarations/initializations
     */
    private String TAG = "ProfileMediaFragment";
    protected RefreshListener refreshListener = null;
    private AbsListViewDelegate mAbsListViewDelegate = new AbsListViewDelegate();
    protected String memberName;
    protected String memberId;
    protected ApiProfileMedia.MediaType mediaType = ApiProfileMedia.MediaType.photo;
    protected HomeProfileFragment profileFragment;
    final ArrayList<ProfileMedia> mediaList = new ArrayList<>();
    int pageNo = 1;
    Handler handler;
    Views views;

    @Override
    public boolean isViewBeingDragged(MotionEvent event) {
        return mAbsListViewDelegate.isViewBeingDragged(event, views.gridView);
    }

    public interface ProfileMediaCallback {
        public void onMediaResponse(String apiStatus, ApiProfileMedia.MediaType mediaType, ArrayList<ProfileMedia> media);
    }

    protected ProfileMediaCallback mediaCallback = null;

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
     * refresh feeds
     */
    public void reloadFeed() {
        pageNo = 1;
        apiCallProfileMedia(false);
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
            adapter = new PostedFeedsAdapter(mediaList);
            gridView.setAdapter(adapter);
            gridView.setOnScrollListener(scrollListener);

            setLoadMore(false);
        }

        public void setLoadMore(boolean isVisible) {
            if (layoutProgress == null) return;
            layoutProgress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }




    protected String mediaApiStatus = "";

    /**
     * Profile meadia api call
     * @param isLoadMore
     */

    public void apiCallProfileMedia(final boolean isLoadMore) {

        final ApiProfileMedia api = new ApiProfileMedia(memberId, memberName, mediaType, pageNo);
        Log.e(TAG, "apiCallProfileMedia:  " + api);
        if (!App.isOnline()) {
            App.showToast(R.string.noNet);
            return;
        }
        StringRequest request = new StringRequest(Request.Method.GET, api.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (refreshListener != null) refreshListener.onRefreshed();
                if (views != null)
                    views.setLoadMore(false);
                try {
                    if (pageNo == 1) {
                        mediaList.clear();
                    }


                    Log.e("apiCallProfileMedia", "response: " + response);
                    JSONObject jResponse = new JSONObject(response);
                    mediaApiStatus = jResponse.has("status") ? jResponse.getString("status"): "false";
                    if (mediaApiStatus.equals("true")) {
                        JSONArray result = jResponse.has("result") ? jResponse.getJSONArray("result") : new JSONArray();
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject obj = result.getJSONObject(i);
                            ProfileMedia media = new ProfileMedia();
                            media.setMediaUrl(obj.has("media_name") ? obj.getString("media_name") : "no-media-found");
                            media.setThumbUrl(obj.has("media_image")?obj.getString("media_image"):"no-media-found");
                            media.setWallId(obj.has("post_id") ? obj.getString("post_id") : "");
                            mediaList.add(media);
                        }
                    } else {

                    }
                    if (mediaCallback != null)
                        mediaCallback.onMediaResponse(mediaApiStatus, mediaType, mediaList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                views.adapter.notifyDataSetChanged();

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        views.setLoadMore(false);
                        if (refreshListener != null) refreshListener.onRefreshed();
                        if (mediaCallback != null)
                            mediaCallback.onMediaResponse(mediaApiStatus, mediaType, mediaList);

                        App.showToast("Please try again");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return api.getDefaultHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                return api.asPostParam();
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        views.setLoadMore(false);
                        if (mediaCallback != null)
                            mediaCallback.onMediaResponse(mediaApiStatus, mediaType, mediaList);
                        if (refreshListener != null) refreshListener.onRefreshed();
                    }
                });
            }
        });
        App.instance().addToRequestQueue(request);
    }


    /**
     * Adapter class to load feeds in list
     */
    private class PostedFeedsAdapter extends BaseAdapter {
        ArrayList<ProfileMedia> data;
        ViewHolder holder;

        public PostedFeedsAdapter(ArrayList<ProfileMedia> postList) {
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
            final ProfileMedia feed = data.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                int layoutId = R.layout.grid_view_repeat_item;
                LayoutInflater li = getActivity().getLayoutInflater();
                convertView = li.inflate(layoutId, null);
                holder.icPlay = (ImageView) convertView.findViewById(R.id.photo_view_video_play_icon);
                holder.image = (ImageView) convertView.findViewById(R.id.grid_view_image);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.icPlay.setVisibility(mediaType == ApiProfileMedia.MediaType.video ? View.VISIBLE : View.GONE);
            Picasso.with(getContext()).load(mediaType == ApiProfileMedia.MediaType.video?feed.getThumbUrl():feed.getMediaUrl())
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
                        ((HomeActivity) getActivity()).views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(feed.getWallId(),memberId), true);
                    } else if (getActivity() instanceof ProfileActivity) {
                        ((ProfileActivity) getActivity()).replaceFragment(FeedDetailFragment.newInstance(feed.getWallId(),memberId), true);
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
                apiCallProfileMedia(true);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };

    // -------------------------------------load more for newsFeed list------------------------------------------//

}
