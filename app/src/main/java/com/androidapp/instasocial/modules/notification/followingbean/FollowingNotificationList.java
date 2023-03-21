
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

import java.util.List;

public class FollowingNotificationList {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("status_msg")
    @Expose
    private String statusMsg;
    @SerializedName("you_feeds")
    @Expose
    private List<Object> youFeeds = null;
    @SerializedName("follow_feeds")
    @Expose
    private List<FollowFeed> followFeeds = null;
    @SerializedName("unread_count")
    @Expose
    private Integer unreadCount;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public List<Object> getYouFeeds() {
        return youFeeds;
    }

    public void setYouFeeds(List<Object> youFeeds) {
        this.youFeeds = youFeeds;
    }

    public List<FollowFeed> getFollowFeeds() {
        return followFeeds;
    }

    public void setFollowFeeds(List<FollowFeed> followFeeds) {
        this.followFeeds = followFeeds;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

}
