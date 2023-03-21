package com.androidapp.instasocial.webservice;

import com.androidapp.instasocial.modules.notification.followingbean.FollowingNotificationList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



public interface ApiService {
    /*
   Retrofit get annotation with our URL
   And our method that will return us the List of EmployeeList
   */

    @GET("user/notifications")
    Call<FollowingNotificationList> GetFollowingNotificationResponse(@Query("user_id") String user_id, @Query("access_token") String access_token, @Query("type") int feed_type, @Query("page_no") int page_no);


}
