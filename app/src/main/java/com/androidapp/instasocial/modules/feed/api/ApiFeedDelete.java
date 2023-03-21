package com.androidapp.instasocial.modules.feed.api;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.utils.ApiBase;
import com.androidapp.instasocial.utils.Config;

import java.util.HashMap;
import java.util.Map;



public class ApiFeedDelete extends ApiBase {

    //Endpoint name

    private String ENDPOINT = Config.ApiUrls.FEED_ACTIONS;

        private String FEED_ID="post_id";
        private String feedID;

        @Override
        public String getUrl() {
            return ENDPOINT;
        }
        public ApiFeedDelete(String feedID) {
            this.feedID=feedID;
        }

    /**
     * build the post params as a hashmap
     * @return
     */
        public Map asPostParam() {
            Map<String, String> param = new HashMap<>();
            param.put(FEED_ID, "" + feedID);
            param.put("user_id", App.preference().getUserId());
            param.put("access_token",App.preference().getAccessToken());
            param.put("action_type","1");
            param.put("cmt_desc","");
            param.put("comment_id","");
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
