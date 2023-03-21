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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidapp.instasocial.R;

public class MenuBtn extends FrameLayout {

//    protected TextView m_viewTitle;
    protected TextView m_viewBubble;
    protected ImageView m_viewIcon;
    protected RelativeLayout m_viewBtnFrame;


    public MenuBtn(Context context, String sTitle, String sBubble, int iIconResource) {
        this(context, R.layout.layout_menu_btn, sTitle, sBubble);

        m_viewIcon =  findViewById(R.id.home_btn_icon);
        if (iIconResource > 0)
            m_viewIcon.setImageResource(iIconResource);
        else
            m_viewIcon.setImageResource(R.drawable.ic_site_view);

        // TODO: resize buttons depending on - getResources().getDisplayMetrics().density
    }

    public MenuBtn(Context context, int iLayout, String sTitle, String sBubble) {
        super(context);

        LayoutInflater.from(context).inflate(iLayout, this, true);

//        m_viewTitle =  findViewById(R.id.home_btn_title);
        m_viewBubble =  findViewById(R.id.home_btn_bubble);
        m_viewBtnFrame =  findViewById(R.id.home_btn);

//        m_viewTitle.setText(sTitle);

        if (sBubble.length() > 0 && !sBubble.equals("0"))
            m_viewBubble.setText(sBubble);
        else
            m_viewBubble.setVisibility(INVISIBLE);
    }

    public MenuBtn(Context context, String sTitle) {
        this(context, sTitle, "", 0);
    }

    public View getBtn() {
        return m_viewBtnFrame;
    }

//    public void getTitleText(String s) {
//        m_viewTitle.setText(s);
//    }

    public void getBubbleText(String s) {
        m_viewBubble.setText(s);
        m_viewBubble.setVisibility(s.length() > 0 && !s.equals("0") ? VISIBLE : INVISIBLE);
    }
}
