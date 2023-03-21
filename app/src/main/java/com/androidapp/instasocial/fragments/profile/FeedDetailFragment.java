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

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.fragments.bottombar.HomeFeedFragment;
import com.androidapp.instasocial.modules.feed.api.ApiFeed;

/**
 * Feed detailed view
 */

public class FeedDetailFragment extends HomeFeedFragment {
    private String TAG = "FeedDetailFragment";

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


    public static FeedDetailFragment newInstance(String feedId, String memberId) {
        FeedDetailFragment newsFeedFragment = new FeedDetailFragment();
        newsFeedFragment.memberName = App.preference().getUserName();
        newsFeedFragment.memberId = memberId != null ? memberId : App.preference().getUserId();
        newsFeedFragment.isPullable = true;
        newsFeedFragment.pageType = ApiFeed.FeedType.custom.getValue();
        newsFeedFragment.feedId = feedId;
        newsFeedFragment.hasActionBar = false;
        newsFeedFragment.isFeedDetail = true;
        return newsFeedFragment;
    }


}
