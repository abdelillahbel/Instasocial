/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui.Observablescroll;

import android.view.ViewGroup;

/**
 * Provides common API for observable and scrollable widgets.
 */
public interface Scrollable {
    /**
     * Sets a callback listener.
     *
     * @param listener listener to set
     */
    @Deprecated
    void setScrollViewCallbacks(ObservableScrollViewCallbacks listener);

    /**
     *  Add a callback listener
     *
     *  @param listener listener to add
     * */
    void addScrollViewCallbacks(ObservableScrollViewCallbacks listener);

    /**
     * Remove a callback listener
     *
     * @param listener to remove
     * */
    void removeScrollViewCallbacks(ObservableScrollViewCallbacks listener);

    /**
     * Clear callback listeners
     *
     * */
    void clearScrollViewCallbacks();

    /**
     * Scrolls vertically to the absolute Y.
     * Implemented classes are expected to scroll to the exact Y pixels from the top,
     * but it depends on the type of the widget.
     *
     * @param y vertical position to scroll to
     */
    void scrollVerticallyTo(int y);

    /**
     * Returns the current Y of the scrollable view.
     *
     * @return current Y pixel
     */
    int getCurrentScrollY();
    void setCurrentScrollY(int x);
    /**
     * Sets a touch motion event delegation ViewGroup.
     * This is used to pass motion events back to parent view.
     * It's up to the implementation classes whether or not it works.
     *
     * @param viewGroup ViewGroup object to dispatch motion events
     */
    void setTouchInterceptionViewGroup(ViewGroup viewGroup);
}
