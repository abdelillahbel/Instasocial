/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.fragments.bottombar.HomeFeedFragment;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.fragments.bottombar.NotificationMainFragment;
import com.androidapp.instasocial.fragments.bottombar.SearchProfileFragment;


/**
 * Bottom navigation UI class
 */
public class BottomTabs implements View.OnClickListener {

    /**
     * Member variables declarations/initializations
     */
    public static final String TAG = "fragment";
    HomeActivity activity;
    public HomeFeedFragment home;
    public HomeProfileFragment profile;
    android.support.v4.app.FragmentManager fragmentManager;
    FrameLayout tabsContainer;
    public static enum Tab {
        home, friends, addPost, message, profile
    }
    public interface TabListener {
        public void onTabSelected(Tab tab);
    }
    private TabListener tabListener = null;
    public final AppCompatImageView icHome;
    public final AppCompatImageView icFriends;
    public final AppCompatImageView icAddPost;
    public final AppCompatImageView icMessages;
    public final AppCompatImageView icProfile;
    public final TextView txtMessageCount;
    public final LinearLayout layHome;
    public final LinearLayout layFriends;
    public final LinearLayout layAddPost;
    public final LinearLayout layMessages;
    public final LinearLayout layProfile;


    /**
     * UI controls initialization
     * @param activity
     * @param tabsContainer
     */

    public BottomTabs(HomeActivity activity, FrameLayout tabsContainer) {
        this.activity = activity;
        this.tabsContainer = tabsContainer;
        fragmentManager = activity.getSupportFragmentManager();
        icHome = activity.findViewById(R.id.icHome);
        icFriends = activity.findViewById(R.id.icFriends);
        icAddPost = activity.findViewById(R.id.icAddPost);
        icMessages = activity.findViewById(R.id.icMessages);
        icProfile = activity.findViewById(R.id.icProfile);

        txtMessageCount = activity.findViewById(R.id.txtMessageCount);
        setMessageCount(0);
        layHome = activity.findViewById(R.id.layoutHomeTab);
        layFriends = activity.findViewById(R.id.layoutFriendsTab);
        layAddPost = activity.findViewById(R.id.layoutAddPost);
        layMessages = activity.findViewById(R.id.layoutMessages);
        layProfile = activity.findViewById(R.id.layoutProfile);

        layHome.setOnClickListener(this);
        layFriends.setOnClickListener(this);
        layAddPost.setOnClickListener(this);
        layMessages.setOnClickListener(this);
        layProfile.setOnClickListener(this);


        home = HomeFeedFragment.newInstance(true);
        setUiHomeSelected();
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcSelected));
        if (tabListener != null) {
            tabListener.onTabSelected(Tab.home);
        }

    }


    public void setTabListener(TabListener tabListener) {
        this.tabListener = tabListener;
    }

    /**
     * set count on notification icons
     * @param count
     */
    public void setMessageCount(int count) {
        Log.e(TAG, "messageCount: " + count);
        txtMessageCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        txtMessageCount.setText(String.valueOf(count));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutHomeTab:
                home = HomeFeedFragment.newInstance(true);
                select(Tab.home);
                break;
            case R.id.layoutFriendsTab:
                select(Tab.friends);
                break;
            case R.id.layoutAddPost:
                select(Tab.addPost);
                break;
            case R.id.layoutMessages:
                select(Tab.message);
                break;
            case R.id.layoutProfile:
                profile = HomeProfileFragment.instance(App.preference().getUserId(), App.preference().getUserName(), true,false);
                select(Tab.profile);
                break;
        }
    }

    /**
     * By default home has to be selected
     */
    public void makeInitialSelection() {
        setUiHomeSelected();
        replaceFragment(home, false);
    }

    /**
     * Make selection
     * @param tab
     * @param canHandle
     */
    public void select(Tab tab, boolean... canHandle) {
        boolean can = canHandle.length > 0 ? canHandle[0] : true;
        switch (tab) {
            case home:
                setUiHomeSelected();
                replaceFragment(home);
                if (can && tabListener != null) {
                    tabListener.onTabSelected(tab);
                }
                break;
            case friends:
                setUiFriendsSelected();
                replaceFragment(SearchProfileFragment.newInstance(true,false));
                if (can && tabListener != null) {
                    tabListener.onTabSelected(tab);
                }
                break;
            case addPost:
//                setUiAddPostSelected();
//                replaceFragment(addPost);
                if (can && tabListener != null) {
                    tabListener.onTabSelected(tab);
                }
                break;
            case message:
                setUiMessageSelected();
                replaceFragment(new NotificationMainFragment());
                if (can && tabListener != null) {
                    tabListener.onTabSelected(tab);
                }
                break;
            case profile:
                setUiProfileSelected();
                replaceFragment(profile);
                if (can && tabListener != null) {
                    tabListener.onTabSelected(tab);
                }
                break;
        }
    }

    public void setUiHomeSelected() {

        icHome.setColorFilter(App.getColorRes(R.color.bottomBarIcSelected));
        icFriends.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icAddPost.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icProfile.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
    }


    public void setUiFriendsSelected() {
        icHome.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icFriends.setColorFilter(App.getColorRes(R.color.bottomBarIcSelected));
        icAddPost.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icProfile.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
    }


    public void setUiAddPostSelected() {
        icHome.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icFriends.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icAddPost.setColorFilter(App.getColorRes(R.color.bottomBarIcSelected));
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icProfile.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
    }


    public void setUiMessageSelected() {
        icHome.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icFriends.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icAddPost.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcSelected));
        icProfile.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
    }


    public void setUiProfileSelected() {
        icHome.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icFriends.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icAddPost.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icProfile.setColorFilter(App.getColorRes(R.color.bottomBarIcSelected));
    }

    public void setNonSelected() {
        icHome.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icFriends.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icAddPost.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icMessages.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
        icProfile.setColorFilter(App.getColorRes(R.color.bottomBarIcUnSelected));
    }

    public void replaceFragment(Fragment fragment, boolean... isAddToBackStack) {
        HomeActivity.replaceFragment(fragmentManager, fragment, isAddToBackStack);
    }

    public void addFragment(Fragment fragment, boolean... isAddToBackStack) {
        HomeActivity.addFragment(fragmentManager, fragment, isAddToBackStack);
    }
}
