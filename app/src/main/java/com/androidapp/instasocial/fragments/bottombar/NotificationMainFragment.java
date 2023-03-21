/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/
package com.androidapp.instasocial.fragments.bottombar;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.notification.FollowingNotificationFragment;
import com.androidapp.instasocial.fragments.notification.YouNotificationFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that is used to display tne nain notification page
 */
public class NotificationMainFragment extends android.support.v4.app.Fragment{
    View rootView;
    TabLayout tab;
    ViewPager viewPager;
    HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity= (HomeActivity) getActivity();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.notification_tab_layout, null);
        initControls();
        return  rootView;
    }

    /**
     * UI controls initialization
     */
    public void initControls() {

        tab = rootView.findViewById(R.id.tab);
        viewPager = rootView.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tab.setupWithViewPager(viewPager);
    }

    /**
     * Add Following and You fragments to the view pager
     * @param viewPager
     */
    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(FollowingNotificationFragment.newInstance(), getActivity().getResources().getString(R.string.notification_following_tab));
        adapter.addFragment(YouNotificationFragment.newInstance(), getActivity().getResources().getString(R.string.notification_you_tab));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    /**
     * Class to adapt data to the viewpager
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}


