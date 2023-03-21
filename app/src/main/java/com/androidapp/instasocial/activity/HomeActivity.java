/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.BuildConfig;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.fragments.bottombar.HomeFeedFragment;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.fragments.bottombar.NotificationMainFragment;
import com.androidapp.instasocial.fragments.bottombar.SearchProfileFragment;
import com.androidapp.instasocial.fragments.feeds.FeedDetailsFragment;
import com.androidapp.instasocial.fragments.profile.FeedDetailFragment;
import com.androidapp.instasocial.fragments.profile.FollowRequestListFragment;
import com.androidapp.instasocial.modules.notification.NotificationBean;
import com.androidapp.instasocial.modules.profile.ProfileActivity;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.LogoutDialog;
import com.androidapp.instasocial.utils.BottomTabs;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.NotificationCountListener;
import com.androidapp.instasocial.utils.Res;


/**
 * This class contains home page of the app with bottom navigation menus
 */

public class HomeActivity extends AppCompatActivity implements BottomTabs.TabListener,NotificationCountListener {


    /**
     * Member variables declarations/initializations
     */
    public static final String TAG = "HomeActivity";
    protected HomeActivity m_actHome;
    public Views views;
    public static boolean reloadFeed = false;



    @Override
    protected void onResume() {
        try {
            super.onResume();

            if (reloadFeed) {
                if (views != null && views.bottomTabs != null && views.bottomTabs.home != null) {
                    views.bottomTabs.home.reloadFeed();//reload home feed
                }
                reloadFeed = false;
            }
            //update notification count
            views.bottomTabs.setMessageCount(App.preference().getNotificationCount());
        }catch (Exception e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        App.preference().setNotificationCountListener(this);
        views = new Views(this);
        Fresco.initialize(this);
        m_actHome = this;

    }

    /**
     *
     * @param tab action to take place when a tab is clicked
     */
    @Override
    public void onTabSelected(BottomTabs.Tab tab) {
        switch (tab) {
            case addPost:
                Intent addPostIntent = new Intent(this, AddPostActivity.class);
                startActivity(addPostIntent);
                break;
        }
    }

    /**
     *
     * @param count update notification count in UI
     */
    @Override
    public void onNotificationCountChange(int count) {
        if (views!=null && views.bottomTabs!=null)views.bottomTabs.setMessageCount(count);
    }


    /**
     * Class to hanlde UI views
     */
    public class Views {
        public final View layoutRoot;
        public final NavigationView navigationView;
        public final ScrollView menuScroll;
        public final TableLayout menuLayout;
        public final CompatTextView icLogout;
        public final BottomTabs bottomTabs;
        public final FrameLayout tabsContainer;
        public AdView bannerAd=null;
        public final LinearLayout layoutBannerAd;



        public Views(HomeActivity activity) {
            layoutRoot = activity.findViewById(R.id.layoutRoot);

            navigationView = activity.findViewById(R.id.navigationView);
            menuScroll = activity.findViewById(R.id.scrollViewMenu);
            menuLayout = activity.findViewById(R.id.tableLayoutMenu);
            icLogout = activity.findViewById(R.id.icLogout);
            tabsContainer = activity.findViewById(R.id.tabsContainer);
            bottomTabs = new BottomTabs(activity, tabsContainer);
            bottomTabs.setTabListener(activity);

            /**
             * banner ads logic goes here
             */
            layoutBannerAd = activity.findViewById(R.id.layoutBannerAd);
            if (Config.AD_SUPPORTED) {
                bannerAd = new AdView(activity);
                bannerAd.setAdSize(AdSize.BANNER);
                bannerAd.setAdUnitId(BuildConfig.DEBUG ? "ca-app-pub-3940256099942544/6300978111" : App.getStringRes(R.string.admob_banner_ad_id));
                layoutBannerAd.addView(bannerAd, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                AdRequest adRequest = new AdRequest.Builder().build();
                bannerAd.loadAd(adRequest);
                bannerAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the user is about to return
                        // to the app after tapping on an ad.
                    }
                });

            }

            init();
        }
            public void init() {
            bottomTabs.makeInitialSelection();//have home tab as selected by default
            icLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LogoutDialog(m_actHome).show(); // L
                }
            });

        }
    }


    /**
     *
     * @param requestCode
     * @param permissions contains the array of permissions
     * @param grantResults implies whether permission is granted or denied
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof HomeProfileFragment) {
            ((HomeProfileFragment) fragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (null == i)
            return;
    }


    /**
     * Actions that take place on back click
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);

        //make appropriate tab selections
        if (fragment != null) {
            if (fragment instanceof HomeFeedFragment) {
                views.bottomTabs.setUiHomeSelected();
            } else if (fragment instanceof FeedDetailsFragment) {
                ((FeedDetailsFragment) fragment).updateFeedDetailCallBacks.updateFeedDetail(((FeedDetailsFragment) fragment).feedBean, ((FeedDetailsFragment) fragment).position);
            }else if (fragment instanceof HomeProfileFragment) {
                views.bottomTabs.setUiProfileSelected();
            }else if (fragment instanceof NotificationMainFragment) {
                views.bottomTabs.setUiMessageSelected();
            }else if (fragment instanceof SearchProfileFragment) {
                views.bottomTabs.setUiFriendsSelected();
            }
        }


    }

    @Override
    public Resources getResources() {
        Resources res;
        res = new Res(super.getResources());
        return res;
    }

    /**
     * Add/Replace fragment logics  -----  STARTS HERE  -----
     */

    public static void addFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_Container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void addInnerFragment(FragmentManager fragmentManager, Fragment fragment) {
        if (fragment != null) {
            FragmentManager manager = fragmentManager;
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(R.id.tabsContainer, fragment, TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, boolean... isAddToBackStack) {
        boolean isBack = isAddToBackStack.length > 0 ? isAddToBackStack[0] : true;
        if (fragment != null) {
            FragmentManager manager = fragmentManager;
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.tabsContainer, fragment, TAG);
            if (isBack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, boolean... isAddToBackStack) {
        boolean isBack = isAddToBackStack.length > 0 ? isAddToBackStack[0] : true;
        if (fragment != null) {
            FragmentManager manager = fragmentManager;
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(R.id.tabsContainer, fragment, TAG);
            if (isBack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    /**
     * Add/Replace fragment logics  -----  ENDS HERE  -----
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.hasExtra(Config.ARG_PUSH)) {
            NotificationBean notification = NotificationBean.parsePushJson(intent.getStringExtra(Config.ARG_PUSH));
            Log.e(Config.ARG_PUSH, "data: " + notification.toString());

            switch (notification.type) {
                case request_sent:
                    //if follow request sent to private user : navigate to request list page
                        addFragment(new FollowRequestListFragment(), getSupportFragmentManager());
                    break;
                case request_accept:
                    try {
                        startActivity(ProfileActivity.getArgIntent(HomeActivity.this, notification.sender_id, ""));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //if private user accept follow request
                    break;
                case follow:
                    //if public user followed by
                    try {
                        startActivity(ProfileActivity.getArgIntent(HomeActivity.this, notification.sender_id, ""));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case like:
                    //if any likes on owner's feed
                    views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), false);
                    break;
                case comment:
                    //if any comment on owner's feed
                    views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), false);
                    break;
                case post:
                    //if follower post a new post
                    views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), false);
                    break;
                case video_post:
                    views.bottomTabs.replaceFragment(FeedDetailFragment.newInstance(notification.getObjectId(),null), false);
                    break;
                default:
            }

        }
    }
}
