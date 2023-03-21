
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

public class FollowFeed {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("profile_id")
    @Expose
    private Integer profileId;
    @SerializedName("profile_name")
    @Expose
    private String profileName;
    @SerializedName("created_at_ago")
    @Expose
    private String createdAtAgo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getCreatedAtAgo() {
        return createdAtAgo;
    }

    public void setCreatedAtAgo(String createdAtAgo) {
        this.createdAtAgo = createdAtAgo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
