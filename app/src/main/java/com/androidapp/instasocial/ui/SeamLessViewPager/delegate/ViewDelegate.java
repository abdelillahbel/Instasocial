/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */
package com.androidapp.instasocial.ui.SeamLessViewPager.delegate;

import android.view.View;

public interface ViewDelegate {

    /**
     * Allows you to provide support for View which do not have built-in
     * support. In this method you should cast <code>view</code> to it's
     * native class, and check if it is scrolled to the top.
     *
     * @param view The view which has should be checked against.
     * @param x The X co-ordinate of the touch event
     * @param y The Y co-ordinate of the touch event
     * @return true if <code>view</code> is scrolled to the top.
     */
    public boolean isReadyForPull(View view, float x, float y);
}