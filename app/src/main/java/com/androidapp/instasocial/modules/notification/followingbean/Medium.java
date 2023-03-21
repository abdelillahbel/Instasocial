
/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.notification.followingbean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Medium {

    @SerializedName("media_id")
    @Expose
    private Integer mediaId;
    @SerializedName("media_name")
    @Expose
    private String mediaName;
    @SerializedName("media_image")
    @Expose
    private String mediaImage;
    @SerializedName("media_size")
    @Expose
    private String mediaSize;
    @SerializedName("media_dimension")
    @Expose
    private String mediaDimension;
    @SerializedName("media_width")
    @Expose
    private String mediaWidth;
    @SerializedName("media_height")
    @Expose
    private String mediaHeight;
    @SerializedName("media_mime_type")
    @Expose
    private String mediaMimeType;
    @SerializedName("media_extension")
    @Expose
    private String mediaExtension;
    @SerializedName("media_type")
    @Expose
    private String mediaType;

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaImage() {
        return mediaImage;
    }

    public void setMediaImage(String mediaImage) {
        this.mediaImage = mediaImage;
    }

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }

    public String getMediaDimension() {
        return mediaDimension;
    }

    public void setMediaDimension(String mediaDimension) {
        this.mediaDimension = mediaDimension;
    }

    public String getMediaWidth() {
        return mediaWidth;
    }

    public void setMediaWidth(String mediaWidth) {
        this.mediaWidth = mediaWidth;
    }

    public String getMediaHeight() {
        return mediaHeight;
    }

    public void setMediaHeight(String mediaHeight) {
        this.mediaHeight = mediaHeight;
    }

    public String getMediaMimeType() {
        return mediaMimeType;
    }

    public void setMediaMimeType(String mediaMimeType) {
        this.mediaMimeType = mediaMimeType;
    }

    public String getMediaExtension() {
        return mediaExtension;
    }

    public void setMediaExtension(String mediaExtension) {
        this.mediaExtension = mediaExtension;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

}
