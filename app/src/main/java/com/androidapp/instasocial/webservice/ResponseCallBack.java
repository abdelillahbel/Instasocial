package com.androidapp.instasocial.webservice;

/**
 * interface to handle network response
 */
public interface ResponseCallBack {
     void onResponse(String response,int pageNo,String TAG);
     void onErrorResponse(String response,int pageNo,String TAG);
}
