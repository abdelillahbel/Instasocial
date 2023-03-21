package com.androidapp.instasocial.webservice;

import com.androidapp.instasocial.App;

public class ApiParams {
    public static String USER_ID="user_id";
    public static String ACCESS_TOKEN="access_token";
    public static String MEMBER_ID="member_id";
    public static String PAGE_NO="page_no";
    public static String USERID="userid";
    public static String USERNAME="username";
    public static String RECORD="record";
    public static String KEYWORD="keyword";
    public static String SORT="sort";
    public static String TYPE="type";
    public static String STATUS="status";
    public static String FOLLOWER_ID="follower_id";
    public static String CONNECT_ID="connect_id";
    public static String POST_ID="post_id";
    public static String NOTIFICATION_ID="notification_id";


    public static String getUserCredential(){
        String headerCredentials= ApiParams.USER_ID+"=" +
                App.preference().getUserId() + "&"+ ApiParams.ACCESS_TOKEN+"=" + App.preference().getAccessToken();
        return headerCredentials;
    }
}
