/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.states;

public enum NotificationType {
    unknown,
    follow,  // used for public profiles. receive notification if some one started following
    request_sent,
    request_accept,
    post,
    like,
    comment,
    video_post;

    public static NotificationType parse(String name) {
        if (name == null || (name != null && name.isEmpty())) return unknown;

        if (name.equalsIgnoreCase(follow.name())) {
            return follow;
        } else if (name.equalsIgnoreCase(request_sent.name())) {
            return request_sent;
        } else if (name.equalsIgnoreCase(request_accept.name())) {
            return request_accept;
        } else if (name.equalsIgnoreCase(post.name())) {
            return post;
        } else if (name.equalsIgnoreCase(like.name())) {
            return like;
        } else if (name.equalsIgnoreCase(comment.name())){
            return comment;
        } else {
            return comment;
        }
    }
}
