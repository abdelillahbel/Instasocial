/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.profile.api;

import com.androidapp.instasocial.utils.ApiBase;

import java.util.HashMap;
import java.util.Map;


public class ApiUpdateAvatar extends ApiBase {

    //endpoint name
    private String ENDPOINT = BASEURL + "update_avatar";

    private String IMAGE = "image";

    private String image = "";

    @Override
    public String getUrl() {
        return ENDPOINT;
    }

    public ApiUpdateAvatar(String image) {
        this.image=image;
    }

    public ApiUpdateAvatar() {
    }
    public void setAvatar(String image){
        this.image=image;
    }

    public String getImage() {
        return image!=null?image:"";
    }

    /**
     * build the post params as a hashmap
     * @return
     */
    public Map asPostParam() {
        Map<String, String> param = new HashMap<>();
        param.put(IMAGE, getImage());
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
