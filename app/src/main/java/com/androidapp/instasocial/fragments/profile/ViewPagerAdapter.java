/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * View pager to load data under profile tabs
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence mTitles[];

    ProfilePostGridFragment fragPostGrid;
    ProfilePostListFragment fragPostList;
    ProfilePhotosFragment fragPhotos;
    ProfileVideosFragment fragVideos;


    String userId;
    String userName;
    private boolean isOwnProfile = true;

    public void setOwnProfile(boolean ownProfile) {
        isOwnProfile = ownProfile;
    }

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], String memberId, String memberName, boolean isOwnProfile) {
        super(fm);
        this.mTitles = mTitles;
        this.userId = memberId;
        this.userName = memberName;
        setOwnProfile(isOwnProfile);
    }

    public Fragment getFragment(int index) {
        switch (index) {
            case 0:
                return fragPostGrid;
            case 1:
                return fragPostList;
            case 2:
                return fragPhotos;
            case 3:
                return fragVideos;
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                fragPostGrid=ProfilePostGridFragment.newInstance(userId, userName, false, position);
                return fragPostGrid;
            case 1:
                fragPostList = ProfilePostListFragment.newInstance(userId, userName, false, position);
                return fragPostList;
            case 2:
                fragPhotos = ProfilePhotosFragment.newInstance(userId, userName, position);
                return fragPhotos;
            case 3:
                fragVideos = ProfileVideosFragment.newInstance(userId, userName, position);
                return fragVideos;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];

    }
}
