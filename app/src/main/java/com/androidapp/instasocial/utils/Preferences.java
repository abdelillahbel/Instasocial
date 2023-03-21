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
import android.content.SharedPreferences;


public class Preferences {

    private static SharedPreferences mPreferences;
    private static Preferences instance;
    public static final String NORMALIMG = "normalimg";
    public static final String NORMALVIDEO = "normalvideo";
    private NotificationCountListener notificationCountListener=null;
    private ProfilePicListener profilePicListener=null;

    public void setNotificationCountListener(NotificationCountListener notificationCountListener) {
        this.notificationCountListener = notificationCountListener;
    }

    public static synchronized Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences();
            instance.mPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        }
        return instance;
    }

    private Preferences() {
    }

    public void setNotificationCountStr(String count) {
        try{
            setNotificationCount(Integer.parseInt(count));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setNotificationCount(Integer count) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (count != null) {
            editor.putInt("notificationCount", count);
            if (notificationCountListener!=null)notificationCountListener.onNotificationCountChange(count);
        }
        else editor.remove("notificationCount");
        editor.apply();
    }

    public int getNotificationCount() {
        return mPreferences.getInt("notificationCount", 0);
    }



    public void setSocialLogin(Boolean isSocialLogin) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (isSocialLogin != null) {
            editor.putBoolean("isSocialLogin", isSocialLogin);
        }
        else editor.remove("isSocialLogin");
        editor.apply();
    }

    public boolean isSocialLogin() {
        return mPreferences.getBoolean("isSocialLogin", false);
    }

    //---Login Email address
    private static final String LOGIN_USER_EMAILADDRESS = "email_address";
    public void setEmailAddress(String email_address) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (email_address != null)
            editor.putString(LOGIN_USER_EMAILADDRESS, email_address);
        else editor.remove(LOGIN_USER_EMAILADDRESS);

        editor.apply();
    }

    public String getEmailAddress() {
        return mPreferences.getString(LOGIN_USER_EMAILADDRESS, "");
    }

    //---Login Full name
    private static final String LOGIN_USER_FIRST_NAME = "first_name";
    public void setFirstName(String full_name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (full_name != null)
            editor.putString(LOGIN_USER_FIRST_NAME, full_name);
        else editor.remove(LOGIN_USER_FIRST_NAME);

        editor.apply();
    }

    public String getFirstName() {
        return mPreferences.getString(LOGIN_USER_FIRST_NAME, "");
    }

    public void setGender(String gender) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (gender != null)
            editor.putString("gender", gender);
        else editor.remove("gender");
        editor.apply();
    }

    public String getGender() {
        return mPreferences.getString("gender", "");
    }


    public void setDescription(String description){
        SharedPreferences.Editor editor = mPreferences.edit();
        if (description != null)
            editor.putString("description", description);
        else editor.remove("description");

        editor.apply();
    }

    public String getDescription() {
        return mPreferences.getString("description", "");
    }





    //---Login User Id
    private static final String LOGIN_USER_ID = "";
    public void setUserId(String userId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (userId != null)
            editor.putString(LOGIN_USER_ID, userId);
        else editor.remove(LOGIN_USER_ID);
        editor.apply();
    }


    public String getUserId() {
            return mPreferences.getString(LOGIN_USER_ID, "");
    }

    public int getUserIdInt() {
        int id=0;
        try{
            id= Integer.parseInt(getUserId());
        }catch (Exception e){
        }finally {
            return id;
        }
    }
    //---Login User Id
    private static final String LOGIN_USER_NAME = "userName";
    public void setUserName(String userName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (userName != null)
            editor.putString(LOGIN_USER_NAME, userName);
        else editor.remove(LOGIN_USER_NAME);
        editor.apply();
    }

    public String getUserName() {
        return mPreferences.getString(LOGIN_USER_NAME,"");
    }


    //---Login User Id
    public void setBirthDate(String birthDate) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (birthDate != null&&!birthDate.equalsIgnoreCase("0000-00-00") && !birthDate.equalsIgnoreCase("null")) {
            editor.putString("birthDate", birthDate);
        }
        else editor.remove("birthDate");
        editor.apply();
    }

    public String getBirthDate() {
        String dob=mPreferences.getString("birthDate","");
        return dob.equalsIgnoreCase("null")?dob:"";
    }

    //---Login AccessToken
    private static final String LOGIN_USER_ACCESSTOKEN = "access_token";
    public void setAccessToken(String access_token) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (access_token != null)
            editor.putString(LOGIN_USER_ACCESSTOKEN, access_token);
        else editor.remove(LOGIN_USER_ACCESSTOKEN);
        editor.apply();
    }

    public String getAccessToken() {
        return mPreferences.getString(LOGIN_USER_ACCESSTOKEN, "");
    }



    /**
     * function to clear all shared preferences data
     */
    public void purge(){
        //call all functions with null parameter to remove its data
        setUserId(null);
        setUserName(null);
        setEmailAddress(null);
        setFirstName(null);
        setGender(null);
        setDescription(null);
        setAccessToken(null);
        setProfileImage(null);
        setBirthDate(null);
        setNotificationCount(null);
        setSocialLogin(null);
        setCountryName(null);
    }
    //*** addomg image
    private static final String PROFILE_IMAGE = "profile_image";
    public void setProfileImage(String profile_image) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (profile_image != null) {
            editor.putString(PROFILE_IMAGE, profile_image);
            if (profilePicListener!=null)profilePicListener.onProfilePicUpdated(profile_image);
        }
        else editor.remove(PROFILE_IMAGE);
        editor.apply();
    }

    public void setProfilePicListener(ProfilePicListener profilePicListener) {
        this.profilePicListener = profilePicListener;
    }

    public String getProfileImage() {
        return mPreferences.getString(PROFILE_IMAGE, "");
    }

   //LastName, country,state, isPrivate,isnotificy,follower_count,following_count,unread_count,role

    private static final String LAST_NAME = "last_name";
    public void setLastName(String lastName) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (lastName != null)
            editor.putString(LAST_NAME, lastName);
        else editor.remove(LAST_NAME);
        editor.apply();
    }

    public String getLastName() {
        return mPreferences.getString(LAST_NAME, "");
    }


    private static final String COUNTRY = "country";
    public void setCountry(String country) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (country != null)
            editor.putString(COUNTRY, country);
        else editor.remove(COUNTRY);
        editor.apply();
    }

    public String getCountry() {
        return mPreferences.getString(COUNTRY, "");
    }

    public void setCountryName(String country) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (country != null)
            editor.putString("countryName", country);
        else editor.remove("countryName");
        editor.apply();
    }

    public String getCountryName() {
        return mPreferences.getString("countryName", "");
    }

    public String getStateCountry(){
        boolean hasCountry=!getCountryName().isEmpty() || !getCountry().isEmpty();
        boolean hasState=!getState().isEmpty();
        return  getState()+(hasCountry && hasState?", ":"") + (!getCountryName().isEmpty()?getCountryName():getCountry());
    }

    private static final String STATE = "state";
    public void setState(String state) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (state != null)
            editor.putString(STATE, state);
        else editor.remove(STATE);
        editor.apply();
    }

    public String getState() {
        return mPreferences.getString(STATE, "");
    }

    private static final String IS_PRIVATE = "is_private";
    public void setIsPrivate(String isPrivate) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (isPrivate != null)
            editor.putString(IS_PRIVATE, isPrivate);
        else editor.remove(IS_PRIVATE);
        editor.apply();
    }

    public String getIsPrivate() {
        return mPreferences.getString(IS_PRIVATE, "");
    }


    private static final String IS_NOTIFY = "is_notification";
    public void setIsNotify(String is_notify) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (is_notify != null)
            editor.putString(IS_NOTIFY, is_notify);
        else editor.remove(IS_NOTIFY);
        editor.apply();
    }

    public String getIsNotify() {
        return mPreferences.getString(IS_NOTIFY, "");
    }


    private static final String FOLLOWER_COUNT = "follower_count";
    public void setFollowerCount(String followerCount) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (followerCount != null)
            editor.putString(FOLLOWER_COUNT, followerCount);
        else editor.remove(FOLLOWER_COUNT);
        editor.apply();
    }

    public String getFollowerCount() {
        return mPreferences.getString(FOLLOWER_COUNT, "");
    }

    private static final String FOLLOWING_COUNT = "following_count";
    public void setFollowingCount(String followingCount) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (followingCount != null)
            editor.putString(FOLLOWING_COUNT, followingCount);
        else editor.remove(FOLLOWING_COUNT);
        editor.apply();
    }

    public String getFollowingCount() {
        return mPreferences.getString(FOLLOWING_COUNT, "");
    }


    private static final String UNREAD_COUNT = "unread_count";
    public void setUnreadCount(String unreadCount) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (unreadCount != null)
            editor.putString(UNREAD_COUNT, unreadCount);
        else editor.remove(UNREAD_COUNT);
        editor.apply();
    }

    public String getUnreadCount() {
        return mPreferences.getString(UNREAD_COUNT, "");
    }

    private static final String ROLE = "role";
    public void setRole(String unreadCount) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (unreadCount != null)
            editor.putString(ROLE, unreadCount);
        else editor.remove(ROLE);
        editor.apply();
    }

    public String getRole() {
        return mPreferences.getString(ROLE, "");
    }

    private static final String POST_COUNT = "post_count";
    public void setPostCount(String unreadCount) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (unreadCount != null)
            editor.putString(POST_COUNT, unreadCount);
        else editor.remove(POST_COUNT);
        editor.apply();
    }

    public String getPostCount() {
        return mPreferences.getString(POST_COUNT, "");
    }

    private static final String PLAYER_ID = "player_id";
    public void setPlayerId(String playerId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (playerId != null)
            editor.putString(PLAYER_ID, playerId);
        else editor.remove(PLAYER_ID);
        editor.apply();
    }
    public String getPlayerId() {
        return mPreferences.getString(PLAYER_ID, "");
    }

}
