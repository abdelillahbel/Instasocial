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


public class ApiProfileMedia extends ApiBase {
    //Endpoint name
    private String ENDPOINT = Config.ApiUrls.MEDIALISTINGS;

    /**
     * Member variables declarations
     */

    private String MEMBER_ID = "member_id";
    private String USER_NAME = "nickname";
    private String TYPE = "feed_type";
    private String PAGE_NO = "page_no";

    private String memberId;
    private String userName;
    private MediaType type;
    private int pageNo;

    public static enum MediaType {
        photo(1), video(2);
        private final int id;

        MediaType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public MediaType getType() {
        return type != null ? type : MediaType.photo;
    }

    public int getTypeVal() {
        return getType().getValue();
    }

    public String getTypeName() {
        return getType().name();
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId != null ? memberId : "";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName != null ? userName : "";
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageNo() {
        return pageNo > 0 ? pageNo : 1;
    }

    @Override
    public String getUrl() {
        return ENDPOINT+"?"+MEMBER_ID+"="+getMemberId()+"&"+TYPE+"="+
                String.valueOf(getTypeVal())+"&"+PAGE_NO+"="+String.valueOf(getPageNo())+"&"+"user_id="+App.preference().getUserId()+"&access_token="+App.preference().getAccessToken();
    }

    public ApiProfileMedia() {
    }

    public ApiProfileMedia(String memberId) {
        setMemberId(memberId);
    }

    public ApiProfileMedia(String memberId, String userName) {
        setMemberId(memberId);
        setUserName(userName);
    }

    public ApiProfileMedia(String memberId, String userName, MediaType type,int pageNo) {
        setMemberId(memberId);
        setUserName(userName);
        setType(type);
        setPageNo(pageNo);
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
        param.put(MEMBER_ID, getMemberId());
       // param.put(USER_NAME, getUserName());
        param.put(TYPE, String.valueOf(getTypeVal()));
        param.put(PAGE_NO, String.valueOf(getPageNo()));
        param.put("user_id", App.preference().getUserId());
        param.put("access_token",App.preference().getAccessToken());
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
