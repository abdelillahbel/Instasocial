package com.androidapp.instasocial.modules.feed.api;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.utils.ApiBase;
import com.androidapp.instasocial.utils.Config;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;



/**
 * Api helper class to add comments
 */
public class ApiCommentAdd extends ApiBase {

    //Endpoint name
    private String ENDPOINT = Config.ApiUrls.FEED_ACTIONS;


    /**
     * Member variables declarations
     */

    private String FEED_ID="post_id";
    private String COMMENT_TEXT="cmt_desc";
    private String MODULE_NAME="module_name";
    private String COMMENT_ID="comment_id";
    private String ACTION_TYPE="action_type";


    private String moduleName;
    private String feedID;
    private String commentText;
    private String commentID;


    @Override
    public String getUrl() {
        return ENDPOINT;
    }

    /**
     * Add a comment
     * @param feedID id of the feed on which comment is added
     * @param cmtText comment text to be added
     * @param commentID available while editing a comment
     */
    public ApiCommentAdd(String feedID, String cmtText, String commentID) {
        this.moduleName=moduleName;
        this.feedID=feedID;
        try {
            this.commentText = StringEscapeUtils.escapeJava(cmtText);
        }catch (Exception e){
            e.printStackTrace();
            this.commentText=commentText;
        }
        this.commentID=commentID;
      }

    public String getModuleName() {
        return moduleName!=null?moduleName:"";
    }

    public String getFeedID() {
        return feedID!=null?feedID:"";
    }

    public String getCommentID() {
        return commentID!=null?commentID:"";
    }

    public String getCommentText() {
        return commentText;
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
     //   param.put(MODULE_NAME,  getModuleName());
        param.put(FEED_ID,  getFeedID());
        param.put(COMMENT_TEXT, getCommentText() );
        param.put(COMMENT_ID,  getCommentID());
        param.put("user_id", App.preference().getUserId());
        param.put("access_token",App.preference().getAccessToken());
        param.put(ACTION_TYPE,"3");
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
