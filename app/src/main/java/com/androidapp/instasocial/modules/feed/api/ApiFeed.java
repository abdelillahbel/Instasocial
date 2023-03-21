package com.androidapp.instasocial.modules.feed.api;

import com.androidapp.instasocial.utils.ApiBase;

import java.util.HashMap;
import java.util.Map;



/**
 * Api helper class for feed detail
 */
public class ApiFeed extends ApiBase {

    //Endpoint name

    private String ENDPOINT = BASEURL + "get_home_timeline";
    /**
     * Member variables declarations
     */
    private String PAGE_TYPE = "page_type";
    private String PAGE_NO = "page_no";
    private String PROFILE_ID = "profile_id";
    private String FEED_ID = "feed_id";
    private String NICK_NAME="nickname";

    private int pageNo;
    private String profileId = "";
    private String pageType = "";
    private String feedId;
    private String nickName;

    public static enum FeedType {
        home(0), my(1), custom(2), other(3);
        private final int id;

        FeedType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }


    public int getPageNo() {
        return pageNo != 0 ? pageNo : 1;
    }

    public String getProfileId() {
        return profileId != null ? profileId : "";
    }

    public String getPageType() {
        return pageType != null ? pageType : "";
    }

    public String getFeedId() {
        return feedId != null ? feedId : "";
    }

    public String getNickName() {
        return nickName!=null?nickName:"";
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    //    @Override
//    public String getUrl() {
//        return ENDPOINT + "?" + ARG_USER_ID + "=" + getUserId()
//                + "&" + ARG_ACCESS_TOKEN + "=" + getAccessToken() + "&"
//                + PAGE_TYPE + "=" + pageType + "&" + PAGE_NO + "=" + pageNo + "&" + PROFILE_ID + "=" + profileId + "&" + FEED_ID + "=" + feedId;
//    }

    @Override
    public String getUrl() {
        return ENDPOINT ;
    }

    public ApiFeed(int page_no, String profile_id, String page_type, String feedId) {
        this.pageNo = page_no;
        this.profileId = profile_id;
        this.pageType = page_type;
        this.feedId = feedId;
    }

    @Override
    public String getBaseUrl() {
        return ENDPOINT;
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
        param.put(PAGE_TYPE, getPageType());
        param.put(PAGE_NO, String.valueOf(getPageNo()));
        param.put(PROFILE_ID, getProfileId());
        param.put(FEED_ID, getFeedId());
        param.put(NICK_NAME,getNickName());
        return param;
    }

    @Override
    public String toString() {
        return "{ url: " + getUrl() + "\n"
                + "  headers: " + getDefaultHeaders() + "\n"
                + "  params : " + asPostParam()+"\n"+
                "}";
    }
}
