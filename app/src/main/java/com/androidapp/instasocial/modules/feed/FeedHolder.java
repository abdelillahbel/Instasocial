/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.feed;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.ui.TextureVideoView;
import com.androidapp.instasocial.utils.AspectRatio;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;

/**
 * View holder class that contains UI logics for feed item
 */
public class FeedHolder extends RecyclerView.ViewHolder
        implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "FeedHolder";
//    private static final AspectRatio mediaRatio = new AspectRatio(16, 9);

    final LinearLayout layoutRoot;
    final RelativeLayout layoutMedia;
    final LinearLayout commentLayout;
    final LinearLayout friendLayout, layoutMore;
    final SimpleDraweeView ownerFriend, friendImage;
    final ProgressWheel feedLoader;
    final ImageView image_feed;

    final View layoutLikeCount, layoutCommentCount;
    final ImageView imgShare;
    public final TextView more_icon;
    public final TextView like_icon;
    public final TextView  likecount, commentcount, sharecount;
    public final ReadMoreTextView title_text;
    public final TextView icVideoCam;

    public final TextView txtUserName, txtPostedTime;
    public final CircleImageView imgUserPic;
    public final TextureVideoView videoView;
    public final ImageView comment_icon;

    public final RelativeLayout layoutVolume;
    public final ImageView imgVolume;

    MediaPlayer mediaPlayer;
    LinearLayout userLay;

    public final View mediaBase;

    boolean sound;

    //    public final TextView txtNoImage;
    public void enableLikeViews(boolean enable) {
        like_icon.setEnabled(enable);
        layoutLikeCount.setEnabled(enable);
    }

    //UI initializations
    public FeedHolder(View view) {
        super(view);
        layoutRoot = view.findViewById(R.id.layoutRoot);
        ownerFriend = view.findViewById(R.id.ownerFriend);
        friendImage = view.findViewById(R.id.friendImage);
        friendLayout = view.findViewById(R.id.friendLayout);
        imgShare=view.findViewById(R.id.share_icon);
        mediaBase = view.findViewById(R.id.layoutMediaBase);
        layoutLikeCount = view.findViewById(R.id.layoutLikeCount);
        //temp hide
        layoutLikeCount.setVisibility(View.VISIBLE);
        layoutCommentCount = view.findViewById(R.id.layoutCommentCount);
        videoView = view.findViewById(R.id.videoView);
        layoutVolume = view.findViewById(R.id.layoutVolume);
        imgVolume = view.findViewById(R.id.imgVolume);

        if (videoView != null) {
            videoView.setOnPreparedListener(this);
            videoView.setOnCompletionListener(this);
            videoView.setOnErrorListener(this);
            videoView.setOnInfoListener(this);
        }
        icVideoCam = (TextView) view.findViewById(R.id.single_image_play_icon);
        image_feed =  view.findViewById(R.id.image_feed);
        likecount = (TextView) view.findViewById(R.id.likecount);
        commentcount = (TextView) view.findViewById(R.id.commentcount);
        sharecount = (TextView) view.findViewById(R.id.sharecount);
        layoutMedia = (RelativeLayout) view.findViewById(R.id.layoutMedia);
        feedLoader = (ProgressWheel) view.findViewById(R.id.feedLoader);
        like_icon = (TextView) view.findViewById(R.id.like_icon);

        comment_icon = view.findViewById(R.id.comment_icon);
        more_icon = (TextView) view.findViewById(R.id.more_icon);
        title_text =  view.findViewById(R.id.title_text);
        commentLayout = (LinearLayout) view.findViewById(R.id.commentLayout);
        layoutMore = (LinearLayout) view.findViewById(R.id.layoutMore);

        txtUserName = view.findViewById(R.id.txtUserName);
        txtPostedTime = view.findViewById(R.id.txtPostedTime);
        imgUserPic = view.findViewById(R.id.imgUserPic);


        layoutVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (sound == false) {
                        if (mediaPlayer != null && videoView.isPlaying()) {
                            imgVolume.setImageResource(R.drawable.un_mute_grey);
                            sound = true;
                            mediaPlayer.setVolume(1, 1);
                        }
                    } else {
                        if (mediaPlayer != null && videoView.isPlaying()) {
                            imgVolume.setImageResource(R.drawable.mute_gray);
                            sound = false;
                            mediaPlayer.setVolume(0, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //resize videoview based on the preview image
    public void resizeVideoView() {
        if (videoView == null || image_feed == null) return;
        videoView.getLayoutParams().width = image_feed.getLayoutParams().width;
        videoView.getLayoutParams().height = image_feed.getLayoutParams().height;
    }

    public void init(FeedBean feed) {
        setVisibilityVolume(false);
        setVisibilityPostImage(false);
        setVisibilityIcVideoCam(false);
        setVisibilityVideo(false);
        setLoaderVisible(true);

        final MediaDetail media = feed.getMedia();
        if (media != null) {
            AspectRatio ratio = media.getMediaAspectRatio();
            if (ratio != null) {
                int width = App.getScreenWidth();
                int height = ratio.getHeightBy(width);
                mediaBase.getLayoutParams().width = width;
                mediaBase.getLayoutParams().height = height;
                image_feed.getLayoutParams().width = width;
                image_feed.getLayoutParams().height = height;
                videoView.getLayoutParams().width = width;
                videoView.getLayoutParams().height = height;
            }
        }else{
            RelativeLayout.LayoutParams mediaBaseParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams imgParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams videoParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mediaBase.setLayoutParams(mediaBaseParams);
            image_feed.setLayoutParams(imgParams);
            videoView.setLayoutParams(videoParams);
        }
    }

    // views visibility handling -- STARTS HERE
    public void setLoaderVisible(boolean visible) {
        setVisibility(feedLoader, visible);
    }

    public void setVisibilityIcVideoCam(boolean visible) {
        setVisibility(icVideoCam, visible);
    }

    public void setVisibilityVolume(boolean visible) {
        setVisibility(layoutVolume, visible);
    }

    public void setVisibilityVideo(boolean visible) {
        setVisibility(videoView, visible);
    }

    public void setVisibilityPostImage(boolean visible) {
        setVisibility(image_feed, visible);
    }

    public void setVisibilityMediaBase(boolean visible) {
        setVisibility(mediaBase, visible);
    }

    private void setVisibility(View view, boolean visible) {
        if (view == null) return;
        view.setVisibility(visible ? VISIBLE : View.GONE);
    }

    // views visibility handling -- ENDS HERE

    //videoview initialization
    public void initVideoView(String videoPath) {
        if (videoView == null || videoPath == null) {
            Log.e(TAG, "Error in initializing video view: " + videoView + " videoPath:" + videoPath);
            return;
        }
        videoView.setVideoPath(videoPath);
    }

    //set feed image
    public void loadFeedImage(String imageUrl, Callback... callback) {
        if (image_feed == null || imageUrl == null) {
            Log.e(TAG, "Error in initializing feed image: " + image_feed + " videoPath:" + imageUrl);
            return;
        }
        if (callback.length > 0) {
            Picasso.with(txtUserName.getContext()).load(imageUrl).into(image_feed, callback[0]);
        } else {
            Picasso.with(txtUserName.getContext()).load(imageUrl).into(image_feed);
        }
    }

    /**
     * Video playback handling ---  STARTS HERE ----
     */
    public void playVideo() {
        if (videoView == null) {
            Log.e(TAG, "Cannot play video; video view is null");
            return;
        }
        if (videoView.isPrepared())
            videoView.start();
    }

    public void pauseVideo() {
        if (videoView == null) {
            Log.e(TAG, "Cannot pause video; video view is null");
            return;
        }
        if (videoView.isPlaying())
            videoView.pause();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        sound = false;
        this.mediaPlayer.setVolume(0, 0);
        setVisibilityPostImage(true);
        if (App.getVisiblePercent(videoView) > 30) {
            videoView.start();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        this.mediaPlayer = mediaPlayer;
        sound = false;
        this.mediaPlayer.setVolume(0, 0);

        imgVolume.setImageResource(R.drawable.mute_gray);

        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                setLoaderVisible(true);
                break;

            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                setLoaderVisible(false);
                break;

            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                // Here the video starts
                setVisibilityVolume(true);
                setVisibilityPostImage(false);
                setVisibilityIcVideoCam(false);
                setLoaderVisible(false);

                break;
        }
        return true;

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        videoView.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        setVisibilityVolume(false);
        setVisibilityPostImage(true);
        setVisibilityIcVideoCam(true);

        return true;
    }

    /**
     * Video playback handling ---  ENDS HERE ----
     */
}
