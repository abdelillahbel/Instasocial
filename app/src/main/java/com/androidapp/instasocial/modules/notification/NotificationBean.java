/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.notification;

import com.androidapp.instasocial.states.NotificationType;

import org.json.JSONObject;

/**
 * bean class for notifications
 */
public class NotificationBean {
    public String notification_id="";
    public String title="";
    public NotificationType type=NotificationType.unknown;
    public String object_id="";
    public String message="";
    public String individualmessage="";
    public String sender_id="";
    public String author_name="";
    public String profile_pic="";
    public String status="";
    public String created_at="";
    private String createdAgo="";
    private String ownerId="";

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId!=null?ownerId:"";
    }
    public String getNotification_Id() {
        return notification_id!=null?notification_id:"";
    }

    public String getProfile_pic() {
        return profile_pic!=null?profile_pic:"no-data-found";
    }

    public void setCreatedAgo(String createdAgo) {
        this.createdAgo = createdAgo;
    }

    public String getCreatedAgo() {
        return createdAgo!=null?createdAgo:(created_at!=null?created_at:"");
    }

    //called when push notification received
    public static NotificationBean parsePushJson(JSONObject obj){
        NotificationBean ins=new NotificationBean();
        try{
            ins.notification_id = obj.getString("push_notification_id");
            ins.title = obj.getString("title");
            ins.type = NotificationType.parse(obj.getString("type"));
            ins.object_id = obj.getString("object_id");
            ins.message = obj.getString("message");
            ins.sender_id = obj.getString("sender_user_id");
            ins.author_name = obj.getString("sender_user_name");
            ins.setOwnerId(obj.has("ID")?obj.getString("ID"):"");
        }catch (Exception e){
            e.printStackTrace();
        }
        return ins;
    }

    public static NotificationBean parsePushJson(String json){
        NotificationBean ins=new NotificationBean();
        try{
            ins=parsePushJson(new JSONObject(json));
        }catch (Exception e){
            e.printStackTrace();
        }
        return ins;
    }

    public String getObjectId() {
        return object_id!=null?object_id:"";
    }

    @Override
    public String toString() {
        return "NotificationBean{" +
                "notification_id='" + notification_id + '\'' +
                ", title='" + title + '\'' +
//                ", groupname='" + groupname + '\'' +
                ", type=" + type +
                ", object_id='" + object_id + '\'' +
                ", message='" + message + '\'' +
                ", individualmessage='" + individualmessage + '\'' +
                ", sender_id='" + sender_id + '\'' +
                ", author_name='" + author_name + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                ", status='" + status + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
