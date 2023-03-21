/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;


import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.regex.Pattern;

public class Config {


    //if need ad support then set this as true
    public static final boolean AD_SUPPORTED = false;
    /**
     * if build is debug build then this wil show only testing ads
     * if build is production build then real ads will show
     */

    public static final boolean AUTO_PRIVATE_USER = false;
    public static final String ARG_PUSH = "notificationData";

    public static final class ApiUrls {


        /**
         * replace your api url here
         **/
        //live url
        public static String API_BASE = "http://bsedemo.com/instasocial/api/";

        /**
         * Webservice names
         */
        public static String PROFILE_URL = API_BASE + "profile/avatar/";
        public static String POLICY_URL = API_BASE + "pages/privacy.html";
        public static String TERMS_URL = API_BASE + "pages/privacy.html";
        public static String FILE_UPLOAD = API_BASE + "fileupload";
        public static String REGISTER = API_BASE + "user/register";
        public static String FORGOT_PWD = API_BASE + "user/forgotpassword";
        public static String LOGIN = API_BASE + "user/login";
        public static String CHANGE_PASSWORD = API_BASE + "user/change_password";
        public static String USER_SETTINGS_PAGE = API_BASE + "user/settings";
        public static String UPDATE_PROFILE = API_BASE + "user/profile";
        public static String GET_PROFILE = API_BASE + "user/profileinfo";
        public static String FACEBOOK_LOGIN = API_BASE + "user/oauth";
        public static String ADD_POST = API_BASE + "post";
        public static String MULTIPLEFILEUPLOAD = API_BASE + "feed_fileuploads";
        public static String NOTIFICATIONURL = API_BASE + "user/notifications";
        public static String FOLLOWINGSLIST = API_BASE + "user/following_list";
        public static String FOLLOWERLIST = API_BASE + "user/followers_list";
        public static String FEEDS_LIST = API_BASE + "post/feeds";
        public static String FEED_ACTIONS = API_BASE + "post/actionbt";
        public static String COMMENTLIST = API_BASE + "post/comments";
        public static String EDIT_FEED = API_BASE + "post/edit-feed";
        public static String MEDIALISTINGS = API_BASE + "post/media-listing";
        public static String FOLLOW_UNFOLLOW_BLOCK = API_BASE + "user/follow_unfollow_block";
        public static String SEARCH_PROFILE_LIST = API_BASE + "user/user_list";
        public static String FOLLOW_REQUEST_LIST = API_BASE + "user/requestlist";
        public static String FOLLOW_REQUEST_ACCEPT = API_BASE + "user/response_status";
        public static String LIKE_LIST = API_BASE + "post/likes";
        public static String BLOCK_USER_LIST = API_BASE + "user/block_list";
        public static String NOTIFICATION_SEEN_STATUS_UPDATE = API_BASE + "user/notifications_update";
        public static String FEED_REPORT_TYPE_LIST = API_BASE + "report-types";
        public static String FEED_REPORT_STATUS = API_BASE + "post/report";

    }


    public static boolean checkEmail(String email) {
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    /**
     * Display text common method
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 20, 50);
        toast.show();
    }

    /**
     * retrieve string from xml
     *
     * @param context
     * @param id
     * @return
     */
    public static String getStringRes(Context context, int id) {
        return context.getResources().getString(id);
    }

    /**
     * Image loader function
     *
     * @param lowRes_image
     * @param highRes_image
     * @param imageView
     */
    public static void imageLoader(String lowRes_image, String highRes_image, SimpleDraweeView imageView) {
        if (lowRes_image != null && highRes_image != null) {
            Uri lowResUri = Uri.parse(lowRes_image);
            Uri highResUri = Uri.parse(highRes_image);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setLowResImageRequest(ImageRequest.fromUri(lowResUri))
                    .setImageRequest(ImageRequest.fromUri(highResUri))
                    .setOldController(imageView.getController())
                    .build();
            imageView.setController(controller);

        }
    }
}
