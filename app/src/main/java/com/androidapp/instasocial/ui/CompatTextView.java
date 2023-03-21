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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.androidapp.instasocial.R;

import java.util.HashMap;
import java.util.Map;


public class CompatTextView extends android.support.v7.widget.AppCompatTextView {
    public static final String TAG = CompatTextView.class.getSimpleName();
    private static Map<String, Typeface> mTypefaces;
    private Typeface typeface = null;


    public CompatTextView(Context context) {
        super(context);
        init(null);
    }

    public CompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }

        String fontName = null;

        if (attrs != null) {
            final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CompatTextView);
            if (array != null) {
                fontName = array.getString(R.styleable.CompatTextView_fontPath);
                if (fontName != null) {
                    if (mTypefaces.containsKey(fontName)) {
                        typeface = mTypefaces.get(fontName);
                    } else {
                        try {
                            typeface = Typeface.createFromAsset(getContext().getAssets(), fontName + getFontExtension(fontName));
                            mTypefaces.put(fontName, typeface);
                        } catch (Exception e) {
                            Log.e(TAG, "Asset named with \"" + fontName + "\" not found");
                        }
                    }
                }
                array.recycle();
            }
        }


        if (typeface != null)
            setTypeface(typeface);
    }

    public static String getFontExtension(String name) {
        String extension ;
        int i = name.lastIndexOf('.');
        if (i > 0) {
            extension = "";
        }else {
            extension=".ttf";
        }
        return extension;
    }
}
