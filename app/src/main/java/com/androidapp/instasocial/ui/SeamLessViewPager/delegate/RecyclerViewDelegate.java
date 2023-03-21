/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui.SeamLessViewPager.delegate;


import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewDelegate implements ViewDelegate {

    //    private static final String TAG="RecyclerDelegate";
    private final int[] mViewLocationResult = new int[2];
    private final Rect mRect = new Rect();

    public boolean isViewBeingDragged(MotionEvent event, RecyclerView view) {
        boolean isEmpty=view.getAdapter().getItemCount()<=0;
        if (view.getAdapter() == null || isEmpty) {
            return true;
        }
        view.getLocationOnScreen(mViewLocationResult);
        final int viewLeft = mViewLocationResult[0], viewTop = mViewLocationResult[1];
//        final int viewLeft = 0, viewTop = mViewLocationResult[1];//todo modified code

        mRect.set(viewLeft, viewTop, viewLeft + view.getWidth(), viewTop + view.getHeight());
        final int rawX = (int) event.getRawX(), rawY = (int) event.getRawY();
//        view.getLocalVisibleRect(mRect);
//        Log.e("recyclerview: ","rect "+mRect.left+","+mRect.top+","+mRect.right+","+mRect.bottom+" rawX "+rawX+" rawY "+rawY
//        +" containsPoint: "+mRect.contains(rawX, rawY)+" isReady: "+isReadyForPull(view, rawX - mRect.left, rawY - mRect.top)
//        );
        if (mRect.contains(rawX, rawY)) {
            boolean val=
                    isReadyForPull(view, rawX - mRect.left, rawY - mRect.top);
            return val;
        }
        return false;
    }

    //function specially designed for linearLayoutManager; if any other layoutManager please modify this function
    @Override
    public boolean isReadyForPull(View view, final float x, final float y) {
        boolean ready = false;
        // First we check whether we're scrolled to the top
        RecyclerView recyclerView = (RecyclerView) view;
        LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
//        Log.e(TAG,"isReadyForPull itemCount: "+recyclerView.getAdapter().getItemCount()+
//                " firstVisiblePosition: "+layoutManager.findFirstVisibleItemPosition()+
//                " layoutManagerType: "+
//                (recyclerView.getLayoutManager() instanceof LinearLayoutManager?"LinearLayoutManager":
//                  recyclerView.getLayoutManager() instanceof GridLayoutManager? "GridLayoutManager":"Unknown")
//        );

        if (recyclerView.getAdapter().getItemCount() == 0 ) {
            ready = true;
        } else if (
                layoutManager.findFirstVisibleItemPosition() == 0||
                        layoutManager.findFirstVisibleItemPosition() == 1) {
            final View firstVisibleChild = layoutManager.findViewByPosition(layoutManager.findFirstVisibleItemPosition());
            ready = firstVisibleChild != null
                    && firstVisibleChild.getTop() >= recyclerView.getPaddingTop();
        }

        return ready;
    }
}