/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;



/**
 * Bean class for media posts in profile
 */
public class ProfileMedia {
    private String mediaUrl;
    private String wallId;
    private String thumbUrl;
    public ProfileMedia(){

    }
    public ProfileMedia(String mediaUrl, String wallId) {
        this.mediaUrl = mediaUrl;
        this.wallId = wallId;
    }

    public String getMediaUrl() {
        return mediaUrl!=null?mediaUrl:"no-media-url-found";
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getWallId() {
        return wallId!=null?wallId:"";
    }

    public void setWallId(String wallId) {
        this.wallId = wallId;
    }

    public String getThumbUrl() {
        return thumbUrl!=null&&!thumbUrl.isEmpty()?thumbUrl:"no-media-url-found";
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
