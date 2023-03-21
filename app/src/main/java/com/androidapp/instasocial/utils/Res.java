/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

public class Res extends Resources {

    public Res(Resources original) {
        super(original.getAssets(), original.getDisplayMetrics(), original.getConfiguration());
    }

    @Override
    public int getColor(int id) throws NotFoundException {

        if(getResourceEntryName(id).equals("colorPrimary"))
        // You can change the return value to an instance field that loads from SharedPreferences.
        return Color.RED;
        else // used as an example. Change as needed.
        return getColor(id, null);
    }

    @Override
    public int getColor(int id, Theme theme) throws NotFoundException {
        switch (getResourceEntryName(id)) {
            case "colorPrimary":
                // You can change the return value to an instance field that loads from SharedPreferences.
                return Color.RED; // used as an example. Change as needed.

            case "colorPrimaryDark":
                // You can change the return value to an instance field that loads from SharedPreferences.
                return Color.GREEN; // used as an example. Change as needed.

             default:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return super.getColor(id, theme);
            }
            return super.getColor(id);
        }

    }
}