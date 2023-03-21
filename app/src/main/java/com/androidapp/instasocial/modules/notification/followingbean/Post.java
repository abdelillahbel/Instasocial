
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

public class Post {

    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("post_text")
    @Expose
    private String postText;
    @SerializedName("post_type")
    @Expose
    private String postType;
    @SerializedName("post_like_count")
    @Expose
    private Integer postLikeCount;
    @SerializedName("post_comment_count")
    @Expose
    private Integer postCommentCount;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_first_name")
    @Expose
    private String userFirstName;
    @SerializedName("user_last_name")
    @Expose
    private String userLastName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("created_at_ago")
    @Expose
    private String createdAtAgo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("author_pic")
    @Expose
    private Integer authorPic;
    @SerializedName("is_like")
    @Expose
    private Integer isLike;
    @SerializedName("share_url")
    @Expose
    private String shareUrl;
    @SerializedName("media")
    @Expose
    private List<Medium> media = null;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public Integer getPostLikeCount() {
        return postLikeCount;
    }

    public void setPostLikeCount(Integer postLikeCount) {
        this.postLikeCount = postLikeCount;
    }

    public Integer getPostCommentCount() {
        return postCommentCount;
    }

    public void setPostCommentCount(Integer postCommentCount) {
        this.postCommentCount = postCommentCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
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

    public Integer getAuthorPic() {
        return authorPic;
    }

    public void setAuthorPic(Integer authorPic) {
        this.authorPic = authorPic;
    }

    public Integer getIsLike() {
        return isLike;
    }

    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia(List<Medium> media) {
        this.media = media;
    }

}
