/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;


import com.androidapp.instasocial.App;

import java.util.HashMap;
import java.util.Map;

public abstract class ApiBase {
    protected String ARG_USER_ID = "userid";
    protected String ARG_ACCESS_TOKEN = "access_token";

    //API base url
    protected static final String BASEURL= Config.ApiUrls.API_BASE;

    //method to get api base url
    public String getBaseUrl(){
        return BASEURL;
    }
    public abstract String getUrl();
    public String getAccessToken() {
        return App.preference().getAccessToken();
    }
    public String getUserId(){
        return App.preference().getUserId();
    }


    public Map<String,String> getDefaultHeaders(){
        Map<String, String>  header = new HashMap<String, String>();
        return header;
    }
}
