package com.androidapp.instasocial.modules.feed.api;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.utils.ApiBase;
import com.androidapp.instasocial.utils.Config;

import java.util.HashMap;
import java.util.Map;


/**
 * Api helper class to list the comments
 */

public class ApiComments extends ApiBase {

    //Endpoint name

    private String ENDPOINT = Config.ApiUrls.COMMENTLIST;

    /**
     * Member variables declarations
     */


    private String PAGE_NO = "page_no";
   // private String MODULE_NAME = "module_name";
    private String FEED_ID = "post_id";

    private String moduleName;
    private String feedID;
    private int pageNo;

    public String getModuleName() {
        return moduleName!=null?moduleName:"";
    }

    public String getFeedID() {
        return feedID!=null?feedID:"";
    }

    public int getPageNo() {
        return pageNo!=0?pageNo:1;
    }


    @Override
    public String getUrl() {
        return ENDPOINT+"?user_id="+App.preference().getUserId()+"&access_token="+App.preference().getAccessToken()+"&post_id="+getFeedID()+"&page_no="+pageNo;
    }

    public ApiComments(int page_no, String feedID) {
        this.pageNo = page_no;
        this.feedID = feedID;
        this.moduleName = moduleName;
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
        param.put(PAGE_NO, String.valueOf(getPageNo()));
        param.put(FEED_ID, getFeedID());
        param.put("user_id", App.preference().getUserId());
        param.put("access_token",App.preference().getAccessToken());
       // param.put(MODULE_NAME, getModuleName());
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
