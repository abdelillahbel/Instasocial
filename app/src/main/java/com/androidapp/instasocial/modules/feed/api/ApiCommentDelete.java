package com.androidapp.instasocial.modules.feed.api;

import com.androidapp.instasocial.utils.ApiBase;
import com.androidapp.instasocial.utils.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Api helper class to delete a comment
 */

public class ApiCommentDelete extends ApiBase {

    //Endpoint name
    private String ENDPOINT = Config.ApiUrls.FEED_ACTIONS;

    /**
     * Member variables declarations
     */
    private String MODULE_NAME = "module_name";
    private String FEED_ID = "feed_id";
    private String COMMENT_ID = "cmt_id";

    private String moduleName;
    private String feedID;
    private String commentID;

    @Override
    public String getUrl() {
        return ENDPOINT;
    }

    /**
     * Delete a comment
     * @param moduleName module name of the feed
     * @param feedID id of the feed
     * @param commentID id of the comment
     */
    public ApiCommentDelete(String moduleName, String feedID, String commentID) {
        this.moduleName = moduleName;
        this.feedID = feedID;
        this.commentID = commentID;
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
        param.put(MODULE_NAME, "" + getModuleName());
        param.put(FEED_ID, "" + getFeedID());
        param.put(COMMENT_ID, "" + getCommentID());
        return param;
    }

    public String getModuleName() {
        return moduleName != null ? moduleName : "";
    }

    public String getFeedID() {
        return feedID != null ? feedID : "";
    }

    public String getCommentID() {
        return commentID != null ? commentID : "";
    }

    @Override
    public String toString() {
        return "{ url: " + getUrl() + "\n"
                + "  headers: " + getDefaultHeaders() + "\n"
                + "  params : " + asPostParam() + "\n" +
                "}";
    }
}
