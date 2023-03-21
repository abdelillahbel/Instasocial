/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui.SeamLessViewPager.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baoyz.widget.PullRefreshLayout;


public class PullToRefreshLayout extends PullRefreshLayout {
    private static final String TAG = "PullRefreshLayout";
    private boolean pullable = false;

    public PullToRefreshLayout(Context context) {
        super(context);
    }

    public void setPullable(boolean pullable) {
        this.pullable = pullable;
    }

    public boolean isPullable() {
        return pullable;
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isPullable() ? super.onTouchEvent(ev) : false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isPullable() ? super.onInterceptTouchEvent(ev) : false;
    }

//    @Override
//    protected void setRefreshing(boolean refreshing, boolean notify) {
//        boolean isPull=!(isPullable()&&refreshing);
//        Log.e(TAG,"setRefreshing2: setPullable: "+isPull);
//        setPullable(isPull);
//        super.setRefreshing(refreshing, notify);
//    }
}
