/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.feed;

import org.apache.commons.lang3.StringEscapeUtils;


public class FeedComment {

    String cmt_id;
    String cmt_text;
    String cmt_objectid;
    String cmt_author_id;
    String cmtAuthorPic;
    String cmt_user_name;
    String time;
    String module_name;

    public String getCmtAuthorPic() {
        return cmtAuthorPic!=null?cmtAuthorPic:"pic-not-available";
    }

    public void setCmtAuthorPic(String cmtAuthorPic) {
        this.cmtAuthorPic = cmtAuthorPic;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }


    public String getCmt_id() {
        return cmt_id;
    }

    public void setCmt_id(String cmt_id) {
        this.cmt_id = cmt_id;
    }

    public String getCmt_text() {
        try{
            return StringEscapeUtils.unescapeJava(cmt_text);
        }catch (Exception e){
            e.printStackTrace();
            return cmt_text;
        }
    }

    public void setCmt_text(String cmt_text) {
        try{
            this.cmt_text= StringEscapeUtils.escapeJava(cmt_text);
        }catch (Exception e){
            e.printStackTrace();
            this.cmt_text = cmt_text;
        }
    }

    public String getCmt_objectid() {
        return cmt_objectid;
    }

    public void setCmt_objectid(String cmt_objectid) {
        this.cmt_objectid = cmt_objectid;
    }

    public String getCmt_author_id() {
        return cmt_author_id;
    }

    public void setCmt_author_id(String cmt_author_id) {
        this.cmt_author_id = cmt_author_id;
    }


    public String getCmt_user_name() {
        return cmt_user_name;
    }

    public void setCmt_user_name(String cmt_user_name) {
        this.cmt_user_name = cmt_user_name;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
