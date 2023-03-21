/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.feeds;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.modules.feed.FeedBean;
import com.androidapp.instasocial.ui.CompatEditText;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.ScaleImageView;
import com.androidapp.instasocial.ui.TextureVideoView;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to edit own posts/feeds
 */


public class EditFeedFragment extends Fragment implements View.OnClickListener,MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{


    /**
     * Member variables declarations/initializations
     */

    FeedBean feedBean;
    MediaPlayer mediaPlayer;
    int position;
    TextureVideoView videoView;
    EditFeedCallBack editFeedCallBack;
    ScaleImageView image_feed;
    CompatTextView txtSubmit;
    CompatEditText edtPostText;

    /**
     * Click event handling
     * @param v
     */
    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.txtSubmit:
             apiCallEditFeed(); //call to edit a feed

             break;
     }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view= LayoutInflater.from(getActivity()).inflate(R.layout._edit_feed_,null);
       initControls(view);
       initListeners();

       return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * UI controls initialization
     * @param view
     */
    private void initControls(View view){
        txtSubmit = view.findViewById(R.id.txtSubmit);
        videoView =view.findViewById(R.id.videoView);
        image_feed = view.findViewById(R.id.image_feed);
        edtPostText =view.findViewById(R.id.edtPostText);
        edtPostText.setText(feedBean.post_text);
        if(feedBean.post_type.equals("video")){
            videoView.setVisibility(View.VISIBLE);

            videoView.setOnPreparedListener(this);
            videoView.setOnCompletionListener(this);
            videoView.setOnErrorListener(this);
            videoView.setOnInfoListener(this);
            image_feed.post(new Runnable() {
                @Override
                public void run() {
                    resizeVideoView();
                }
            });
            initVideoView(feedBean.mediaDetails.get(0).media_name);//setup videoplayer view

        }else {


            loadFeedImage(feedBean.mediaDetails.get(0).media_name, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        }
    }

    /**
     * Register listeners
     */
    private void initListeners(){
        txtSubmit.setOnClickListener(this);
    }

    /**
     * Videoview initializations using path
     * @param videoPath
     */
    public void initVideoView(String videoPath) {
        if (videoView == null || videoPath == null) {
            Log.e("", "Error in initializing video view: " + videoView + " videoPath:" + videoPath);
            return;
        }
        videoView.setVideoPath(videoPath);
    }

    /**
     * Load image for the feed
     * @param imageUrl
     * @param callback
     */
    public void loadFeedImage(String imageUrl, Callback... callback) {
        if (image_feed == null || imageUrl == null) {
            Log.e("", "Error in initializing feed image: " + image_feed + " videoPath:" + imageUrl);
            return;
        }
        if (callback.length > 0) {
            Picasso.with(getActivity()).load(imageUrl).into(image_feed, callback[0]);
        } else {
            Picasso.with(getActivity()).load(imageUrl).into(image_feed);
        }
    }

    /**
     * Fragment instance creation
     * @param feedBean
     * @param position
     * @param editFeedCallBack
     * @return
     */
    public static EditFeedFragment newInstance(FeedBean feedBean,int  position,EditFeedCallBack editFeedCallBack){
        EditFeedFragment editFeedFragment = new EditFeedFragment();
        editFeedFragment.feedBean=feedBean;
        editFeedFragment.editFeedCallBack=editFeedCallBack;
        editFeedFragment.position=position;
        return editFeedFragment;
    }

    /**
     * Mediaplayer logics ---- STARTS HERE -----
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
         videoView.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (App.getVisiblePercent(videoView) > 30) {
            videoView.start();
        }
    }

    /**
     * Mediaplayer logics ---- ENDS HERE -----
     */

    public interface EditFeedCallBack{
        public void editFeedCallback(int position,String postText);
    }

    /**
     * Adjust the size of videovoew based on the preview image
     */
    public void resizeVideoView() {
        if (videoView == null || image_feed == null) return;
        videoView.getLayoutParams().width = image_feed.getLayoutParams().width;
        videoView.getLayoutParams().height = image_feed.getLayoutParams().height;
    }

    /**
     * API call to edit a feed
     */

    public void apiCallEditFeed() {
        if (NetworkReceiver.isOnline(getActivity())) {
            StringRequest request = new StringRequest(Request.Method.POST, Config.ApiUrls.EDIT_FEED,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //customLoader.stopLoading();
                            Log.d("", "apiCallNonRetReq response" + response);
                            if (response != null && !response.equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.has("status") ? jsonObject.getString("status").equalsIgnoreCase("true") : false;
                                    if (status) {

                                        App.showToast("Feed updated successfully!");
                                        editFeedCallBack.editFeedCallback(position,edtPostText.getText().toString());
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e("", "apiCallNonRetReq  api response error; response: " + response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("", "apiCallNonRetReqApiError: " + error.toString());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return new HashMap<String, String>();
                }

                @Override
                protected Map getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("userid",App.preference().getUserId());
                    params.put("access_token",App.preference().getAccessToken());
                    params.put("post_id",feedBean.post_id);
                    params.put("post_text", StringEscapeUtils.escapeJava(edtPostText.getText().toString()));
                    return params;
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
                    return 3;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    Log.e("", "apiCallNonRetReqApiRetryError: " + error.toString());
                }
            });
            App.instance().addToRequestQueue(request, "");
        }
    }


}
