/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import android.content.Context;

import com.androidapp.instasocial.fragments.bottombar.HomeFeedFragment;
import com.androidapp.instasocial.modules.feed.api.ApiFeed;


/**
 * Media posts listing in profile page
 */
public class ProfilePostListFragment extends HomeFeedFragment {
    private String TAG = "ProfilePostListFragment";

    @Override
    public void onAttach(Context context) {
        try {
            refreshListener = (RefreshListener) getParentFragment();
        }catch (Exception e){
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        refreshListener=null;
        super.onDetach();
    }


    /**
     * new instance creation to assign member id, name and feed type
     * @param memberId
     * @param memberName
     * @param isPullable
     * @param index
     * @return
     */
    public static ProfilePostListFragment newInstance(String memberId, String memberName, boolean isPullable, int index) {
        ProfilePostListFragment newsFeedFragment = new ProfilePostListFragment();
        newsFeedFragment.memberName = memberName;
        newsFeedFragment.memberId = memberId;
        newsFeedFragment.isPullable = isPullable;
        newsFeedFragment.setFeedType(ApiFeed.FeedType.other);
        newsFeedFragment.mFragmentIndex = index;
        newsFeedFragment.hasActionBar=false;
        return newsFeedFragment;
    }


}
