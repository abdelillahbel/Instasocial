/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui.SeamLessViewPager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidapp.instasocial.ui.SeamLessViewPager.tools.ScrollableFragmentListener;
import com.androidapp.instasocial.ui.SeamLessViewPager.tools.ScrollableListener;


public abstract class BaseViewPagerFragment extends Fragment implements ScrollableListener {

    private static final String TAG = "BaseViewPagerFragment";
    protected ScrollableFragmentListener mListener;
    public static final String BUNDLE_FRAGMENT_INDEX = "BaseFragment.BUNDLE_FRAGMENT_INDEX";
    protected int mFragmentIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFragmentIndex = bundle.getInt(BUNDLE_FRAGMENT_INDEX, 0);
        }

        if (mListener != null) {
            mListener.onFragmentAttached(this, mFragmentIndex);
        }
    }

    //	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//            mListener = (ScrollableFragmentListener) activity;
//        } catch (ClassCastException e) {
//            Log.e(TAG, activity.toString() + " must implement ScrollableFragmentListener");
//        }
//    }
//modified function for fragment that have SeamlessViewPager
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        try {
            mListener = (ScrollableFragmentListener) parent;
        } catch (ClassCastException e) {
            Log.e(TAG, parent.toString() + " must implement ScrollableFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        if (mListener != null) {
            mListener.onFragmentDetached(this, mFragmentIndex);
        }

        super.onDetach();
        mListener = null;
    }
}
