package com.androidapp.instasocial.modules.feed.api;

import com.androidapp.instasocial.utils.ApiBase;

import java.util.HashMap;
import java.util.Map;


/**
 * Api helper class to like a post
 */
public class ApiLike extends ApiBase {
    //Endpoint name
    private String ENDPOINT = BASEURL + "add_like";

    /**
     * Member variables declarations
     */

    private String FEED_ID = "feed_id";
    private String MODULE_NAME = "module_name";
    private String IP = "ip";

    private String feedId = "";
    private String moduleName = "";

    @Override
    public String getUrl() {
        return ENDPOINT;
    }

    public String getModuleName() {
        return moduleName != null ? moduleName : "";
    }

    public ApiLike(String feedId) {
        this.feedId = feedId;
        this.moduleName = moduleName;
    }

    public String getFeedId() {
        return feedId != null ? feedId : "";
    }


    public String getIpAddress(){
        return "";
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
//        param.put(ARG_USER_ID, getUserId());
//        param.put(ARG_ACCESS_TOKEN, "" + getAccessToken());
        param.put(FEED_ID, getFeedId());
        param.put(MODULE_NAME, "" + getModuleName());
        param.put(IP, getIpAddress());
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
