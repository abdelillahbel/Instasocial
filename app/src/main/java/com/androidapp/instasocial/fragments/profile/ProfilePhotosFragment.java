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

import java.util.ArrayList;

/**
 * Image posts in gridview
 */
public class ProfilePhotosFragment extends ProfileMediaFragment implements ProfileMediaFragment.ProfileMediaCallback{
    private String TAG = "ProfilePhotosFragment";

    /**
     * Fragment instance creation
     * @param memberId
     * @param memberName
     * @param index
     * @return
     */
    public static ProfilePhotosFragment newInstance(String memberId, String memberName, int index) {
        ProfilePhotosFragment newsFeedFragment = new ProfilePhotosFragment();
        newsFeedFragment.memberName = memberName;
        newsFeedFragment.memberId = memberId;
        newsFeedFragment.mediaType= ApiProfileMedia.MediaType.photo;
        newsFeedFragment.mediaCallback=newsFeedFragment;
        newsFeedFragment.mFragmentIndex = index;
        return newsFeedFragment;
    }

    @Override
    public void onAttach(Context context) {
        try {
            refreshListener = (RefreshListener) getParentFragment();
        } catch (Exception e) {
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        refreshListener = null;
        super.onDetach();
    }

    @Override
    public void onMediaResponse(String apiStatus, ApiProfileMedia.MediaType mediaType, ArrayList<ProfileMedia> media) {
        //handle api response here
        //fit data to ui
    }
}
