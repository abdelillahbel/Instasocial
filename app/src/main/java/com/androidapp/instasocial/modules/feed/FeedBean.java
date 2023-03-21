/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.feed;

import com.androidapp.instasocial.utils.AspectRatio;

import java.util.ArrayList;

public class FeedBean {
    public String post_id = "";
    public String user_id = "";
    public String post_text = "";
    public String post_type = "";
    public String post_like_count = "";
    public String post_comment_count = "";
    public String user_name = "";
    public String user_first_name = "";
    public String user_last_name = "";
    public String user_image = "";
    public String created_at = "";
    public String author_pic = "";
    public String is_like = "";
    public String created_at_ago = "";
    public ArrayList<MediaDetail> mediaDetails = new ArrayList<>();
    private String feedUrl;

    public boolean isPhotoFeed() {
        return post_type != null && post_type.equals("photo");
    }

    public boolean isVideoFeed() {
        return (post_type != null && post_type.equals("video")) || (post_type != null && post_type.equals(""));
    }

    public MediaDetail getMedia() {
        try {
            return mediaDetails.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AspectRatio getMediaAspectRatio() {
        try {
            return mediaDetails.get(0).getMediaAspectRatio();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFeedUrl() {
        return feedUrl != null && !feedUrl.isEmpty() ? feedUrl : "no-url-found";
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public int getLikesInt() {
        int val = 0;
        try {
            val = Integer.parseInt(post_like_count);
        } catch (Exception e) {

        }
        return val;
    }

    public void incrementLikes() {
        int val = 0;
        try {
            val = Integer.parseInt(post_like_count);
            val = val + 1;
            post_like_count = String.valueOf(val);
        } catch (Exception e) {
        }
    }

    public String formattedLikesCount() {
        Integer number = getLikesInt();
        String[] suffix = new String[]{"k", "m", "b", "t"};
        int size = (number.intValue() != 0) ? (int) Math.log10(number) : 0;
        if (size >= 3) {
            while (size % 3 != 0) {
                size = size - 1;
            }
        }
        double notation = Math.pow(10, size);
        String result = (size >= 3) ? +(Math.round((number / notation) * 100) / 100.0d) + suffix[(size / 3) - 1] : +number + "";
        return result;
    }


}

