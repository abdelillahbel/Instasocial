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
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;

import com.androidapp.instasocial.R;

import java.util.HashMap;
import java.util.Map;


public class CompatEditText extends AppCompatEditText {
    public static final String TAG = CompatTextView.class.getSimpleName();
    private static Map<String, Typeface> mTypefaces;
    Typeface typeface = null;


    public CompatEditText(Context context) {
        super(context);
        init(null);
    }

    public CompatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CompatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }
        String fontName="";
        if (attrs != null) {

            final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CompatEditText);
            if (array != null) {
                fontName = array.getString(R.styleable.CompatEditText_fontPath);
                if (fontName != null) {
                    if (mTypefaces.containsKey(fontName)) {
                        typeface = mTypefaces.get(fontName);
                    } else {
                        try {
                            typeface = Typeface.createFromAsset(getContext().getAssets(), fontName + CompatTextView.getFontExtension(fontName));
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


}
