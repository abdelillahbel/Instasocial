/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.post;

import android.util.Log;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.utils.ApiBase;
import com.androidapp.instasocial.utils.Config;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



/**
 * bean class to post feed
 * @return
 */

public class ApiPostFeed extends ApiBase {

    //endpoint name

    private String ENDPOINT = Config.ApiUrls.ADD_POST;
    private String POST_TYPE = "post_type";
    private String POST_TEXT = "post_text";
    private String POST_LINK = "feed_link";
    private String POST_MEDIA = "media_data";
    private String POST_MEDIA_TITLE = "media_title";
    private PostType postType = PostType.text;
    private String USERID="userid";
    private String ACCESSTOKEN="access_token";
    private String postText;
    private String postLink;
    private String postMediaUrl;
    private String title;
    private File mediaFile;
    private String userid;
    private String access_token;

    public static enum PostType {
        text(1), link(2), photo(3), video(4);
        private final int id;

        PostType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }


    public boolean isTextPost() {
        return getPostType().getValue() == PostType.text.getValue();
    }

    public boolean isLinkPost() {
        return getPostType().getValue() == PostType.link.getValue();
    }

    public boolean isPhotoPost() {
        return getPostType().getValue() == PostType.photo.getValue();
    }

    public boolean isVideoPost() {
        return getPostType().getValue() == PostType.video.getValue();
    }

    public boolean isMediaPost() {
        return isPhotoPost() || isVideoPost();
    }

    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
    }

    public void setMediaFile(String mediaFilePath) {
        try {
            this.mediaFile = new File(mediaFilePath);
        } catch (Exception e) {

        }
    }

    public String getPostTypeStr() {
        return getPostType() != null ? String.valueOf(getPostType().getValue()) : "";
    }

    public ApiPostFeed setPostType(PostType postType) {
        this.postType = postType;
        return this;
    }

    public ApiPostFeed setPostLink(String postLink) {
        this.postLink = postLink;
        return this;
    }

    public ApiPostFeed setPostText(String postText) {
        this.postText = postText;
        return this;
    }

    public ApiPostFeed setPostMediaUrl(String postMediaUrl) {
        this.postMediaUrl = postMediaUrl;
        return this;
    }

    public PostType getPostType() {
        return postType;
    }

    public String getPostText() {
        return postText != null ? postText : "";
    }

    public String getPostLink() {
        return postLink != null ? postLink : "";
    }

    public String getPostMediaUrl() {
        return postMediaUrl != null ? postMediaUrl : "";
    }

    @Override
    public String getUrl() {
        return ENDPOINT;
    }

    private ApiPostFeed() {
    }

    public static ApiPostFeed textInstance(String postText) {
        return new ApiPostFeed().setPostType(PostType.text)
                .setPostText(postText);
    }

    public static ApiPostFeed linkInstance(String postLink) {
        return new ApiPostFeed().setPostType(PostType.link)
                .setPostLink(postLink);
    }

    public static ApiPostFeed photoInstance(String postPhotoLink) {
        return new ApiPostFeed().setPostType(PostType.photo)
                .setPostMediaUrl(postPhotoLink);
    }

    public static ApiPostFeed videoInstance(String postVideoLink) {
        return new ApiPostFeed().setPostType(PostType.video)
                .setPostMediaUrl(postVideoLink);
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public ApiPostFeed setTitle(String title) {
        try {
            this.title= StringEscapeUtils.escapeJava(title);
        }catch (Exception e){
            e.printStackTrace();
            this.title = title;
        }
        return this;
    }

    @Override
    public String getBaseUrl() {
        return ENDPOINT;
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map<String,String> asPostParam() {
        Map<String, String> param = new HashMap<>();
        if(postType.getValue()==3)
        param.put(POST_TYPE, "photo");
        else
        param.put(POST_TYPE,"video");
        param.put(POST_TEXT, getTitle());
        param.put(POST_MEDIA, getPostMediaUrl());
        param.put(USERID, App.preference().getUserId());
        param.put(ACCESSTOKEN,App.preference().getAccessToken());
        Log.e("Params for post feed " , param.toString());
        return param;
    }

    @Override
    public String toString() {
        return "{ url: " + getUrl() + "\n"
                + "  headers: " + getDefaultHeaders() + "\n"
                + "  params : " + asPostParam() + "\n" +
                "}";
    }
}
