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
 * Video posts grid view in profile page
 */
public class ProfileVideosFragment extends ProfileMediaFragment implements ProfileMediaFragment.ProfileMediaCallback {
    private String TAG = "ProfileVideosFragment";

    /**
     * new instance creation to assign member id, name and media type
     * @param memberId
     * @param memberName
     * @param index
     * @return
     */
    public static ProfileVideosFragment newInstance(String memberId, String memberName, int index) {
        ProfileVideosFragment newsFeedFragment = new ProfileVideosFragment();
        newsFeedFragment.memberName = memberName;
        newsFeedFragment.memberId = memberId;
        newsFeedFragment.mediaType= ApiProfileMedia.MediaType.video;
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
