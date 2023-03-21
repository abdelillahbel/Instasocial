package com.androidapp.instasocial.webservice;

import com.androidapp.instasocial.utils.Config;



public class ApiUtils {
    private ApiUtils() {
    }

    public static final String BASE_URL = Config.ApiUrls.API_BASE;//"http://34.195.25.180:7003/v1/";

    public static ApiService getAPIService() {

        return RetroClient.getClient(BASE_URL).create(ApiService.class);
    }
}
