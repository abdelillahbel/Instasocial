/*
 * *
 *  Company : Bsetec
 *  Product: Instasocial
 *  Email : support@bsetec.com
 *  Copyright © 2018 BSEtec. All rights reserved.
 *
 */

package com.androidapp.instasocial.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.androidapp.instasocial.R;


public class CompatImageView extends android.support.v7.widget.AppCompatImageView {
    public static final String TAG = CompatImageView.class.getSimpleName();

    int filterColor= Color.TRANSPARENT;

    public CompatImageView(Context context) {
        super(context);
        init(null);
    }

    public CompatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CompatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {


        if (attrs != null) {
            final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CompatImageView);
            if (array != null) {
                filterColor=array.getColor(R.styleable.CompatImageView_filterColor,Color.TRANSPARENT);
                array.recycle();
            }
        }


        if (filterColor!=Color.TRANSPARENT)
        setColorFilter(filterColor);
    }
}
