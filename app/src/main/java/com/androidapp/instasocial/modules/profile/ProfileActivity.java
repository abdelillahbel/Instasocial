/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.modules.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.androidapp.instasocial.App;
import com.androidapp.instasocial.BuildConfig;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.fragments.bottombar.HomeProfileFragment;
import com.androidapp.instasocial.utils.Config;

/**
 * This class is used to view other user's profile
 */
public class ProfileActivity extends FragmentActivity {
    public static final String TAG = "ProfileActivity";
    public static final String ARG_USER_ID = "userid";
    public static final String ARG_USER_NAME = "username";

    String userId;
    String userName;
    Views views;
    HomeProfileFragment profileFragment;

    RewardedVideoAd mRewardedVideoAd;
    public static Intent getArgIntent(Activity activity, String userId, String userName) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(ARG_USER_ID, userId);
        intent.putExtra(ARG_USER_NAME, userName);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        userId = i != null && i.hasExtra(ARG_USER_ID) ? i.getStringExtra(ARG_USER_ID) : "";
        userName = i != null && i.hasExtra(ARG_USER_NAME) ? i.getStringExtra(ARG_USER_NAME) : App.preference().getUserName();

        Log.e(TAG, "userId: " + userId + " userName: " + userName);
        setContentView(R.layout.activity_profile);
        views = new Views();

        profileFragment = HomeProfileFragment.instance(userId, userName,true,true);
        replaceFragment(profileFragment);
        if (Config.AD_SUPPORTED) {
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {

                }

                @Override
                public void onRewardedVideoAdOpened() {

                }

                @Override
                public void onRewardedVideoStarted() {

                }

                @Override
                public void onRewardedVideoAdClosed() {

                }

                @Override
                public void onRewarded(RewardItem rewardItem) {

                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {

                }

                @Override
                public void onRewardedVideoCompleted() {

                }
            });

            loadRewardedVideoAd();
        }
    }

    @Override
    protected void onDestroy() {
        if (mRewardedVideoAd!=null&&mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
        super.onDestroy();
    }

    public void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(BuildConfig.DEBUG?"ca-app-pub-3940256099942544/5224354917":App.getStringRes(R.string.admob_reward_ad_id),
                new AdRequest.Builder().build());
    }

    //UI view handling logics
    public class Views {
        final FrameLayout container;
        final ImageView icBack,icSearch;


        public Views() {
            container = findViewById(R.id.container);
            icSearch=findViewById(R.id.icSearch);
            icSearch.setVisibility(View.GONE);
            icBack = findViewById(R.id.icNavigation);
            icBack.setImageResource(R.drawable.ic_back);
            icBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        finish();
                    }
                }
            });
        }
    }


    //Substitute profile fragment in the activity container
    public void replaceFragment(Fragment fragment, boolean... isBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            FragmentManager manager = fragmentManager;
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, TAG);
            if (isBackStack.length > 0 && isBackStack[0])
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof HomeProfileFragment) {
            ((HomeProfileFragment) fragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
