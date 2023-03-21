/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.fragments.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidapp.instasocial.App;
import com.androidapp.instasocial.R;
import com.androidapp.instasocial.activity.HomeActivity;
import com.androidapp.instasocial.activity.WebActivity;
import com.androidapp.instasocial.ui.CompatImageView;
import com.androidapp.instasocial.ui.CompatTextView;
import com.androidapp.instasocial.ui.LogoutDialog;
import com.androidapp.instasocial.utils.Config;
import com.androidapp.instasocial.utils.ProfilePicListener;

/**
 * User settings page
 */
public class UserSettingsFragment extends Fragment implements View.OnClickListener {
    CompatImageView user_sett_back;
    View rootView;
    CompatTextView log_out, chg_pass, update_set, edit_profile, follow_req, blocked,privacy,terms;

    ProfilePicListener profileLocalPicListener;

    public static UserSettingsFragment newInstance(ProfilePicListener profileLocalPicListener) {
        UserSettingsFragment frag = new UserSettingsFragment();
        frag.profileLocalPicListener = profileLocalPicListener;
        return frag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile: // edit profile
                HomeActivity.addFragment(EditProfileFragment.newFragment(profileLocalPicListener), getActivity().getSupportFragmentManager());
                break;
            case R.id.user_sett_back: //Settings
                getActivity().getSupportFragmentManager().popBackStack();

                break;
            case R.id.log_out: //Logout
                new LogoutDialog(getActivity()).show();

                break;
            case R.id.chg_pass: //Change password
                HomeActivity.addFragment(new ChangePasswordFragment(), getActivity().getSupportFragmentManager());
                break;
            case R.id.update_set:
                HomeActivity.addFragment(new ManualSettingsFragment(), getActivity().getSupportFragmentManager());
                break;
            case R.id.follow_req: // Follow requests
                HomeActivity.addFragment(new FollowRequestListFragment(), getActivity().getSupportFragmentManager());
                break;
            case R.id.blocked: //Bloacked lists
                HomeActivity.addFragment(new UserBlockedListFragment(), getActivity().getSupportFragmentManager());
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout._user_settings_page_, null);
        initControls();
        initListeners();
        return rootView;
    }

    //UI controls initialization
    private void initControls() {
        follow_req = rootView.findViewById(R.id.follow_req);
        user_sett_back = rootView.findViewById(R.id.user_sett_back);
        log_out = rootView.findViewById(R.id.log_out);
        chg_pass = rootView.findViewById(R.id.chg_pass);
        update_set = rootView.findViewById(R.id.update_set);
        edit_profile = rootView.findViewById(R.id.edit_profile);
        blocked = rootView.findViewById(R.id.blocked);
        privacy=rootView.findViewById(R.id.privacy);
        terms=rootView.findViewById(R.id.terms);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebActivity.getArgIntent(getActivity(),Config.ApiUrls.POLICY_URL,App.getStringRes(R.string.str_privacy_policy)));
            }
        });
        rootView.findViewById(R.id.layoutTerms).setVisibility(Config.ApiUrls.TERMS_URL!=null && !Config.ApiUrls.TERMS_URL.isEmpty()?View.VISIBLE:View.GONE);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WebActivity.getArgIntent(getActivity(),Config.ApiUrls.TERMS_URL,App.getStringRes(R.string.str_terms)));
            }
        });
        chg_pass.setVisibility(App.preference().isSocialLogin() ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.view_chg_pass).setVisibility(App.preference().isSocialLogin() ? View.GONE : View.VISIBLE);
    }

    //Registering UI listeners
    private void initListeners() {
        log_out.setOnClickListener(this);
        user_sett_back.setOnClickListener(this);
        chg_pass.setOnClickListener(this);
        update_set.setOnClickListener(this);
        edit_profile.setOnClickListener(this);
        follow_req.setOnClickListener(this);
        blocked.setOnClickListener(this);
    }
}
