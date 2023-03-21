/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.utils.ApiBase;
import com.androidapp.instasocial.utils.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Api helper class to get prpfile info
 */

public class ApiProfileInfo extends ApiBase {
    //Endpoint name
    private String ENDPOINT = Config.ApiUrls.GET_PROFILE;

    /**
     * Member variables declarations
     */

    private String MEMBER_ID = "member_id";
    private String USER_NAME = "username";
    private String USER_ID="userid";
    private String ACCESSTOKEN="access_token";
    private String memberId;
    private String userName;

    /**
     * Getters and seters
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId!=null?memberId:"";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName!=null?userName:"";
    }


    /**
     * Generate and return api url
     * @return
     */
    @Override
    public String getUrl() {
        return ENDPOINT+"?"+USER_ID+"="+App.preference().getUserId()+"&"+ACCESSTOKEN+"="+App.preference().getAccessToken()+"&"+MEMBER_ID+"="+memberId+"&"+USER_NAME+"="+userName;
    }

     public ApiProfileInfo(String memberId) {
        setMemberId(memberId);
    }

    @Override
    public String getBaseUrl() {
        return ENDPOINT+"?"+USER_ID+"="+App.preference().getUserId()+"&"+ACCESSTOKEN+"="+App.preference().getAccessToken()+"&"+MEMBER_ID+"="+memberId+"&"+USER_NAME+"="+userName;

    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
        param.put(USER_ID, App.preference().getUserId());
        param.put(ACCESSTOKEN,App.preference().getAccessToken());
        param.put(MEMBER_ID, getMemberId());
        param.put(USER_NAME, getUserName());
        return param;
    }

    @Override
    public String toString() {
        return "{ url: " + getUrl() + "\n"
                + "  headers: " + getDefaultHeaders() + "\n"
                + "  params : " + asPostParam() + "\n" +
                "}";
    }
}
